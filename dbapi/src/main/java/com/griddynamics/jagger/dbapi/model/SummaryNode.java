package com.griddynamics.jagger.dbapi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Corresponds to "Summary" and "Trends" tabs in UI
 * User: amikryukov
 * Date: 11/26/13
 */
public class SummaryNode extends AbstractIdentifyNode {

    List<TestNode> tests;

    SessionInfoNode sessionInfo;

    public SummaryNode() {
    }

    public SummaryNode(String id, String displayName) {
        super(id, displayName);
    }

    public List<TestNode> getTests() {
        if (tests == null) {
            return Collections.EMPTY_LIST;
        }
        return tests;
    }

    public void setTests(List<TestNode> tests) {
        this.tests = tests;
    }

    public SessionInfoNode getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(SessionInfoNode sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        ArrayList<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        result.add(sessionInfo);
        if (tests != null && !tests.isEmpty())result.addAll(tests);
        return result;
    }
}
