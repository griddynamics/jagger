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
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.MetricDetails;
import com.griddynamics.jagger.engine.e1.collector.MetricCollector;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.Master;
import com.griddynamics.jagger.master.SessionIdProvider;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.storage.fs.logging.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 18.03.13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class MetricLogProcessor extends LogProcessor implements DistributionListener {

    private static final Logger log = LoggerFactory.getLogger(Master.class);

    private LogAggregator logAggregator;
    private LogReader logReader;
    private SessionIdProvider sessionIdProvider;
    private int pointCount;

    @Required
    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }

    @Required
    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
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

            TaskData taskData = getTaskData(taskId, sessionId);
            if (taskData == null) {
                log.error("TaskData not found by taskId: {}", taskId);
                return;
            }

            String dir = sessionId + File.separatorChar + taskId + File.separatorChar + MetricCollector.METRIC_MARKER;

            String file = dir + File.separatorChar + "aggregated.dat";
            AggregationInfo aggregationInfo = logAggregator.chronology(dir, file);

            if(aggregationInfo.getCount()==0){
                //metrics not collected
                return;
            }
            int intervalSize = (int) ((aggregationInfo.getMaxTime() - aggregationInfo.getMinTime()) / pointCount);
            if (intervalSize < 1) {
                intervalSize = 1;
            }
            StatisticsGenerator statisticsGenerator = new StatisticsGenerator(file, aggregationInfo, intervalSize, taskData).generate();
            final Collection<MetricDetails> statistics = statisticsGenerator.getStatistics();

            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    for (MetricDetails stat : statistics) {
                        session.persist(stat);
                    }
                    session.flush();
                    return null;
                }
            });

        } catch (Exception e) {
            log.error("Error during log processing", e);
        }

    }

    private class StatisticsGenerator {
        private String path;
        private AggregationInfo aggregationInfo;
        private int intervalSize;
        private TaskData taskData;
        private Collection<MetricDetails> statistics;

        public StatisticsGenerator(String path, AggregationInfo aggregationInfo, int intervalSize, TaskData taskData) {
            this.path = path;
            this.aggregationInfo = aggregationInfo;
            this.intervalSize = intervalSize;
            this.taskData = taskData;
        }

        public Collection<MetricDetails> getStatistics() {
            return statistics;
        }

        public StatisticsGenerator generate() throws IOException {
            statistics = new LinkedList<MetricDetails>();
            Map<String, MetricStatistics> metrics = new HashMap<String, MetricStatistics>();

            LogReader.FileReader<MetricLogEntry> fileReader = null;
            try {
                fileReader = logReader.read(path, MetricLogEntry.class);
                for (MetricLogEntry logEntry : fileReader) {
                    MetricStatistics stat = metrics.get(logEntry.getMetricName());
                    if (stat == null) {
                        stat = new MetricStatistics(aggregationInfo.getMinTime() + intervalSize);
                        metrics.put(logEntry.getMetricName(), stat);
                    }
                    log.debug("Log entry {} time", logEntry.getTime());

                    while (logEntry.getTime() > stat.currentInterval) {
                        log.debug("processing count {} interval {}", stat.currentCount, intervalSize);

                        if (stat.currentCount > 0) {
                            statistics.add(new MetricDetails(stat.time, logEntry.getMetricName(), stat.currentCount, taskData));
                            stat.currentCount = 0;
                        } else {
                            statistics.add(new MetricDetails(stat.time, logEntry.getMetricName(), 0l, taskData));
                        }
                        stat.time += intervalSize;
                        stat.currentInterval += intervalSize;
                    }
                    stat.currentCount += logEntry.getMetric();
                }
            } finally {
                if (fileReader != null) {
                    fileReader.close();
                }
            }

            return this;
        }
    }

    private class MetricStatistics {
        //for separating time-line in metrics
        int currentCount;
        long currentInterval;
        long time;

        private MetricStatistics(long currentInterval) {
            this.currentInterval = currentInterval;
        }
    }
}

