package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.PlotNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class PlotNodeHandler extends TreeAwareHandler<PlotNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<PlotNode> event) {

        Set<MetricNode> metricNodeSet = new HashSet<MetricNode>();
        metricNodeSet.add(event.getItem());

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            testPlotFetcher.fetchPlots(metricNodeSet);
        } else {
            testPlotFetcher.removePlots(metricNodeSet);
        }
    }
}
