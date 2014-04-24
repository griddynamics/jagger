package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.ProviderUtil;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.services.ServicesInitializable;
import com.griddynamics.jagger.util.JavaSystemClock;

import java.util.Collections;
import java.util.List;

/**
 * Created by kgribov on 2/18/14.
 */
public class InvokerStepProvider implements Provider<InvokerStep>, ServicesInitializable{
    private String name = "No name step";

    private InvokerWrapper invokerWrapper;
    private Provider<QueryProcessor> queryProcessorProvider = new DefaultQueryProcessorProvider();
    private List<Provider<InvocationListener<Object, Object, Object>>> listeners = Collections.EMPTY_LIST;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInvokerWrapper(InvokerWrapper invokerWrapper) {
        this.invokerWrapper = invokerWrapper;
    }

    public void setQueryProcessorProvider(Provider<QueryProcessor> queryProcessorProvider) {
        this.queryProcessorProvider = queryProcessorProvider;
    }
    public void setListeners(List<Provider<InvocationListener<Object, Object, Object>>> listeners) {
        this.listeners = listeners;
    }

    @Override
    public InvokerStep provide() {
        return new InvokerStep() {

            //provide elements for thread
            private Invoker invoker = createInvoker();
            private QueryProcessor queryProcessor = queryProcessorProvider.provide();

            @Override
            public StepResult execute(StepInfo stepInfo){

                StepInfo info = queryProcessor.preProcess(stepInfo);

                Object result = invoker.invoke(info.getQuery(), info.getEndpoint());

                StepInfo nextStepInfo = queryProcessor.postProcess(new StepResult(info, result));

                return new StepResult(nextStepInfo, result);
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    private NodeContext context;

    @Override
    public void initServices(String sessionId, String taskId, NodeContext context, JaggerPlace jaggerPlace) {
        this.context = context;
        for (Provider<InvocationListener<Object, Object, Object>> provider : listeners){
            ProviderUtil.injectContext(provider, sessionId, taskId, context, jaggerPlace);
        }
    }

    private Invoker createInvoker(){
        List<InvocationListener<Object, Object, Object>> invokerListeners = ProviderUtil.provideElements(listeners);
        InvocationListener composite = InvocationListener.Composer.compose(invokerListeners);
        return Invokers.listenableInvoker(invokerWrapper.getInvoker(context), composite, new JavaSystemClock());
    }
}
