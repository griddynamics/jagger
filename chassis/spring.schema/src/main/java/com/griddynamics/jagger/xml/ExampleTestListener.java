package com.griddynamics.jagger.xml;

import com.google.common.collect.Multimap;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeType;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.SessionExecutionListener;
import com.griddynamics.jagger.master.configuration.Task;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/17/12
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */

//Test class
//for configuration

public class ExampleTestListener implements DistributionListener, SessionExecutionListener {


    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSessionStarted(String sessionId, Multimap<NodeType, NodeId> nodes) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSessionExecuted(String sessionId, String sessionComment) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
