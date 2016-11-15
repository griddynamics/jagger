package com.griddynamics.jagger.dbapi.model;

import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.util.MetricNamesRankingProvider;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 22.04.13
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public class MetricRankingProvider extends MetricNamesRankingProvider {

    public static void sortPlotNodes(List<? extends AbstractIdentifyNode> list) {
        Collections.sort(list, new Comparator<AbstractIdentifyNode>() {
            @Override
            public int compare(AbstractIdentifyNode o, AbstractIdentifyNode o2) {
                return MetricRankingProvider.compare(o.getDisplayName(), o2.getDisplayName());
            }
        });
    }

    public static void sortMetrics(List<SummarySingleDto> list) {
        Collections.sort(list, new Comparator<SummarySingleDto>() {
            @Override
            public int compare(SummarySingleDto metricDto, SummarySingleDto metricDto2) {
                String displayName1 = metricDto.getMetricName().getMetricDisplayName();
                String displayName2 = metricDto2.getMetricName().getMetricDisplayName();
                if (!displayName1.equals(displayName2)) {
                    return MetricRankingProvider.compare(metricDto.getMetricName().getMetricDisplayName(), metricDto2.getMetricName()
                            .getMetricDisplayName());
                } else {
                    return metricDto2.getMetricName().getOrigin().ordinal() - metricDto.getMetricName().getOrigin().ordinal();
                }
            }
        });
    }
}
