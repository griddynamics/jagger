package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.util.ColorCodeGenerator;
import com.griddynamics.jagger.dbapi.dto.*;

import java.util.*;

public abstract class SummaryDbMetricDataFetcher extends DbMetricDataFetcher<MetricDto> {

    protected PlotSeriesDto generatePlotSeriesDto(MetricDto metricDto) {
        double yMinimum = Double.MAX_VALUE;

        //So plot draws as {(0, val0),(1, val1), (2, val2), ... (n, valn)}
        List<PointDto> list = new ArrayList<PointDto>();

        List<MetricValueDto> metricList = new ArrayList<MetricValueDto>();
        for(MetricValueDto value :metricDto.getValues()) {
            metricList.add(value);
        }

        Collections.sort(metricList, new Comparator<MetricValueDto>() {

            @Override
            public int compare(MetricValueDto o1, MetricValueDto o2) {
                return  o2.getSessionId() < o1.getSessionId() ? 1 : -1;
            }
        });

        for (MetricValueDto value: metricList) {
            double temp = Double.parseDouble(value.getValue());
            list.add(new PointDto(value.getSessionId(), temp));
            if (yMinimum == Double.MAX_VALUE || temp < yMinimum)
                yMinimum = temp;
        }

        String legend = metricDto.getMetricName().getMetricDisplayName();

        PlotDatasetDto pdd = new PlotDatasetDto(
                list,
                legend,
                ColorCodeGenerator.getHexColorCode()
        );

        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(metricDto.getMetricName().getTest().getTaskName()).
                append(", ").
                append(metricDto.getMetricName().getMetricName());

        PlotSeriesDto psd = new PlotSeriesDto(
                Arrays.asList(pdd),
                "Sessions" ,
                metricDto.getMetricName().getMetricName(),
                headerBuilder.toString()
        );

        psd.setYAxisMin(yMinimum);

        return psd;
    }
}
