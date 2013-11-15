package com.griddynamics.jagger.util;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.NodeSideInitializable;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 11/6/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Injector {
    public static void injectNodeContext(Object object, String sessionId, String taskId, NodeContext context){
        if (object instanceof NodeSideInitializable){
            NodeSideInitializable nodeSideInitializable = (NodeSideInitializable)object;
            nodeSideInitializable.init(sessionId, taskId, context);
        }
    }
}
