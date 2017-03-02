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

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.dbapi.entity.MetricDescriptionEntity;
import com.griddynamics.jagger.dbapi.entity.MetricPointEntity;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.engine.e1.collector.DurationCollector;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.Master;
import com.griddynamics.jagger.master.SessionIdProvider;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.reporting.interval.IntervalSizeProvider;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.fs.logging.AggregationInfo;
import com.griddynamics.jagger.storage.fs.logging.DurationLogEntry;
import com.griddynamics.jagger.storage.fs.logging.LogAggregator;
import com.griddynamics.jagger.storage.fs.logging.LogProcessor;
import com.griddynamics.jagger.storage.fs.logging.LogReader;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_SEC;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_STD_DEV_AGG_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.THROUGHPUT_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.THROUGHPUT_TPS;

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

    private List<Double> timeWindowPercentilesKeys;
    private List<Double> globalPercentilesKeys;

    private KeyValueStorage keyValueStorage;

    @Required
    public void setKeyValueStorage(KeyValueStorage keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
    }

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

    public List<Double> getTimeWindowPercentilesKeys() {
        return timeWindowPercentilesKeys;
    }

    @Required
    public void setTimeWindowPercentilesKeys(List<Double> timeWindowPercentilesKeys) {
        this.timeWindowPercentilesKeys = new ArrayList<>(new HashSet<>(timeWindowPercentilesKeys));
    }

    public List<Double> getGlobalPercentilesKeys() {
        return globalPercentilesKeys;
    }

    @Required
    public void setGlobalPercentilesKeys(List<Double> globalPercentilesKeys) {
        this.globalPercentilesKeys = new ArrayList<>(new HashSet<>(globalPercentilesKeys));
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

            if (aggregationInfo.getCount() == 0) {
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
            final Collection<MetricPointEntity> statistics = statisticsGenerator.getStatistics();

            log.info("BEGIN: Save to data base " + dir);
            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    for (MetricPointEntity point : statistics) {
                        session.persist(point);
                    }
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
        private Collection<MetricPointEntity> statistics;

        StatisticsGenerator(String path, AggregationInfo aggregationInfo, int intervalSize, TaskData taskData) {
            this.path = path;
            this.aggregationInfo = aggregationInfo;
            this.intervalSize = intervalSize;
            this.taskData = taskData;
            this.statistics = new ArrayList<>();
        }

        Collection<MetricPointEntity> getStatistics() {
            return statistics;
        }

        public StatisticsGenerator generate() throws IOException {
            StatisticsCalculator windowStatisticsCalc = new StatisticsCalculator();
            StatisticsCalculator globalStatisticsCalc = new StatisticsCalculator();
            MetricDescriptionEntity throughputDesc = persistMetricDescription(THROUGHPUT_ID, THROUGHPUT_TPS, taskData);
            MetricDescriptionEntity latencyDesc = persistMetricDescription(LATENCY_ID, LATENCY_SEC, taskData);
            MetricDescriptionEntity latencyStdDevDesc = persistMetricDescription(LATENCY_STD_DEV_AGG_ID, LATENCY_STD_DEV_SEC, taskData);
            Map<Double, MetricDescriptionEntity> percentiles = initPercentileMap();

            // starting point is aggregationInfo.getMinTime()
            long currentInterval = aggregationInfo.getMinTime() + intervalSize;
            // starting point is 0
            long time = intervalSize;
            int currentCount = 0;
            int totalCount = 0;
            int extendedInterval = intervalSize;
            int addedStatistics = 0;
            try (LogReader.FileReader<DurationLogEntry> fileReader = logReader.read(path, DurationLogEntry.class)) {
                for (DurationLogEntry logEntry : fileReader) {
                    log.debug("Log entry {} time", logEntry.getTime());
                    while (logEntry.getTime() > currentInterval) {
                        log.debug("Processing count {} interval {}", currentCount, intervalSize);
                        if (currentCount > 0) {
                            double throughput = (double) currentCount * 1000 / extendedInterval;
                            long currentTime = time - extendedInterval / 2;
                            // first point is removed because it's value very high due to the first invocation of invoker taking longer than the other
                            // and it breaks statistics JFG-729
                            if (++addedStatistics > 1)
                                addStatistics(time, currentTime, throughput, windowStatisticsCalc, throughputDesc, latencyDesc, latencyStdDevDesc, percentiles);
                            currentCount = 0;
                            extendedInterval = 0;
                            windowStatisticsCalc.reset();
                        }
                        time += intervalSize;
                        extendedInterval += intervalSize;
                        currentInterval += intervalSize;
                    }
                    currentCount++;
                    totalCount++;
                    windowStatisticsCalc.addValue(logEntry.getDuration());
                    globalStatisticsCalc.addValue(logEntry.getDuration());
                }
            }
            // first point is removed because it's value very high due to the first invocation of invoker taking longer than the other
            // and it breaks statistics JFG-729
            if (currentCount > 0 && ++addedStatistics > 1) {
                double throughput = (double) currentCount * 1000 / intervalSize;
                long currentTime = time - extendedInterval / 2;
                addStatistics(time, currentTime, throughput, windowStatisticsCalc, throughputDesc, latencyDesc, latencyStdDevDesc, percentiles);
            }

            for (double percentileKey : getGlobalPercentilesKeys()) {
                double percentileValue = globalStatisticsCalc.getPercentile(percentileKey);
                persistAggregatedMetricValue(Math.rint(percentileValue) / 1000D, percentiles.get(percentileKey));
            }
            persistAggregatedMetricValue(Math.rint(globalStatisticsCalc.getMean()) / 1000D, latencyDesc);
            persistAggregatedMetricValue(Math.rint(globalStatisticsCalc.getStandardDeviation()) / 1000D, latencyStdDevDesc);

            Long startTime = aggregationInfo.getMinTime();
            Long endTime = aggregationInfo.getMaxTime();
            double duration = (double) (endTime - startTime) / 1000;
            double totalThroughput = Math.rint(totalCount / duration * 100) / 100;
            persistAggregatedMetricValue(Math.rint(totalThroughput * 100) / 100, throughputDesc);

            return this;
        }

        private Map<Double, MetricDescriptionEntity> initPercentileMap() {
            Map<Double, MetricDescriptionEntity> percentileMap = new HashMap<>(getTimeWindowPercentilesKeys().size());
            for (Double percentileKey : getTimeWindowPercentilesKeys()) {
                String metricId = StandardMetricsNamesUtil.getLatencyMetricId(percentileKey);
                String metricDisplayName = StandardMetricsNamesUtil.getLatencyMetricDisplayName(percentileKey);
                percentileMap.put(percentileKey, persistMetricDescription(metricId, metricDisplayName, taskData));
            }
            return percentileMap;
        }

        private void addStatistics(long time, long currentTime, Double throughput, StatisticsCalculator windowStatisticCalculator,
                                   MetricDescriptionEntity throughputDesc, MetricDescriptionEntity latencyDesc,
                                   MetricDescriptionEntity latencyStdDevDesc, Map<Double, MetricDescriptionEntity> percentileMap) {

            statistics.add(new MetricPointEntity(currentTime, throughput, throughputDesc));
            statistics.add(new MetricPointEntity(currentTime, windowStatisticCalculator.getMean() / 1000, latencyDesc));
            statistics.add(new MetricPointEntity(currentTime, windowStatisticCalculator.getStandardDeviation() / 1000, latencyStdDevDesc));
            for (Double percentileKey : getTimeWindowPercentilesKeys()) {
                Double value = windowStatisticCalculator.getPercentile(percentileKey) / 1000D;
                statistics.add(new MetricPointEntity(time, value, percentileMap.get(percentileKey)));
            }
        }
    }
}
