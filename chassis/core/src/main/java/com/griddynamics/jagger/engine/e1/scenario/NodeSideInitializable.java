package com.griddynamics.jagger.engine.e1.scenario;

import com.griddynamics.jagger.coordinator.NodeContext;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/10/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NodeSideInitializable {
    void init(String sessionId, String taskId, NodeContext nodeContext);
}
