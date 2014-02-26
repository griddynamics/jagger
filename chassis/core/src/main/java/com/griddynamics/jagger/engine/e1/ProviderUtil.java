package com.griddynamics.jagger.engine.e1;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.services.ServicesInitializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 12/12/13
 * Time: 2:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProviderUtil {
    public static <T>List provideElementsAndInjectContext(List<Provider<T>> providers, String sessionId, String taskId, NodeContext context, JaggerPlace environment){
        return provideAndInject(providers, sessionId, taskId, context, environment);
    }

    public static <T>List provideElements(List<Provider<T>> provider){
        return provideAndInject(provider, null, null, null, null);
    }

    public static void injectContext(Object provider, String sessionId, String taskId, NodeContext context, JaggerPlace environment){
        if (provider instanceof ServicesInitializable){
            ServicesInitializable nodeSideInitializable = (ServicesInitializable)provider;
            nodeSideInitializable.initServices(sessionId, taskId, context, environment);
        }
    }

    private static <T>List provideAndInject(List<Provider<T>> providers, String sessionId, String taskId, NodeContext context, JaggerPlace environment){
        List<T> result = new ArrayList<T>(providers.size());

        for (Provider<T> provider : providers){
            if (context !=null){
                injectContext(provider, sessionId, taskId, context, environment);
            }
            result.add(provider.provide());
        }

        return result;
    }
}
