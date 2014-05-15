package com.griddynamics.jagger.dbapi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Corresponds to "Metric" tab in UI.
 * User: amikryukov
 * Date: 11/27/13
 */
public class DetailsNode extends AbstractIdentifyNode {

    SessionScopePlotsNode sessionScopePlotsNode;

    List<TestDetailsNode> tests;

    public DetailsNode() {}

    public DetailsNode(String id, String displayName) {
        super(id, displayName);
    }

    public SessionScopePlotsNode getSessionScopePlotsNode() {
        return sessionScopePlotsNode;
    }

    public void setSessionScopePlotsNode(SessionScopePlotsNode sessionScopePlotsNode) {
        this.sessionScopePlotsNode = sessionScopePlotsNode;
    }

    public List<TestDetailsNode> getTests() {
        if (tests == null) {
            return Collections.EMPTY_LIST;
        }
        return tests;
    }

    public void setTests(List<TestDetailsNode> tests) {
        this.tests = tests;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        List<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        if (sessionScopePlotsNode != null) result.add(sessionScopePlotsNode);
        if (tests != null && !tests.isEmpty()) result.addAll(tests);
        return result;
    }
}
