package com.griddynamics.jagger.dbapi.fetcher;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.TestInfoDto;
import com.griddynamics.jagger.dbapi.util.FetchUtil;
import com.griddynamics.jagger.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.TreeMultimap.create;

/**
 * User: mnovozhilov
 * Date: 6/10/14
 * Time: 4:07 PM
 *
 * This is class is needed because session scope processing of data for test group metrics is equal
 * processing monitoring parameters. We use delegates of required classes.
 */

public class AbstractSessionScopeFetcher<F extends AbstractMetricPlotFetcher> extends PlotsDbMetricDataFetcher {

    private F abstractMetricPlotFetcher;
    private FetchUtil fetchUtil;

    @Autowired
    public void setAbstractMetricPlotFetcher(F abstractMetricPlotFetcher) {
        this.abstractMetricPlotFetcher = abstractMetricPlotFetcher;
    }

    @Autowired
    public void setFetchUtil(FetchUtil fetchUtil) {
        this.fetchUtil = fetchUtil;
    }

    @Override
    protected Set<Pair<MetricNameDto, List<PlotSingleDto>>> fetchData(List<MetricNameDto> metricNames) {
        if (metricNames.isEmpty()) {
            return Collections.emptySet();
        }

        Collection<AbstractMetricPlotFetcher.MetricRawData> allRawData = abstractMetricPlotFetcher.getAllRawData(metricNames);

        if (allRawData.isEmpty()) {
            log.warn("No plot data found for metrics : {}", metricNames);
            return Collections.emptySet();
        }
        return getResult(allRawData, metricNames);
    }


    protected Set<Pair<MetricNameDto, List<PlotSingleDto>>> getResult(Collection<AbstractMetricPlotFetcher.MetricRawData> allRawData,
                                                                      List<MetricNameDto> metricNames) {

        Set<Long> taskIds = new HashSet<>();
        for (MetricNameDto metricName : metricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        Multimap<String, AbstractMetricPlotFetcher.MetricRawData> metricIdRawMap = getMetricIdRawMap(allRawData, taskIds);

        Multimap<MetricNameDto, PlotSingleDto> metricNamePlotMap = ArrayListMultimap.create();
        for (MetricNameDto metricName : metricNames) {
            Collection<AbstractMetricPlotFetcher.MetricRawData> rawDatas;

            if (metricIdRawMap.isEmpty())
                continue;
            rawDatas = metricIdRawMap.get(metricName.getMetricName());
            if (rawDatas == null || rawDatas.isEmpty()) {
                continue;
            }

            metricNamePlotMap.put(metricName, abstractMetricPlotFetcher.assemble(metricName, rawDatas));
        }

        Set<Pair<MetricNameDto, List<PlotSingleDto>>> resultSet = new HashSet<>(metricNames.size());

        for (MetricNameDto metricName : metricNamePlotMap.keySet()) {
            List<PlotSingleDto> plotDatasetDtoList = new ArrayList<>(metricNamePlotMap.get(metricName));
            resultSet.add(Pair.of(metricName, plotDatasetDtoList));
        }

        return resultSet;
    }

    private Multimap<String, AbstractMetricPlotFetcher.MetricRawData> getMetricIdRawMap(
            Collection<AbstractMetricPlotFetcher.MetricRawData> allRawDataForProcessing,
            Set<Long> taskIds) {

        Collection<AbstractMetricPlotFetcher.MetricRawData> allRawData = dataProcess(allRawDataForProcessing, taskIds);

        Multimap<String, AbstractMetricPlotFetcher.MetricRawData> metricIdRawMap = ArrayListMultimap.create();

        for (AbstractMetricPlotFetcher.MetricRawData rawData : allRawData) {
            metricIdRawMap.put(rawData.getMetricId(), rawData);
        }

        return metricIdRawMap;
    }


    private Collection<AbstractMetricPlotFetcher.MetricRawData> dataProcess(Collection<AbstractMetricPlotFetcher.MetricRawData> forProcess,
                                                                            Set<Long> taskIds) {

        List<AbstractMetricPlotFetcher.MetricRawData> result = Lists.newArrayList();
        Map<Long, Map<String, TestInfoDto>> testStartEndMap = fetchUtil.getTestInfoByTaskIds(taskIds);
        Map<String, Multimap<Number, AbstractMetricPlotFetcher.MetricRawData>> lines = getLineForSessionScope(forProcess);

        for (String metricId : lines.keySet()) {
            Long lastTimeOfPreviousTask = 0L;
            Long timeShift = 0L;
            Long finishTimeFirstTask = 0L;
            Long startTimeSecondTask = null;
            Long startTimePre = null;
            for (Long taskId : testStartEndMap.keySet()) {
                Long testStartTime = testStartEndMap.get(taskId).values().iterator().next().getStartTime().getTime();
                Long testEndTime = testStartEndMap.get(taskId).values().iterator().next().getEndTime().getTime();

                if (lines.get(metricId).containsKey(taskId) && !testStartTime.equals(startTimePre)) {
                    for (AbstractMetricPlotFetcher.MetricRawData metricRawData : lines.get(metricId).get(taskId)) {

                        if (startTimeSecondTask == null)
                            startTimeSecondTask = testStartTime;
                        if (!startTimeSecondTask.equals(testStartTime)) {
                            startTimeSecondTask = testStartTime;
                            timeShift = startTimeSecondTask - finishTimeFirstTask + lastTimeOfPreviousTask;
                        }

                        metricRawData.setTime(metricRawData.getTime() + timeShift);
                        result.add(metricRawData);

                        lastTimeOfPreviousTask = metricRawData.getTime();
                        finishTimeFirstTask = testEndTime;
                    }
                }
                startTimePre = testStartTime;
            }
        }

        return result;
    }

    private Map<String, Multimap<Number, AbstractMetricPlotFetcher.MetricRawData>> getLineForSessionScope(
            Collection<AbstractMetricPlotFetcher.MetricRawData> forProcess) {
        Map<String, Multimap<Number, AbstractMetricPlotFetcher.MetricRawData>> lines = new HashMap<>();

        for (AbstractMetricPlotFetcher.MetricRawData metricRawData : forProcess) {
            String metricId = metricRawData.getMetricId();
            if (lines.get(metricId) == null) {
                Multimap<Number, AbstractMetricPlotFetcher.MetricRawData> multimap = create(numberComparator, metricRawDataComparator);
                lines.put(metricId, multimap);
            }
            lines.get(metricId).put(metricRawData.getWorkloadTaskDataId(), metricRawData);
        }
        return lines;
    }

    private static Comparator<AbstractMetricPlotFetcher.MetricRawData> metricRawDataComparator = (s1, s2) -> {
        Long s1Long = s1.getTime();
        return s1Long.compareTo(s2.getTime());
    };

    private static Comparator<Number> numberComparator = (s1, s2) -> {
        Long s1Long = s1.longValue();
        return s1Long.compareTo(s2.longValue());
    };


}
