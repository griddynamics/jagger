package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.NodeSideInitializable;
import com.griddynamics.jagger.engine.e1.Provider;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 11/7/13
 * Time: 8:56 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractServicesAwareProvider<T> implements NodeSideInitializable, Provider<T> {

    private ServicesContext services;

    protected ServicesContext getServices(){
        return services;
    }

    public final void init(String sessionId, String taskId, NodeContext context){
        services = new ServicesContext(sessionId, taskId, context);

        init();
    }

    protected abstract void init();
}
