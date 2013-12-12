package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.*;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class DetailsNodeHandler extends TreeAwareHandler<DetailsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<DetailsNode> event) {

        DetailsNode detailsNode = event.getItem();

        Set<PlotNameDto> testScopePlotNames = new HashSet<PlotNameDto>();
        for (TestDetailsNode test: detailsNode.getTests()) {
            for (PlotNode plotNode: test.getPlots()) {
                testScopePlotNames.add(plotNode.getPlotName());
            }
        }

        Set<PlotNameDto> sessionScopePlotNames = new HashSet<PlotNameDto>();
        if (detailsNode.getSessionScopePlotsNode() != null) {
            for (SessionPlotNode plotNode: detailsNode.getSessionScopePlotsNode().getPlots()) {
                sessionScopePlotNames.add(plotNode.getPlotNameDto());
            }
        }

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionScopePlotFetcher.fetchPlots(sessionScopePlotNames, false);
            testPlotFetcher.fetchPlots(testScopePlotNames, true);
        } else {
            sessionScopePlotFetcher.removePlots(sessionScopePlotNames);
            testPlotFetcher.removePlots(testScopePlotNames);
        }
    }
}
