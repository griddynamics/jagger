package com.griddynamics.jagger.dbapi.model;

import java.util.Collections;
import java.util.List;

/**
 * User: amikryukov
 * Date: 11/27/13
 */
public class SessionScopePlotsNode extends AbstractIdentifyNode {

    List<MonitoringSessionScopePlotNode> plots;

    public SessionScopePlotsNode() {
    }

    public SessionScopePlotsNode(String id, String displayName) {
        super(id, displayName);
    }

    public List<MonitoringSessionScopePlotNode> getPlots() {
        return plots;
    }

    public void setPlots(List<MonitoringSessionScopePlotNode> plots) {
        this.plots = plots;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return plots == null ? Collections.EMPTY_LIST : plots;
    }
}
