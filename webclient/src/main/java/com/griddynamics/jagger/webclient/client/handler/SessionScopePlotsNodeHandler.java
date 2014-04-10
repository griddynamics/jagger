package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.*;
import com.griddynamics.jagger.dbapi.dto.SessionPlotNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class SessionScopePlotsNodeHandler extends TreeAwareHandler<SessionScopePlotsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<SessionScopePlotsNode> event) {
        Set<SessionPlotNameDto> dtos = new HashSet<SessionPlotNameDto >();
        for (MonitoringSessionScopePlotNode monitoringNode: event.getItem().getPlots()) {
            for (SessionPlotNode plotNode: monitoringNode.getPlots()) {
                dtos.add(plotNode.getPlotNameDto());
            }
        }

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionScopePlotFetcher.fetchPlots(dtos, true);
        } else {
            sessionScopePlotFetcher.removePlots(dtos);
        }
    }
}
