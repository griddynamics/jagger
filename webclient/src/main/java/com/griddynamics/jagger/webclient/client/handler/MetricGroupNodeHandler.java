package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.MetricGroupNode;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.PlotNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.LinkedHashSet;
import java.util.Set;


public class MetricGroupNodeHandler extends TreeAwareHandler<MetricGroupNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<MetricGroupNode> event) {

        int size = event.getItem().getMetrics().size();

        if (size > 0) {

            // todo
            // Same not nice approach as in CheckHandlerMap
            // Point to change in future
            // MetricGroupNode should contain appropriate fetcher and just call it here

            if (event.getItem().getMetrics().get(0).getClass() == MetricNode.class) {
                metricFetcher.fetchMetrics(tree.getCheckedMetrics(), true);
            }

            if (event.getItem().getMetrics().get(0).getClass() == PlotNode.class) {
                MetricGroupNode<PlotNode> testNode = event.getItem();

                Set<MetricNode> nodes = new LinkedHashSet<MetricNode>();
                nodes.addAll(testNode.getMetrics());

                if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
                    testPlotFetcher.fetchPlots(nodes);
                } else {
                    testPlotFetcher.removePlots(nodes);
                }
            }

        }
    }
}
