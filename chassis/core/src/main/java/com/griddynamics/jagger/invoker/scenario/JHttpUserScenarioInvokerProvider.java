package com.griddynamics.jagger.invoker.scenario;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.invoker.Invoker;

/**
 * Provider for {@link JHttpUserScenarioInvoker}.
 *
 * @ingroup Main_Http_User_Scenario_group
 */
public class JHttpUserScenarioInvokerProvider implements Provider<Invoker>  {

    @Override
    public Invoker provide() {
        return new JHttpUserScenarioInvoker();
    }
}