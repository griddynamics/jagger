package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.regexp.shared.RegExp;
import com.griddynamics.jagger.webclient.client.components.control.model.AbstractIdentifyNode;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;

import java.util.Arrays;
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
public class MetricRankingProvider {
    private static List<RegExp> patterns = Arrays.asList(
            RegExp.compile("^Iterations"),
            RegExp.compile("^Duration"),
            RegExp.compile("^Throughput"),
            RegExp.compile("^Success rate"),
            RegExp.compile("^Latency"),
            RegExp.compile("^Time Latency Percentile")
    );

    protected static int compare(String o1, String o2){
        Comparable o1Rank = getRank(o1);
        Comparable o2Rank = getRank(o2);
        if (o1Rank.compareTo(0)==0 && o2Rank.compareTo(0)==0){
            // display names, not matched to pattern above
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1,o2);
            return (res != 0) ? res : o1.compareTo(o2);
        }
        if (o1Rank.compareTo(o2Rank)==0){
            return o1.compareTo(o2);
        }
        return o1Rank.compareTo(o2Rank);
    }

    protected static Comparable getRank(String o){
        int rank = 0;
        for (RegExp pattern : patterns){
            if (pattern.test(o)){
                return new Integer(-patterns.size()+rank);
            }
            rank++;
        }
        return 0;
    }

    public static void sortPlotNodes(List<? extends AbstractIdentifyNode> list){
        Collections.sort(list, new Comparator<AbstractIdentifyNode>() {
            @Override
            public int compare(AbstractIdentifyNode o, AbstractIdentifyNode o2) {
                return MetricRankingProvider.compare(o.getDisplayName(), o2.getDisplayName());
            }
        });
    }

    public static void sortMetrics(List<MetricDto> list){
        Collections.sort(list, new Comparator<MetricDto>() {
            @Override
            public int compare(MetricDto metricDto, MetricDto metricDto2) {
                return MetricRankingProvider.compare(metricDto.getMetricName().getMetricDisplayName(), metricDto2.getMetricName().getMetricDisplayName());
            }
        });
    }
}
