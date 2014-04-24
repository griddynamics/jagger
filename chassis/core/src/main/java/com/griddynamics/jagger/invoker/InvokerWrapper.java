package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.ProviderUtil;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.services.ServicesInitializable;

/**
 * Created by kgribov on 2/19/14.
 */
// need to use old configuration(invoker object per invoker class) and new
public class InvokerWrapper<Q, R, E> implements ServicesInitializable{
    private Class<Invoker<Q, R, E>> invokerClazz;
    private Provider<Invoker<Q, R, E>> invokerProvider;

    public Invoker<Q, R, E> getInvoker(NodeContext context){
        return initInvoker(context);
    }

    private Invoker<Q, R, E> initInvoker(NodeContext context){
        if (invokerClazz != null){
            Invoker<Q, R, E> invoker = context.getService(invokerClazz);
            if(invoker == null) {
                throw new IllegalArgumentException("Service for class + '" + invokerClazz.getCanonicalName()
                        + "' not found!");
            }
            return context.getService(invokerClazz);
        }else{
            return invokerProvider.provide();
        }
    }

    public void setInvokerClazz(Class<Invoker<Q, R, E>> invokerClazz) {
        this.invokerClazz = invokerClazz;
    }

    public void setInvokerProvider(Provider<Invoker<Q, R, E>> invokerProvider) {
        this.invokerProvider = invokerProvider;
    }

    @Override
    public void initServices(String sessionId, String taskId, NodeContext context, JaggerPlace environment) {
        ProviderUtil.injectContext(invokerProvider, sessionId, taskId, context, environment);
    }
}
