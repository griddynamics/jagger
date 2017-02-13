package com.griddynamics.jagger.invoker.scenario.http;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.scenario.CustomScenarioInvoker;
import com.griddynamics.jagger.invoker.scenario.InvocationResult;
import com.griddynamics.jagger.invoker.scenario.InvocationScenario;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;

import static java.lang.String.format;

public class DefaultHttpCustomScenarioInvoker implements CustomScenarioInvoker<SpringBasedHttpClient> {

    private final SpringBasedHttpClient executor;

    public DefaultHttpCustomScenarioInvoker() {
        this.executor = new SpringBasedHttpClient();
    }

    public DefaultHttpCustomScenarioInvoker(SpringBasedHttpClient executor) {
        this.executor = executor;
    }

    /**
     * @param nothing            this parameter comes from {@link Invoker#invoke(Object, Object)}, but is not used in {@link CustomScenarioInvoker}
     * @param invocationScenario Scenario to execute as one invocation
     * @return result of execution of invocation scenario
     * @throws InvocationException
     */
    @Override
    public InvocationResult invoke(Void nothing, InvocationScenario<SpringBasedHttpClient> invocationScenario) throws InvocationException {
        Preconditions.checkNotNull(invocationScenario, "invocationScenario is null!");
        invocationScenario.setExecutor(executor);
        try {
            return invocationScenario.execute();
        } catch (Exception e) {
            throw new InvocationException(format("Exception occurred during execution of invocationScenario %s.", invocationScenario), e);
        }
    }
}
