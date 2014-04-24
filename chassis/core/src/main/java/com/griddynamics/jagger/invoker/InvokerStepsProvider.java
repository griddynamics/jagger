package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.ProviderUtil;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.services.ServicesInitializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by kgribov on 2/18/14.
 */
public class InvokerStepsProvider<Q,R,E>  implements Provider<Invoker<Q,R,E>>, ServicesInitializable{

    private static final Logger log = LoggerFactory.getLogger(InvokerStepsProvider.class);


    private List<Provider<InvokerStep>> stepsProviders;

    public void setStepsProviders(List<Provider<InvokerStep>> stepsProviders) {
        this.stepsProviders = stepsProviders;
    }

    @Override
    public Invoker<Q, R, E> provide() {
        return new Invoker<Q, R, E>() {

            private List<InvokerStep> steps = ProviderUtil.provideElements(stepsProviders);

            @Override
            public R invoke(Q query, E endpoint) throws InvocationException {
                Object lastResult = null;
                StepInfo nextStepInfo = new StepInfo(query, endpoint);

                for (InvokerStep step : steps){
                    StepResult stepResult = step.execute(nextStepInfo);
                    if (stepResult.getStepInfo() == null){
                        throw new StepFailException(step.getName());
                    }

                    lastResult = stepResult.getResult();
                    nextStepInfo = stepResult.getStepInfo();

                }
                return (R)lastResult;
            }
        };
    }

    @Override
    public void initServices(String sessionId, String taskId, NodeContext context, JaggerPlace environment) {
        for (Provider<InvokerStep> provider : stepsProviders){
            ProviderUtil.injectContext(provider, sessionId, taskId, context, environment);
        }
    }
}