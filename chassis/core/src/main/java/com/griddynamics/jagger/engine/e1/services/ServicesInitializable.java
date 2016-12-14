package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/13
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ServicesInitializable {
    void initServices(String sessionId, String taskId, NodeContext context, JaggerPlace environment);
}
