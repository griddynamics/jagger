package com.griddynamics.jagger.dbapi.dto;

import com.griddynamics.jagger.dbapi.model.MetricGroupNode;
import com.griddynamics.jagger.dbapi.model.LegendNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents plot with multiple lines
 * Refers to MetricNode
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class PlotIntegratedDto implements Serializable {
    private Collection<MarkingDto> markingSeries = Collections.emptyList();
    private String xAxisLabel;
    private String yAxisLabel;
    private String plotHeader;

    private MetricGroupNode<LegendNode> legendTree;

    public MetricGroupNode<LegendNode> getLegendTree() {
        return legendTree;
    }

    private PlotIntegratedDto() {
    }

    public PlotIntegratedDto(MetricGroupNode<LegendNode> legendTree, String xAxisLabel, String yAxisLabel, String plotHeader) {
        this.legendTree = legendTree;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.plotHeader = plotHeader;
    }

    public PlotIntegratedDto(MetricGroupNode<LegendNode> legendTree, String xAxisLabel, String yAxisLabel, String plotHeader, Collection<MarkingDto> markingSeries) {
        this.legendTree = legendTree;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.plotHeader = plotHeader;
        this.markingSeries = markingSeries;
    }

    /**
     * get list of lines */
    public Collection<PlotSingleDto> getPlotSeries() {
        if (legendTree == null) {
            return Collections.emptyList();
        }

        List<PlotSingleDto> plotSeries = new ArrayList<PlotSingleDto>();
        readLinesFromLegendTree(legendTree, plotSeries);
        return plotSeries;
    }

    /**
     * populate list with lines
     *
     * @param legendTree tree to populate list
     * @param plotSeries list that would be populated
     */
    private void readLinesFromLegendTree(MetricGroupNode<LegendNode> legendTree, List<PlotSingleDto> plotSeries) {

        if (legendTree.getMetricGroupNodeList() != null) {
            for (MetricGroupNode<LegendNode> group : legendTree.getMetricGroupNodeList()) {
                readLinesFromLegendTree(group, plotSeries);
            }
        }

        if (legendTree.getMetricsWithoutChildren() != null) {
            for (LegendNode node : legendTree.getMetricsWithoutChildren()) {
                plotSeries.add(node.getLine());
            }
        }
    }


    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public String getPlotHeader() {
        return plotHeader;
    }

    public Collection<MarkingDto> getMarkingSeries() {
        return markingSeries;
    }

    @Override
    public String toString() {
        return "PlotIntegratedDto{" +
                "legendTree=" + legendTree +
                ", markingSeries=" + markingSeries +
                ", xAxisLabel='" + xAxisLabel + '\'' +
                ", yAxisLabel='" + yAxisLabel + '\'' +
                ", plotHeader='" + plotHeader + '\'' +
                '}';
    }
}
