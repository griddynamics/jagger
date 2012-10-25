package com.griddynamics.jagger.engine.e1.aggregator.workload;

import com.caucho.hessian.io.Hessian2Input;
import com.google.common.collect.Lists;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DurationMetricLatencyPercentile;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DurationMetricStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.TimeInvocationStatistics;
import com.griddynamics.jagger.engine.e1.collector.MultiDurationCollector;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.SessionIdProvider;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.storage.FileStorage;
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
import java.util.*;

/**
 * @author Sergey Pulyaev
 * Date: 23.10.12
 */
public class MultiDurationLogProcessor  extends LogProcessor implements DistributionListener {

    private static final Logger log = LoggerFactory.getLogger(MultiDurationLogProcessor.class);

    private FileStorage fileStorage;
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

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        // do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        if (task instanceof WorkloadTask) {
            String dir = sessionId + "/" + taskId + "/" + MultiDurationCollector.DURATION_MARKER;
            try {
                Set<String> metricsNames = fileStorage.getFileNameList(dir);
                for (String metric : metricsNames) {
                    processLog(sessionIdProvider.getSessionId(), taskId, metric, new File(metric).getName());
                }
            } catch (Exception e) {
                log.error("Error during log processing", e);
            }
        }
    }

    private void processLog(String sessionId, String taskId, String dir, String metricName) {
        Hessian2Input in = null;
        String file = null;
        try {
            file = dir + "/aggregated.dat";
            AggregationInfo aggregationInfo = logAggregator.chronology(dir, file);

            int intervalSize = (int) ((aggregationInfo.getMaxTime() - aggregationInfo.getMinTime()) / pointCount);
            if (intervalSize < 1) {
                intervalSize = 1;
            }

            in = new Hessian2Input(fileStorage.open(file));

            TaskData taskData = getTaskData(taskId, sessionId);
            if (taskData == null) {
                log.error("TaskData not found by taskId: {}", taskId);
                return;
            }

            StatisticsGenerator statisticsGenerator = new StatisticsGenerator(file, aggregationInfo, intervalSize, taskData).generate();
            final Collection<TimeInvocationStatistics> statistics = statisticsGenerator.getStatistics();
            final DurationMetricStatistics durationMetricDescriptiveStatistics = statisticsGenerator.getDurationMetricDescriptiveStatistics();
            durationMetricDescriptiveStatistics.setName(metricName);
            durationMetricDescriptiveStatistics.setCount(aggregationInfo.getCount());
            durationMetricDescriptiveStatistics.setMaxTime(aggregationInfo.getMaxTime());
            durationMetricDescriptiveStatistics.setMinTime(aggregationInfo.getMinTime());

            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    for (TimeInvocationStatistics stat : statistics) {
                        session.persist(stat);
                    }
                    session.persist(durationMetricDescriptiveStatistics);
                    session.flush();
                    return null;
                }
            });

        } catch (Exception e) {
            log.error("Error during log processing", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
//                    fileStorage.delete(file, true);
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    private class StatisticsGenerator {
        private String path;
        private AggregationInfo aggregationInfo;
        private int intervalSize;
        private TaskData taskData;
        private Collection<TimeInvocationStatistics> statistics;
        private DurationMetricStatistics durationMetricDescriptiveStatistics;

        public StatisticsGenerator(String path, AggregationInfo aggregationInfo, int intervalSize, TaskData taskData) {
            this.path = path;
            this.aggregationInfo = aggregationInfo;
            this.intervalSize = intervalSize;
            this.taskData = taskData;
        }

        public Collection<TimeInvocationStatistics> getStatistics() {
            return statistics;
        }

        public DurationMetricStatistics getDurationMetricDescriptiveStatistics() {
            return durationMetricDescriptiveStatistics;
        }

        public StatisticsGenerator generate() throws IOException {
            statistics = new ArrayList<TimeInvocationStatistics>();

            long currentInterval = aggregationInfo.getMinTime() + intervalSize;
            long time = 0;
            int currentCount = 0;
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
                            double throughput = (double) currentCount * 1000 / intervalSize;
                            statistics.add(assembleInvocationStatistics(time, windowStatisticsCalculator, throughput, taskData));
                            currentCount = 0;
                            windowStatisticsCalculator.reset();
                        } else {
                            statistics.add(new TimeInvocationStatistics(time, 0d, 0d, 0d, taskData));
                        }
                        time += intervalSize;
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
                statistics.add(assembleInvocationStatistics(time, windowStatisticsCalculator, throughput, taskData));
            }

            durationMetricDescriptiveStatistics = assembleMetricDescriptiveScenarioStatistics(globalStatisticsCalculator, taskData);
            return this;
        }
    }

    private List<Double> globalPercentilesKeysLocal;

    @Required
    public void setGlobalPercentilesKeys(List<Double> globalPercentilesKeys) {
        this.globalPercentilesKeysLocal = globalPercentilesKeys;
        super.setGlobalPercentilesKeys(globalPercentilesKeys);
    }

    protected DurationMetricStatistics assembleMetricDescriptiveScenarioStatistics(StatisticsCalculator calculator, TaskData taskData) {
        DurationMetricStatistics statistics = new DurationMetricStatistics();
        statistics.setTaskData(taskData);

        List<DurationMetricLatencyPercentile> percentiles = Lists.newArrayList();
        for (double percentileKey : globalPercentilesKeysLocal) {
            double percentileValue = calculator.getPercentile(percentileKey);
            DurationMetricLatencyPercentile percentile = new DurationMetricLatencyPercentile(percentileKey, percentileValue);
            percentile.setDurationMetricStatistic(statistics);
            if (Double.isNaN(percentileKey) || Double.isNaN(percentileValue))
                log.error("Percentile has NaN values : key={}, value={}", percentileKey, percentileValue);
            percentiles.add(percentile);
        }
        if (!percentiles.isEmpty()) {
            statistics.setPercentiles(percentiles);
        }
        return statistics;
    }
}
