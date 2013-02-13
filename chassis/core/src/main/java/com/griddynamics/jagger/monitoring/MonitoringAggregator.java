/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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
package com.griddynamics.jagger.monitoring;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.agent.model.MonitoringParameterLevel;
import com.griddynamics.jagger.agent.model.SystemUnderTestInfo;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.diagnostics.thread.sampling.ProfileDTO;
import com.griddynamics.jagger.diagnostics.thread.sampling.RuntimeGraph;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.monitoring.model.MonitoringStatistics;
import com.griddynamics.jagger.monitoring.model.PerformedMonitoring;
import com.griddynamics.jagger.monitoring.model.ProfilingSuT;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.fs.logging.*;
import com.griddynamics.jagger.util.SerializationUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Aggregates monitoring information.
 *
 * @author Mairbek Khadikov
 */
public class MonitoringAggregator extends LogProcessor implements DistributionListener {
    private static Logger log = LoggerFactory.getLogger(MonitoringAggregator.class);

    private LogAggregator logAggregator;
    private FileStorage fileStorage;

    private LogReader logReader;
    private int pointCount;

    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        // do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        if (task instanceof MonitoringTask) {
            persistEntry(sessionId, taskId, (MonitoringTask) task);

            log.debug("Going to aggregate monitoring details for task id {}", taskId);
            aggregateLogs(sessionId, taskId);
            log.debug("Monitoring details aggregation completed for task id {}", taskId);
        }
    }

    private void persistEntry(String sessionId, String taskId, MonitoringTask monitoringTask) {
        String parentId = monitoringTask.getParentTaskId();
        String name = monitoringTask.getTaskName();
        String termination = monitoringTask.getTerminationStrategy().toString();

        log.debug("Storing monitoring entry. Task id {} parent id {}", taskId, parentId);
        PerformedMonitoring performedMonitoring = new PerformedMonitoring();
        performedMonitoring.setSessionId(sessionId);
        performedMonitoring.setMonitoringId(taskId);
        performedMonitoring.setParentId(parentId);
        performedMonitoring.setName(name);
        performedMonitoring.setTermination(termination);
        getHibernateTemplate().persist(performedMonitoring);
    }

    private void aggregateLogs(String sessionId, String taskId) {
        String dir = sessionId + "/" + taskId + "/" + LoggingMonitoringProcessor.MONITORING_MARKER;
        String aggregatedFile = dir + "/aggregated.dat";
        try {
            AggregationInfo aggregationInfo = logAggregator.chronology(dir, aggregatedFile);

            long intervalSize = (aggregationInfo.getMaxTime() - aggregationInfo.getMinTime()) / (long) (pointCount - 1);
            if (intervalSize < 1) {
                intervalSize = 1;
            }

            TaskData taskData = getTaskData(taskId, sessionId);
            if (taskData == null) {
                log.error("TaskData not found by taskId: {}", taskId);
                return;
            }

            long currentInterval = aggregationInfo.getMinTime() + intervalSize;
            final Map<NodeId, Map<MonitoringParameter, Double>> sumByIntervalAgent = Maps.newHashMap();
            final Map<NodeId, Map<MonitoringParameter, Long>> countByIntervalAgent = Maps.newHashMap();
            final Map<String, Map<MonitoringParameter, Double>> sumByIntervalSuT = Maps.newHashMap();
            final Map<String, Map<MonitoringParameter, Long>> countByIntervalSuT = Maps.newHashMap();
            final ListMultimap<MonitoringStream, MonitoringStatistics> avgStatisticsByAgent = ArrayListMultimap.create();
            final ListMultimap<MonitoringStream, MonitoringStatistics> avgStatisticsBySuT = ArrayListMultimap.create();

            LogReader.FileReader<MonitoringLogEntry> fileReader = logReader.read(aggregatedFile, MonitoringLogEntry.class);
            Iterator<MonitoringLogEntry> it = fileReader.iterator();
            while (it.hasNext()) {
                MonitoringLogEntry logEntry = it.next();
                    try{
                        currentInterval = processLogEntry(sessionId, aggregationInfo, intervalSize, taskData, currentInterval,
                                sumByIntervalAgent, countByIntervalAgent, sumByIntervalSuT, countByIntervalSuT, avgStatisticsByAgent,
                                avgStatisticsBySuT, logEntry);
                    } catch (ClassCastException e){
                        //HotFix for hessian de/serialization problem
                        log.error("Deserialization problem: {}",e);
                    }
            }

            fileReader.close();

            finalizeIntervalSysInfo(sessionId, taskData, currentInterval - aggregationInfo.getMinTime(),
                    sumByIntervalAgent, countByIntervalAgent, avgStatisticsByAgent);
            finalizeIntervalSysUT(sessionId, taskData, currentInterval - aggregationInfo.getMinTime(),
                    sumByIntervalSuT, countByIntervalSuT, avgStatisticsBySuT);
            
            differentiateRelativeParameters(avgStatisticsByAgent);
            differentiateRelativeParameters(avgStatisticsBySuT);

            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    for (MonitoringStatistics stat : avgStatisticsByAgent.values()) {
                        session.persist(stat);
                    }
                    for (MonitoringStatistics stat : avgStatisticsBySuT.values()) {
                        session.persist(stat);
                    }
                    session.flush();
                    return null;
                }
            });

            saveProfilers(sessionId, taskId);

        } catch (Exception e) {
            log.error("Error during log processing", e);
        }
    }

    private void saveProfilers(final String sessionId, final String taskId) throws IOException {
        String dir;
        dir = sessionId + "/" + taskId + "/" + MonitorProcess.PROFILER_MARKER;

        Set<String> fileNameList = fileStorage.getFileNameList(dir);
        if (fileNameList.isEmpty()) {
            log.debug("Directory {} is empty.", dir);
            return;
        }
        for (final String fileName : fileNameList) {
            LogReader.FileReader reader;
            try {
                reader = logReader.read(fileName, Object.class);
            } catch (IllegalArgumentException e) {
                log.warn(e.getMessage(), e);
                return;
            }
            final ProfileDTO profileDTO = SerializationUtils.fromString(reader.iterator().next().toString());

            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    String prefix = "Agent on (" + profileDTO.getHostAddress() + ") : ";
                    for (Map.Entry<String, RuntimeGraph> runtimeGraphEntry : profileDTO.getRuntimeGraphs().entrySet()) {
                        String context = SerializationUtils.toString(runtimeGraphEntry.getValue());
                        session.persist(new ProfilingSuT(prefix + runtimeGraphEntry.getKey(), sessionId,
                                getTaskData(taskId, sessionId), context));
                    }
                    session.flush();
                    return null;
                }
            });
        }
    }

    private long processLogEntry(String sessionId, AggregationInfo aggregationInfo, long intervalSize, TaskData taskData,
                                 long currentInterval, Map<NodeId, Map<MonitoringParameter, Double>> sumByIntervalAgent,
                                 Map<NodeId, Map<MonitoringParameter, Long>> countByIntervalAgent,
                                 Map<String, Map<MonitoringParameter, Double>> sumByIntervalSuT,
                                 Map<String, Map<MonitoringParameter, Long>> countByIntervalSuT,
                                 ListMultimap<MonitoringStream, MonitoringStatistics> avgStatisticsByAgent,
                                 ListMultimap<MonitoringStream, MonitoringStatistics> avgStatisticsBySuT,
                                 MonitoringLogEntry logEntry) {
        while (logEntry.getTime() > currentInterval) {
            finalizeIntervalSysInfo(sessionId, taskData, currentInterval - aggregationInfo.getMinTime(),
                    sumByIntervalAgent, countByIntervalAgent, avgStatisticsByAgent);
            finalizeIntervalSysUT(sessionId, taskData, currentInterval - aggregationInfo.getMinTime(),
                    sumByIntervalSuT, countByIntervalSuT, avgStatisticsBySuT);
            sumByIntervalAgent.clear();
            countByIntervalAgent.clear();
            sumByIntervalSuT.clear();
            countByIntervalSuT.clear();
            currentInterval += intervalSize;
        }
        Map<String, SystemUnderTestInfo> sysUnderTest = logEntry.getSystemInfo().getSysUnderTest();
        if (sysUnderTest != null) {
            for (String url : sysUnderTest.keySet()) {
                Map<MonitoringParameter, Double> sumSysUnderTestMetrics = sumByIntervalSuT.get(url);
                Map<MonitoringParameter, Long> countSysUnderTestMetrics = countByIntervalSuT.get(url);
                if (sumSysUnderTestMetrics == null) {
                    sumSysUnderTestMetrics = Maps.newHashMap();
                    sumByIntervalSuT.put(url, sumSysUnderTestMetrics);
                    countSysUnderTestMetrics = Maps.newHashMap();
                    countByIntervalSuT.put(url, countSysUnderTestMetrics);
                }
                for (Map.Entry<MonitoringParameter, Double> entry : sysUnderTest.get(url).getSysUTInfo().entrySet()) {
                    Double prevSum = sumSysUnderTestMetrics.get(entry.getKey());
                    double value = entry.getValue();
                    if (prevSum != null) {
                        if (!entry.getKey().isCumulativeCounter()) {
                            value += prevSum;
                        } else {
                            value = Math.max(value, prevSum);
                        }
                    }
                    sumSysUnderTestMetrics.put(entry.getKey(), value);
                    Long prevCount = countSysUnderTestMetrics.get(entry.getKey());
                    countSysUnderTestMetrics.put(entry.getKey(), (prevCount == null || entry.getKey().isCumulativeCounter()) ? 1 : prevCount + 1);
                }
            }
        }
        Map<MonitoringParameter, Double> sysInfo = logEntry.getSystemInfo().getSysInfo();
        if (sysInfo != null) {
            NodeId nodeId = logEntry.getSystemInfo().getNodeId();
            for (Map.Entry<MonitoringParameter, Double> entry : sysInfo.entrySet()) {
                Map<MonitoringParameter, Double> sumMonitoringAgent = sumByIntervalAgent.get(nodeId);
                Map<MonitoringParameter, Long> countsMonitoringAgent = countByIntervalAgent.get(nodeId);
                if (sumMonitoringAgent == null) {
                    sumMonitoringAgent = Maps.newHashMap();
                    sumByIntervalAgent.put(nodeId, sumMonitoringAgent);
                    countsMonitoringAgent = Maps.newHashMap();
                    countByIntervalAgent.put(nodeId, countsMonitoringAgent);
                }
                Double prevSum = sumMonitoringAgent.get(entry.getKey());
                double value = entry.getValue();
                if (prevSum != null) {
                    if (!entry.getKey().isCumulativeCounter()) {
                        value += prevSum;
                    } else {
                        value = Math.max(value, prevSum);
                    }
                }
                sumMonitoringAgent.put(entry.getKey(), value);
                Long prevCount = countsMonitoringAgent.get(entry.getKey());
                countsMonitoringAgent.put(entry.getKey(), (prevCount == null || entry.getKey().isCumulativeCounter()) ? 1 : prevCount + 1);
            }
        }
        return currentInterval;
    }

    private static void differentiateRelativeParameters(ListMultimap<MonitoringStream, ? extends MonitoringStatistics> aggregatedData) {
        for (MonitoringStream stream : aggregatedData.keySet()) {
            if (stream.monitoringParameter.isCumulativeCounter()) {
                List<? extends MonitoringStatistics> trace = aggregatedData.get(stream); // trace is ordered by time
                if (!trace.isEmpty()) {
                    for (int i = trace.size() - 1; i > 0; i--) {
                        MonitoringStatistics statisticsBySuT = trace.get(i);
                        MonitoringStatistics previousStatisticsBySuT = trace.get(i - 1);
                        double diffValue = statisticsBySuT.getAverageValue() - previousStatisticsBySuT.getAverageValue();
                        if (diffValue < 0) {
                            log.warn("Negative cumulative metric has been flushed and will be removed. Difference with previous value by parameter {} is {}: ({} - {})",
                                    new Object[]{statisticsBySuT.getParameterId(), diffValue, statisticsBySuT.getAverageValue(),
                                            previousStatisticsBySuT.getAverageValue()});
                            trace.remove(i);
                        } else {
                            statisticsBySuT.setAverageValue(diffValue);
                        }
                    }
                    trace.get(0).setAverageValue(0d);
                }
            }
        }
    }

    private static void finalizeIntervalSysInfo(String sessionId, TaskData taskData, long currentInterval,
                                                Map<NodeId, Map<MonitoringParameter, Double>> sumByInterval,
                                                Map<NodeId, Map<MonitoringParameter, Long>> countByInterval,
                                                ListMultimap<MonitoringStream, MonitoringStatistics> aggregatedData) {
        for (NodeId nodeId : countByInterval.keySet()) {
            for (DefaultMonitoringParameters parameterId : DefaultMonitoringParameters.values()) {
                MonitoringParameterBean parameter = MonitoringParameterBean.copyOf(parameterId);
                if (parameterId.getLevel() == MonitoringParameterLevel.BOX) {
                    if (!countByInterval.get(nodeId).containsKey(parameterId)) break;
                    Double avgValue = sumByInterval.get(nodeId).get(parameterId) / countByInterval.get(nodeId).get(parameterId);

                    aggregatedData.put(
                            new MonitoringStream(nodeId.getIdentifier(), parameterId),
                            new MonitoringStatistics(nodeId.getIdentifier(), null, sessionId, taskData, currentInterval,
                                    parameter, avgValue)
                    );
                }
            }
        }
    }

    private static void finalizeIntervalSysUT(String sessionId, TaskData taskData, long currentInterval,
                                              Map<String, Map<MonitoringParameter, Double>> sumByInterval,
                                              Map<String, Map<MonitoringParameter, Long>> countByInterval,
                                              ListMultimap<MonitoringStream, MonitoringStatistics> aggregatedData) {
        for (String url : countByInterval.keySet()) {
            for (DefaultMonitoringParameters parameterId : DefaultMonitoringParameters.values()) {
                if (parameterId.getLevel() == MonitoringParameterLevel.SUT) {
                    MonitoringParameterBean parameter = MonitoringParameterBean.copyOf(parameterId);
                    if (!countByInterval.get(url).containsKey(parameterId)) break;
                    Double avgValue = sumByInterval.get(url).get(parameterId) / countByInterval.get(url).get(parameterId);

                    aggregatedData.put(
                            new MonitoringStream(url, parameterId),
                            new MonitoringStatistics(null, url, sessionId, taskData, currentInterval, parameter, avgValue)
                    );
                }
            }
        }
    }

    public void setLogAggregator(LogAggregator logAggregator) {
        this.logAggregator = logAggregator;
    }

    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    private static class MonitoringStream {
        private String sourceId;
        private MonitoringParameter monitoringParameter;

        public MonitoringStream(String sourceId, DefaultMonitoringParameters parameter) {
            this.sourceId = sourceId;
            this.monitoringParameter = parameter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MonitoringStream that = (MonitoringStream) o;

            if (monitoringParameter != null ? !monitoringParameter.equals(that.monitoringParameter) : that.monitoringParameter != null)
                return false;
            if (sourceId != null ? !sourceId.equals(that.sourceId) : that.sourceId != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = sourceId != null ? sourceId.hashCode() : 0;
            result = 31 * result + (monitoringParameter != null ? monitoringParameter.hashCode() : 0);
            return result;
        }
    }

}
