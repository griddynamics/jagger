package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.MonitoringPlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.TestDetailsNode;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class TestDetailsNodeHandler extends TreeAwareHandler<TestDetailsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<TestDetailsNode> event) {

        TestDetailsNode testNode = event.getItem();
        Set<MetricNameDto> dtos = new LinkedHashSet<MetricNameDto>();
        for (PlotNode plotNode : testNode.getMetrics()) {
            dtos.add(plotNode.getMetricNameDto());
        }

        for (MonitoringPlotNode monitoringPlotNode : testNode.getMonitoringPlots()) {
            for (PlotNode plot: monitoringPlotNode.getPlots()) {
                dtos.add(plot.getMetricNameDto());
            }
        }

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            testPlotFetcher.fetchPlots(dtos, true);
        } else {
            testPlotFetcher.removePlots(dtos);
        }
    }
}

