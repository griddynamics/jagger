package com.griddynamics.jagger.xml.stubs.xml;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.master.DistributionListener;
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

public class ExampleTestListener implements DistributionListener {

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {

    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {

    }
}
