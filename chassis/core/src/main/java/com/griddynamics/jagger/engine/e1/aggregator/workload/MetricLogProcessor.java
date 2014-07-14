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
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.dbapi.entity.MetricDescriptionEntity;
import com.griddynamics.jagger.dbapi.entity.MetricPointEntity;
import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.Master;
import com.griddynamics.jagger.master.SessionIdProvider;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.reporting.interval.CalculatedIntervalSizeProvider;
import com.griddynamics.jagger.reporting.interval.IntervalSizeProvider;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.storage.fs.logging.*;
import com.griddynamics.jagger.util.TimeUnits;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
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
    private IntervalSizeProvider intervalSizeProvider;
    private FileStorage fileStorage;

    private MetricDescription defaultMetricDescription;
    {
        defaultMetricDescription = new MetricDescription("No name metric");
        defaultMetricDescription.setPlotData(false);
        defaultMetricDescription.setShowSummary(true);
        defaultMetricDescription.setAggregators(Arrays.<MetricAggregatorProvider>asList(new SumMetricAggregatorProvider()));
    }

    private KeyValueStorage keyValueStorage;

    public void setKeyValueStorage(KeyValueStorage keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
    }

    @Required
    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
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
        if (task instanceof WorkloadTask || task instanceof CompositeTask) {
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
            String dir = sessionId + File.separatorChar + taskId + File.separatorChar + MetricCollector.METRIC_MARKER + File.separatorChar;
            Set<String> metrics = fileStorage.getFileNameList(dir);

            for (String metricPath: metrics) {
                try {
                    String file = metricPath + File.separatorChar + "aggregated.dat";
                    AggregationInfo aggregationInfo = logAggregator.chronology(metricPath, file);


                    if(aggregationInfo.getCount() == 0) {
                        //metric not collected
                        return;
                    }
                    StatisticsGenerator statisticsGenerator = new StatisticsGenerator(file, aggregationInfo, intervalSizeProvider, taskData).generate();
                    final Collection<MetricPointEntity> statistics = statisticsGenerator.getStatistics();

                    log.debug("BEGIN: Save to data base " + metricPath);
                    getHibernateTemplate().execute(new HibernateCallback<Void>() {
                        @Override
                        public Void doInHibernate(Session session) throws HibernateException, SQLException {
                            for (MetricPointEntity stat : statistics) {
                                session.persist(stat);
                            }
                            session.flush();
                            return null;
                        }
                    });
                    log.debug("END: Save to data base " + metricPath);
                } catch (Exception e) {
                    log.error("Error during processing metric by path: '{}'",metricPath);
                }
            }

        } catch (Exception e) {
            log.error("Error during log processing", e);
        }

    }

    private class StatisticsGenerator {
        private String path;
        private AggregationInfo aggregationInfo;
        private IntervalSizeProvider intervalSizeProvider;
        private TaskData taskData;
        private Collection<MetricPointEntity> statistics;

        public StatisticsGenerator(String path, AggregationInfo aggregationInfo,  IntervalSizeProvider intervalSizeProvider, TaskData taskData) {
            this.path = path;
            this.aggregationInfo = aggregationInfo;
            this.intervalSizeProvider = intervalSizeProvider;
            this.taskData = taskData;
        }

        public Collection<MetricPointEntity> getStatistics() {
            return statistics;
        }

        public StatisticsGenerator generate() throws IOException {
            String tmp = path.substring(0, path.lastIndexOf(File.separatorChar));
            String metricName = tmp.substring(tmp.lastIndexOf(File.separatorChar) + 1);
            metricName = URLDecoder.decode(metricName, "UTF-8");

            MetricDescription metricDescription = fetchDescription(metricName);

            if (metricDescription == null) {
                log.warn("Aggregators not found for metric: '{}' in task: '{}'; Using default aggregator", metricName, taskData.getTaskId());
                metricDescription = defaultMetricDescription;
                metricDescription.setMetricId(metricName);
            }else{
                // if there are no aggregators - add default sum-aggregator
                if (metricDescription.getAggregators().isEmpty()){
                    log.warn("Aggregators not found for metric: '{}' in task: '{}'; Using default aggregator", metricName, taskData.getTaskId());
                    metricDescription.addAggregator(new SumMetricAggregatorProvider());
                }
            }

            LogReader.FileReader<MetricLogEntry> fileReader = null;
            statistics = new LinkedList<MetricPointEntity>();

            for (Map.Entry<MetricAggregatorProvider, MetricAggregatorSettings> entry : metricDescription.getAggregatorsWithSettings().entrySet()) {
                MetricAggregator overallMetricAggregator = null;
                MetricAggregator intervalAggregator = null;
                MetricAggregatorSettings aggregatorSettings = entry.getValue();

                TimeUnits normalizeByIntervalValue = aggregatorSettings.getNormalizationBy();
                boolean normalizeByTimeRequired = (normalizeByIntervalValue != TimeUnits.NONE);
                boolean normalizeByFullTimeIntervalRequired = aggregatorSettings.isNormalizeOnFullMeasuredInterval();
                long normalizeByInterval = normalizeByIntervalValue.getMilliseconds();
                long intervalSize = getIntervalSize(intervalSizeProvider, aggregatorSettings, aggregationInfo);

                if (metricDescription.getShowSummary())
                    overallMetricAggregator = entry.getKey().provide();

                if (metricDescription.getPlotData())
                    intervalAggregator = entry.getKey().provide();

                if ((metricDescription.getShowSummary()) || (metricDescription.getPlotData())) {

                    MetricAggregator nameAggregator = overallMetricAggregator == null ? intervalAggregator : overallMetricAggregator;

                    String aggregatorName = nameAggregator.getName();
                    String aggregatorIdSuffix = createIdFromName(aggregatorName, normalizeByIntervalValue);
                    String aggregatorDisplayNameSuffix = createAggregatorDisplayNameSuffix(aggregatorName, normalizeByIntervalValue);

                    String displayName = (metricDescription.getDisplayName() == null ? metricDescription.getMetricId() :
                    metricDescription.getDisplayName()) + aggregatorDisplayNameSuffix;
                    String metricId = metricDescription.getMetricId() + '-' + aggregatorIdSuffix;

                    MetricDescriptionEntity metricDescriptionEntity = persistMetricDescription(metricId, displayName, taskData);

                    long currentInterval = aggregationInfo.getMinTime() + intervalSize;
                    long time = intervalSize;

                    long extendedInterval = intervalSize;

                    try {
                        fileReader = logReader.read(path, MetricLogEntry.class);
                        for (MetricLogEntry logEntry : fileReader) {
                            log.debug("Log entry {} time", logEntry.getTime());
                            if (metricDescription.getPlotData()) {
                                while (logEntry.getTime() > currentInterval){
                                    // we leave current interval or current interval is empty
                                    Number aggregated = intervalAggregator.getAggregated();
                                    if (aggregated != null){
                                        // we leave interval
                                        // we have some info in interval aggregator
                                        // we need to save it
                                        double value = aggregated.doubleValue();

                                        // normalize result
                                        if (normalizeByTimeRequired) {
                                            long intervalForNormalization;
                                            if (!normalizeByFullTimeIntervalRequired) {
                                                // normalize by current aggregation interval
                                                intervalForNormalization = extendedInterval;
                                            }
                                            else {
                                                // normalize by time from beginning of the test
                                                intervalForNormalization = time;
                                            }
                                            value = value * normalizeByInterval * 1d / intervalForNormalization;
                                        }

                                        statistics.add(new MetricPointEntity(time - extendedInterval / 2, value, metricDescriptionEntity));
                                        intervalAggregator.reset();

                                        // go for the next interval
                                        extendedInterval = intervalSize;
                                        time += intervalSize;
                                        currentInterval += intervalSize;
                                    }else{
                                        // current interval is empty
                                        // we will extend it
                                        while (logEntry.getTime() > currentInterval){
                                            extendedInterval += intervalSize;
                                            time += intervalSize;
                                            currentInterval += intervalSize;
                                        }
                                    }
                                }
                                intervalAggregator.append(logEntry.getMetric());
                            }
                            if (metricDescription.getShowSummary())
                                overallMetricAggregator.append(logEntry.getMetric());
                        }

                        if (metricDescription.getPlotData()) {
                            Number aggregated = intervalAggregator.getAggregated();
                            if (aggregated != null){
                                double value = aggregated.doubleValue();

                                // normalize result
                                if (normalizeByTimeRequired) {
                                    long intervalForNormalization;
                                    if (!normalizeByFullTimeIntervalRequired) {
                                        // normalize by current aggregation interval
                                        intervalForNormalization = extendedInterval;
                                    }
                                    else {
                                        // normalize by time from beginning of the test
                                        intervalForNormalization = time;
                                    }
                                    value = value * normalizeByInterval * 1d / intervalForNormalization;

                                }
                                statistics.add(new MetricPointEntity(time - extendedInterval / 2, value, metricDescriptionEntity));
                                intervalAggregator.reset();
                            }
                        }

                        if (metricDescription.getShowSummary()) {
                            double value = overallMetricAggregator.getAggregated().doubleValue();
                            if (normalizeByTimeRequired) {
                                value = value * normalizeByInterval * 1d / time;
                            }
                            persistAggregatedMetricValue(value, metricDescriptionEntity);
                        }

                    }
                    finally {
                        if (fileReader != null) {
                            fileReader.close();
                        }
                    }
                }
            }

            return this;
        }

        private int getIntervalSize(IntervalSizeProvider intervalSizeProvider,
                                            MetricAggregatorSettings aggregatorSettings,
                                            AggregationInfo aggregationInfo) {

            long maxTime = aggregationInfo.getMaxTime();
            long minTime = aggregationInfo.getMinTime();
            int pointsCount = aggregatorSettings.getPointCount();
            int aggregationInterval = aggregatorSettings.getPointInterval();

            int intervalSize = intervalSizeProvider.getIntervalSize(minTime, maxTime);

            if (pointsCount > 0) {
                intervalSize = new CalculatedIntervalSizeProvider(pointsCount).getIntervalSize(minTime, maxTime);
            }
            if (intervalSize < 1)
                intervalSize = 1;

            if (aggregationInterval > 0) {
                intervalSize = aggregationInterval;
            }
            return intervalSize;
        }


        private MetricDescription fetchDescription(String metricName) {
            Collection<Object> metricDescription = keyValueStorage.fetchAll(
                    Namespace.of(taskData.getSessionId(), taskData.getTaskId(), "metricDescription"),
                    metricName
            );

            if (!metricDescription.iterator().hasNext()){
                return null;
            }

            return (MetricDescription)metricDescription.iterator().next();
        }

       /**
        * Creates aggregator`s id from aggregator`s displayName.
        * Replace all reserved symbols for aggregator`s name with empty String.
        * Reserved symbols = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" | "$" | ","
        * @param name aggregator`s name
        * @return aggregator`s id
        */
        private String createIdFromName(String name, TimeUnits normalization) {
            String result;
            String regexp = "[\\;/\\?\\:@\\&=\\+\\$\\,]";
            result = name.replaceAll(regexp, "");
            switch (normalization) {
                case NONE:
                    break;
                case SECOND:
                    result += "_per_second";
                    break;
                case MINUTE:
                    result += "_per_minute";
                    break;
                case HOUR:
                    result += "_per_hour";
                    break;
            }
            return result;
        }

        /**
         * Wrap aggregator name to make it more comfortable to read in pdf/webclient
         * @param name aggregator`s name
         * @return suffix for displayName of metric with given aggregator
         */
        private String createAggregatorDisplayNameSuffix(String name, TimeUnits normalization) {
            String result = " [" + name;
            switch (normalization) {
                case NONE:
                    break;
                case SECOND:
                    result += "/second";
                    break;
                case MINUTE:
                    result += "/minute";
                    break;
                case HOUR:
                    result += "/hour";
                    break;
            }
            result += "]";
            return result;
        }

    }
}
