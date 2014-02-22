package com.griddynamics.jagger.xml.stubs.xml;

import com.google.common.collect.Multimap;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.master.configuration.SessionExecutionStatus;
import com.griddynamics.jagger.master.configuration.SessionListener;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 09.04.13
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public class ExampleSessionListener implements SessionListener {
    @Override
    public void onSessionExecuted(String sessionId, String sessionComment, SessionExecutionStatus errorStatus) {

    }

    @Override
    public void onSessionStarted(String sessionId, Multimap<NodeType, NodeId> nodes) {

    }

    @Override
    public void onSessionExecuted(String sessionId, String sessionComment) {

    }
}
