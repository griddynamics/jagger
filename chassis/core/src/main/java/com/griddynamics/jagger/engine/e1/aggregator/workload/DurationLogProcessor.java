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

package com.griddynamics.jagger.engine.e1.aggregator.workload;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.TimeInvocationStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessDescriptiveStatistics;
import com.griddynamics.jagger.engine.e1.collector.DurationCollector;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.Master;
import com.griddynamics.jagger.master.SessionIdProvider;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.reporting.interval.IntervalSizeProvider;
import com.griddynamics.jagger.storage.fs.logging.*;
import com.griddynamics.jagger.util.statistics.StatisticsCalculator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
* @author Alexey Kiselyov
*         Date: 20.07.11
*/
public class DurationLogProcessor extends LogProcessor implements DistributionListener {

    private static final Logger log = LoggerFactory.getLogger(Master.class);

    private LogAggregator logAggregator;
    private LogReader logReader;
    private SessionIdProvider sessionIdProvider;
    private IntervalSizeProvider intervalSizeProvider;

    @Required
    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }

    public void setIntervalSizeProvider(IntervalSizeProvider intervalSizeProvider) {
        this.intervalSizeProvider = intervalSizeProvider;
    }

    @Required
    public void setSessionIdProvider(SessionIdProvider sessionIdProvider) {
        this.sessionIdProvider = sessionIdProvider;
    }

    @Required
    public void setLogAggregator(LogAggregator logAggregator) {
        this.logAggregator = logAggregator;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        // do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        if (task instanceof WorkloadTask) {
            processLog(sessionIdProvider.getSessionId(), taskId);
        }
    }

    private void processLog(String sessionId, String taskId) {
        try {
            String dir = sessionId + File.separatorChar + taskId + File.separatorChar + DurationCollector.DURATION_MARKER;
            String file = dir + File.separatorChar + "aggregated.dat";
            AggregationInfo aggregationInfo = logAggregator.chronology(dir, file);

            if(aggregationInfo.getCount() == 0) {
                //no data collected
                return;
            }

            int intervalSize = intervalSizeProvider.getIntervalSize(aggregationInfo.getMinTime(), aggregationInfo.getMaxTime());
            if (intervalSize < 1) {
                intervalSize = 1;
            }

            TaskData taskData = getTaskData(taskId, sessionId);
            if (taskData == null) {
                log.error("TaskData not found by taskId: {}", taskId);
                return;
            }

            StatisticsGenerator statisticsGenerator = new StatisticsGenerator(file, aggregationInfo, intervalSize, taskData).generate();
            final Collection<TimeInvocationStatistics> statistics = statisticsGenerator.getStatistics();
            final WorkloadProcessDescriptiveStatistics workloadProcessDescriptiveStatistics = statisticsGenerator.getWorkloadProcessDescriptiveStatistics();

            log.info("BEGIN: Save to data base " + dir);
            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    for (TimeInvocationStatistics stat : statistics) {
                        session.persist(stat);
                    }
                    session.persist(workloadProcessDescriptiveStatistics);
                    session.flush();
                    return null;
                }
            });
            log.info("END: Save to data base " + dir);

        } catch (Exception e) {
            log.error("Error during log processing", e);
        }

    }

    private class StatisticsGenerator {
        private String path;
        private AggregationInfo aggregationInfo;
        private int intervalSize;
        private TaskData taskData;
        private Collection<TimeInvocationStatistics> statistics;
        private WorkloadProcessDescriptiveStatistics workloadProcessDescriptiveStatistics;

        public StatisticsGenerator(String path, AggregationInfo aggregationInfo, int intervalSize, TaskData taskData) {
            this.path = path;
            this.aggregationInfo = aggregationInfo;
            this.intervalSize = intervalSize;
            this.taskData = taskData;
        }

        public Collection<TimeInvocationStatistics> getStatistics() {
            return statistics;
        }

        public WorkloadProcessDescriptiveStatistics getWorkloadProcessDescriptiveStatistics() {
            return workloadProcessDescriptiveStatistics;
        }

        public StatisticsGenerator generate() throws IOException {
            statistics = new ArrayList<TimeInvocationStatistics>();

            // starting point is aagregationInfo.getMinTime()
            long currentInterval = aggregationInfo.getMinTime() + intervalSize;
            // starting point is 0
            long time = intervalSize;
            int currentCount = 0;
            int extendedInterval = intervalSize;
            StatisticsCalculator windowStatisticsCalculator = new StatisticsCalculator();
            StatisticsCalculator globalStatisticsCalculator = new StatisticsCalculator();

            LogReader.FileReader<DurationLogEntry> fileReader = null;
            try {
                fileReader = logReader.read(path, DurationLogEntry.class);
                Iterator<DurationLogEntry> it = fileReader.iterator();
                while (it.hasNext()) {
                    DurationLogEntry logEntry = it.next();

                    log.debug("Log entry {} time", logEntry.getTime());

                    while (logEntry.getTime() > currentInterval) {
                        log.debug("processing count {} interval {}", currentCount, intervalSize);

                        if (currentCount > 0) {
                            double throughput = (double) currentCount * 1000 / extendedInterval;
                            statistics.add(assembleInvocationStatistics(time - extendedInterval / 2, windowStatisticsCalculator, throughput, taskData));
                            currentCount = 0;
                            extendedInterval = 0;
                            windowStatisticsCalculator.reset();
                        }
                        time += intervalSize;
                        extendedInterval += intervalSize;
                        currentInterval += intervalSize;
                    }
                    currentCount++;
                    windowStatisticsCalculator.addValue(logEntry.getDuration());
                    globalStatisticsCalculator.addValue(logEntry.getDuration());
                }
            } finally {
                if (fileReader != null) {
                    fileReader.close();
                }
            }

            if (currentCount > 0) {
                double throughput = (double) currentCount * 1000 / intervalSize;
                statistics.add(assembleInvocationStatistics(time - extendedInterval / 2, windowStatisticsCalculator, throughput, taskData));
            }

            workloadProcessDescriptiveStatistics = assembleDescriptiveScenarioStatistics(globalStatisticsCalculator, taskData);
            return this;
        }
    }
}
