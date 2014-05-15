package com.griddynamics.jagger.dbapi.fetcher;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotDatasetDto;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import com.griddynamics.jagger.util.Pair;

import java.util.*;


public abstract class StandardMetricPlotFetcher<T extends StandardMetricPlotFetcher.StandardMetricRawData> extends PlotsDbMetricDataFetcher {
    @Override
    protected Set<Pair<MetricNameDto, List<PlotDatasetDto>>> fetchData(List<MetricNameDto> metricNames) {
        if (metricNames.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Pair<MetricNameDto, List<PlotDatasetDto>>> resultSet = new HashSet<Pair<MetricNameDto, List<PlotDatasetDto>>>(metricNames.size());
        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto metricNameDto : metricNames) {
            taskIds.addAll(metricNameDto.getTaskIds());
        }

        List<T> rawAllData = findRawDataByTaskData(taskIds);

        if (rawAllData.isEmpty()) {
            throw new RuntimeException("Could not find plot data for " + metricNames);
        }

        Multimap<Long, T> taskIdRawMap = ArrayListMultimap.create();
        for (T rawData : rawAllData) {
            Long taskDataId = rawData.getTaskDataId();
            taskIdRawMap.put(taskDataId, rawData);
        }

        Multimap<MetricNameDto, PlotDatasetDto> metricNamePlotMap = ArrayListMultimap.create();

        Map<Long,  MetricNameDto> mappedMetricNames = MetricNameUtil.getMappedMetricDtosByTaskIds(metricNames);

        for (Long taskDataId : taskIdRawMap.keySet()) {

            MetricNameDto metricName;
            try {
                metricName = mappedMetricNames.get(taskDataId);
            } catch (NullPointerException e) {
                throw new RuntimeException("cant find metric name dto in MetricNameUtil.getMappedMetricDtos(metricNames) " + metricNames);
            }

            Collection<T> rawData =  taskIdRawMap.get(taskDataId);
            if (rawData.isEmpty()) {
                throw new RuntimeException("no plot data found for TaskDataId : " + taskDataId);
            }
            metricNamePlotMap.putAll(metricName, assemble(rawData));
        }

        for (MetricNameDto metricName : metricNamePlotMap.keySet()) {
            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>(metricNamePlotMap.get(metricName));
            resultSet.add(
                    Pair.of(
                            metricName,
                            plotDatasetDtoList
                    ));
        }

        return resultSet;
    }


    /**
     * assemble raw data of one taskDataId
     * @param rawData never null or empty
     */
    protected abstract Iterable<? extends PlotDatasetDto> assemble(Collection<T> rawData);


    /**
     * @param taskIds ids of TaskData
     */
    protected abstract List<T> findRawDataByTaskData(Set<Long> taskIds);


    /**
     * Interface of Raw data.
     * It should contain getTaskDataId() method for StandardMetricPlotFetcher logic. Namely one TaskData id -> one curve
     */
    public static interface StandardMetricRawData {

        public Long getTaskDataId();
    }
}
