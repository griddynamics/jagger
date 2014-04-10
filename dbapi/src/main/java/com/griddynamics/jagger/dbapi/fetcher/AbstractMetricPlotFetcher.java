package com.griddynamics.jagger.dbapi.fetcher;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.util.ColorCodeGenerator;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotDatasetDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import com.griddynamics.jagger.util.Pair;

import java.util.*;


/**
 * Abstract class that deal with MetricRawData data to create PlotDatasetDto object
 * todo : all PlotFetchers can be refactored to extend this abstract class
 */
public abstract class AbstractMetricPlotFetcher extends PlotsDbMetricDataFetcher {

    @Override
    protected Set<Pair<MetricNameDto, List<PlotDatasetDto>>> fetchData(List<MetricNameDto> metricNames) {

        if (metricNames.isEmpty()) {
            return Collections.emptySet();
        }

        Collection<MetricRawData> allRawData = getAllRawData(metricNames);

        if (allRawData.isEmpty()) {
            log.warn("No plot data found for metrics : {}", metricNames);
            return Collections.emptySet();
        }

        Map<Long, Multimap<String, MetricRawData>> taskIdMetricIdRawMap = createMappedPlotDatasets(metricNames);

        for (MetricRawData rawData : allRawData) {
            String metricId = rawData.getMetricId();
            Long taskId = rawData.getWorkloadTaskDataId();
            taskIdMetricIdRawMap.get(taskId).put(metricId, rawData);
        }

        Multimap<MetricNameDto, PlotDatasetDto> metricNamePlotMap = ArrayListMultimap.create();
        for (MetricNameDto metricName : metricNames) {
            for (Long taskId : metricName.getTaskIds()) {
                Collection<MetricRawData> rawDatas;

                Multimap<String, MetricRawData> multimap = taskIdMetricIdRawMap.get(taskId);
                if (multimap == null) {
                    // we did not find metric for given task Id
                    // it could happen if we got 2 sessions with different sets of custom metrics
                    continue;
                }
                rawDatas = multimap.get(metricName.getMetricName());
                if (rawDatas == null || rawDatas.isEmpty()) {
                    // no data was saved for given taskId and metric Id
                    continue;
                }

                metricNamePlotMap.put(metricName, assemble(metricName, rawDatas));
            }
        }

        Set<Pair<MetricNameDto, List<PlotDatasetDto>>> resultSet = new HashSet<Pair<MetricNameDto, List<PlotDatasetDto>>>(metricNames.size());

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

    protected PlotDatasetDto assemble(MetricNameDto metricNameDto, Collection<MetricRawData> rawDatas) {
        List<PointDto> points = new ArrayList<PointDto>(rawDatas.size());
        String sessionId = null;

        for (MetricRawData metricRawData : rawDatas){
            if (sessionId == null) sessionId = metricRawData.getSessionId();
            points.add(new PointDto(metricRawData.getTime() / 1000D, metricRawData.getValue()));
        }

        return new PlotDatasetDto(
                points,
                legendProvider.generatePlotLegend(
                        sessionId,
                        metricNameDto.getMetricDisplayName(),
                        true),
                ColorCodeGenerator.getHexColorCode());
    }


    /**
     * method that retrieves all plotdata for given metricNames as MetricRawData
     * @param metricNames list of MetricNameDto objects for which plots should be retrieved
     * @return Collection of MetricRawData objects that represents one raw in database (one point on plot)
     */
    protected abstract Collection<MetricRawData> getAllRawData(List<MetricNameDto> metricNames);


    /**
     * Map Collection of MetricNameDto first of all by TaskDataDto id, after all by metric name (metric id)
     * @param metricNameDtos collection of MetricNameDto to be mapped
     * @return Map <TaskDataDto.id, Multimap<metricName, MetricRawData>>
     */
    private Map<Long, Multimap<String, MetricRawData>> createMappedPlotDatasets(Collection<MetricNameDto> metricNameDtos) {
        Map<Long, Multimap<String, MetricRawData>> taskIdMap = new HashMap<Long, Multimap<String, MetricRawData>>();
        for (MetricNameDto metricName : metricNameDtos) {
            Set<Long> ids = metricName.getTaskIds();
            for (Long id : ids) {
                if (!taskIdMap.containsKey(id)) {
                    taskIdMap.put(id, ArrayListMultimap.<String, MetricRawData>create());
                }
            }
        }
        return taskIdMap;
    }


    protected static class MetricRawData {

        String metricId;
        Long workloadTaskDataId;
        String sessionId;
        Long time;
        Double value;

        public void setMetricId(String metricId) {
            this.metricId = metricId;
        }

        public void setWorkloadTaskDataId(Long workloadTaskDataId) {
            this.workloadTaskDataId = workloadTaskDataId;
        }

        public String getMetricId() {
            return metricId;
        }

        public Long getWorkloadTaskDataId() {
            return workloadTaskDataId;
        }

        public String getSessionId() {
            return sessionId;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }
}
