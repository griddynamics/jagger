package com.griddynamics.jagger.dbapi.model;

import java.util.Collections;
import java.util.List;

/**
 * User: amikryukov
 * Date: 12/17/13
 */
public class MonitoringSessionScopePlotNode extends AbstractIdentifyNode {

    private List<SessionPlotNode> plots;

    public List<SessionPlotNode> getPlots() {
        return plots;
    }

    public void setPlots(List<SessionPlotNode> plots) {
        this.plots = plots;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return plots == null ? Collections.EMPTY_LIST : plots;
    }
}
