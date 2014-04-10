package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.MonitoringSessionScopePlotNode;
import com.griddynamics.jagger.dbapi.model.SessionPlotNode;
import com.griddynamics.jagger.dbapi.dto.SessionPlotNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User: amikryukov
 * Date: 12/17/13
 */
public class MonitoringSessionScopePlotNodeHandler extends TreeAwareHandler<MonitoringSessionScopePlotNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<MonitoringSessionScopePlotNode> event) {

        Set<SessionPlotNameDto> dtos = new LinkedHashSet<SessionPlotNameDto>();

        for (SessionPlotNode plot: event.getItem().getPlots()) {
            dtos.add(plot.getPlotNameDto());
        }

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionScopePlotFetcher.fetchPlots(dtos, true);
        } else {
            sessionScopePlotFetcher.removePlots(dtos);
        }
    }
}