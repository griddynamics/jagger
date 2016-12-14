/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.aggregator.workload;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.dbapi.entity.MetricDescriptionEntity;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.dbapi.entity.ValidationResultEntity;
import com.griddynamics.jagger.dbapi.entity.WorkloadData;
import com.griddynamics.jagger.dbapi.entity.WorkloadDetails;
import com.griddynamics.jagger.dbapi.entity.WorkloadTaskData;
import com.griddynamics.jagger.engine.e1.collector.ValidationResult;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.storage.fs.logging.LogProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.CLOCK;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.CLOCK_VALUE;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.END_TIME;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.FAILED;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.INVOKED;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.KERNELS;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.RESULT;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.START_TIME;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.TERMINATION;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.TOTAL_DURATION;
import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.TOTAL_SQR_DURATION;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.DURATION_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.DURATION_SEC;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.ITERATIONS_SAMPLES;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.ITERATION_SAMPLES_ID;

/**
 * Aggregates recorded e1 scenario data from key-value storage to relational
 * table. See {@link com.griddynamics.jagger.dbapi.entity.WorkloadTaskData} for table structure.
 *
 * @author Mairbek Khadikov
 */
public class WorkloadAggregator extends LogProcessor implements DistributionListener {
    private static final Logger log = LoggerFactory.getLogger(WorkloadAggregator.class);

    private KeyValueStorage keyValueStorage;

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        // do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {

        log.debug("Going to perform workload data aggregation");
        if (task instanceof WorkloadTask) {
            aggregateValues(sessionId, taskId, (WorkloadTask) task);
        } else {
            log.debug("Task {} is not a workload task. ignore", task);
        }
    }

    private void aggregateValues(String sessionId, String taskId, WorkloadTask workloadTask) {
        Namespace taskNamespace = Namespace.of(sessionId, taskId);

        String clock = (String) keyValueStorage.fetch(taskNamespace, CLOCK);
        log.debug("Clock {}", clock);
        Integer clockValue = (Integer) keyValueStorage.fetch(taskNamespace, CLOCK_VALUE);
        log.debug("Clock value {}", clockValue);
        String termination = (String) keyValueStorage.fetchNotNull(taskNamespace, TERMINATION);
        log.debug("Termination strategy {}", termination);
        Long startTime = (Long) keyValueStorage.fetchNotNull(taskNamespace, START_TIME);
        Long endTime = (Long) keyValueStorage.fetchNotNull(taskNamespace, END_TIME);
        double duration = (double) (endTime - startTime) / 1000;
        log.debug("start {} end {} duration {}", startTime, endTime, duration);

        @SuppressWarnings({"unchecked", "rawtypes"})
        Collection<String> kernels = (Collection) keyValueStorage.fetchAll(taskNamespace, KERNELS);
        log.debug("kernels found {}", kernels);
        double totalDuration = 0;
        double totalSqrDuration = 0;
        Integer failed = 0;
        Integer invoked = 0;
        Map<String, ValidationResult> validationResults = Maps.newHashMap();
        for (String kernelId : kernels) {
            KernelProcessor kernelProcessor = new KernelProcessor(taskNamespace, totalDuration, totalSqrDuration, failed, invoked,
                    validationResults, kernelId).process();
            invoked = kernelProcessor.getInvoked();
            failed = kernelProcessor.getFailed();
            totalDuration = kernelProcessor.getTotalDuration();
            totalSqrDuration = kernelProcessor.getTotalSqrDuration();
        }

        log.debug("validation result {}", validationResults);

        log.debug("invoked {} failed {}", invoked, failed);
        double avgLatency = 0;
        double stdDevLatency = 0;

        if (invoked > 1) {
            avgLatency = Math.rint(totalDuration / invoked.doubleValue() * 1000) / 1000;
            double avgDuration = totalDuration / invoked.doubleValue();
            stdDevLatency = Math.sqrt(
                    totalSqrDuration / invoked.doubleValue() - avgDuration * avgDuration
            );
            stdDevLatency = Math.rint(stdDevLatency * 1000) / 1000;
        }

        double succeeded = (double) (invoked - failed);
        log.debug("Latency: avg {} stdev {}", avgLatency, stdDevLatency);

        double throughput = Math.rint(succeeded / duration * 100) / 100;
        if (Double.isNaN(throughput)) {
            log.error("throughput is NaN (succeeded={},duration={}). Value for throughput will be set zero", succeeded, duration);
            throughput = 0;
        }
        log.debug("Throughput: {}", throughput);

        double successRate = (invoked.doubleValue() > 0)
                ? Math.rint(succeeded / invoked.doubleValue() * 10000) / 10000
                : 1;
        if (Double.isNaN(successRate)) {
            log.error("successRate is NaN (succeeded={},invoked={}). Value for successRate will be set zero", succeeded, invoked);
            successRate = 0;
        }
        log.debug("Success rate: {}", successRate);

        persistValues(sessionId, taskId, workloadTask, clock, clockValue, termination, startTime, endTime, kernels, failed, invoked,
                validationResults, successRate);
    }

    private void persistValues(String sessionId, String taskId, WorkloadTask workloadTask, String clock, Integer clockValue, String termination,
                               Long startTime, Long endTime, Collection<String> kernels, Integer failed, Integer invoked,
                               Map<String, ValidationResult> validationResults, double successRate) {
        String parentId = workloadTask.getParentTaskId();

        TaskData taskData = getTaskData(taskId, sessionId);
        if (taskData == null) {
            log.error("TaskData not found by sessionId: {} and taskId: {}", sessionId, taskId);
            return;
        }

        WorkloadDetails workloadDetails = getScenarioData(workloadTask);

        WorkloadData testData = new WorkloadData();
        testData.setSessionId(sessionId);
        testData.setTaskId(taskId);
        testData.setParentId(parentId);
        testData.setNumber(workloadTask.getNumber());
        testData.setScenario(workloadDetails);
        testData.setStartTime(new Date(startTime));
        testData.setEndTime(new Date(endTime));

        getHibernateTemplate().persist(testData);

        WorkloadTaskData workloadTaskData = new WorkloadTaskData();
        workloadTaskData.setSessionId(sessionId);
        workloadTaskData.setTaskId(taskId);
        workloadTaskData.setNumber(workloadTask.getNumber());
        workloadTaskData.setScenario(workloadDetails);
        workloadTaskData.setClock(clock);
        workloadTaskData.setClockValue(clockValue);
        workloadTaskData.setTermination(termination);
        workloadTaskData.setKernels(kernels.size());

        getHibernateTemplate().persist(workloadTaskData);

        MetricDescriptionEntity samplesDescription = persistMetricDescription(ITERATION_SAMPLES_ID, ITERATIONS_SAMPLES, taskData);
        persistAggregatedMetricValue(invoked, samplesDescription);
        MetricDescriptionEntity durationDescription = persistMetricDescription(DURATION_ID, DURATION_SEC, taskData);
        Double duration = (endTime - startTime) / 1000.0;
        persistAggregatedMetricValue(duration, durationDescription);

        for (Map.Entry<String, ValidationResult> entry : validationResults.entrySet()) {
            ValidationResultEntity entity = new ValidationResultEntity();
            entity.setWorkloadData(testData);
            entity.setValidator(entry.getKey());
            entity.setDisplayName(entry.getValue().getDisplayName());
            entity.setTotal(entry.getValue().getInvoked());
            entity.setFailed(entry.getValue().getFailed());

            getHibernateTemplate().persist(entity);
        }

    }

    @Required
    public void setKeyValueStorage(KeyValueStorage keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
    }

    private WorkloadDetails getScenarioData(WorkloadTask workloadTask) {
        List<WorkloadDetails> all = (List<WorkloadDetails>) getHibernateTemplate().find(
                "from WorkloadDetails s where s.name=? and s.version=? and s.description=?", workloadTask.getName(), workloadTask.getVersion(),
                workloadTask.getDescription());

        if (all.size() > 0) {
            return all.get(0);
        }
        WorkloadDetails workloadDetails = new WorkloadDetails();
        workloadDetails.setName(workloadTask.getName());
        workloadDetails.setVersion(workloadTask.getVersion());
        workloadDetails.setDescription(workloadTask.getDescription());

        getHibernateTemplate().persist(workloadDetails);
        return workloadDetails;
    }

    private class KernelProcessor {
        private Namespace taskNamespace;
        private double totalDuration;
        private double totalSqrDuration;
        private Integer failed;
        private Integer invoked;
        private Map<String, ValidationResult> validationResults;
        private String kernelId;

        public KernelProcessor(Namespace taskNamespace, double totalDuration, double totalSqrDuration, Integer failed, Integer invoked, Map<String,
                ValidationResult> validationResults, String kernelId) {
            this.taskNamespace = taskNamespace;
            this.totalDuration = totalDuration;
            this.totalSqrDuration = totalSqrDuration;
            this.failed = failed;
            this.invoked = invoked;
            this.validationResults = validationResults;
            this.kernelId = kernelId;
        }

        public double getTotalDuration() {
            return totalDuration;
        }

        public double getTotalSqrDuration() {
            return totalSqrDuration;
        }

        public Integer getFailed() {
            return failed;
        }

        public Integer getInvoked() {
            return invoked;
        }

        public KernelProcessor process() {
            Namespace durationNamespace = taskNamespace.child("DurationCollector", kernelId);

            double commandDuration = 0;
            @SuppressWarnings("unchecked")
            Collection<Double> totalDurations = (Collection) keyValueStorage.fetchAll(durationNamespace, TOTAL_DURATION);
            for (Double d : totalDurations) {
                commandDuration = commandDuration + d;
            }


            double commandSqrDuration = 0;
            @SuppressWarnings("unchecked")
            Collection<Double> totalSqrtDurations = (Collection) keyValueStorage.fetchAll(durationNamespace, TOTAL_SQR_DURATION);
            for (Double totalSqrtDuration : totalSqrtDurations) {
                commandSqrDuration = commandSqrDuration + totalSqrtDuration;
            }

            Namespace informationNamespace = taskNamespace.child("InformationCollector", kernelId);
            Integer invokedOnKernel = 0;
            @SuppressWarnings("unchecked")
            Collection<Integer> invocations = (Collection) keyValueStorage.fetchAll(informationNamespace, INVOKED);
            for (Integer invocation : invocations) {
                invokedOnKernel += invocation;
            }

            Integer failedOnKernel = 0;
            @SuppressWarnings("unchecked")
            Collection<Integer> failedInvocations = (Collection) keyValueStorage.fetchAll(informationNamespace, FAILED);
            for (Integer invocation : failedInvocations) {
                failedOnKernel += invocation;
            }

            log.debug("invoked on kernel {}", invokedOnKernel);
            log.debug("failed on kernel {}", failedOnKernel);

            invoked += invokedOnKernel;
            failed += failedOnKernel;

            totalDuration = totalDuration + commandDuration;
            totalSqrDuration = totalSqrDuration + commandSqrDuration;

            Namespace validationNamespace = taskNamespace.child("ValidationCollector", kernelId);
            @SuppressWarnings("unchecked")
            Collection<ValidationResult> validation = (Collection) keyValueStorage.fetchAll(validationNamespace, RESULT);
            for (ValidationResult validationResult : validation) {
                ValidationResult stat = validationResults.get(validationResult.getName());
                if (stat == null) {
                    validationResults.put(validationResult.getName(), validationResult);
                } else {
                    ValidationResult newVResult = ValidationResult.create(
                            stat.getName(),
                            stat.getDisplayName(),
                            stat.getInvoked() + validationResult.getInvoked(),
                            stat.getFailed() + validationResult.getFailed()
                    );
                    validationResults.put(validationResult.getName(), newVResult);
                }
            }

            return this;
        }
    }
}
