package com.griddynamics.jagger.invoker.scenario;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.invoker.Invoker;

/**
 * Provides {@link JHttpUserScenarioInvoker}.
 */
public class JHttpUserScenarioInvokerProvider implements Provider<Invoker>  {

    @Override
    public Invoker provide() {
        return new JHttpUserScenarioInvoker();
    }
}