package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.*;
import com.griddynamics.jagger.dbapi.dto.SessionPlotNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * User: amikryukov
 * Date: 11/27/13
 */
public class DetailsNodeHandler extends TreeAwareHandler<DetailsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<DetailsNode> event) {

        DetailsNode detailsNode = event.getItem();

        Set<MetricNode> testScopePlotNames = new HashSet<MetricNode>();
        for (TestDetailsNode test: detailsNode.getTests()) {
            testScopePlotNames.addAll(test.getMetrics());
        }

        Set<SessionPlotNameDto> sessionScopePlotNames = new HashSet<SessionPlotNameDto>();
        if (detailsNode.getSessionScopePlotsNode() != null) {
            for (MonitoringSessionScopePlotNode monitoringPlotNode: detailsNode.getSessionScopePlotsNode().getPlots()) {
                for (SessionPlotNode plotNode : monitoringPlotNode.getPlots()) {
                    sessionScopePlotNames.add(plotNode.getPlotNameDto());
                }
            }
        }

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionScopePlotFetcher.fetchPlots(sessionScopePlotNames, false);
            testPlotFetcher.fetchPlots(testScopePlotNames);
        } else {
            sessionScopePlotFetcher.removePlots(sessionScopePlotNames);
            testPlotFetcher.removePlots(testScopePlotNames);
        }
    }
}
