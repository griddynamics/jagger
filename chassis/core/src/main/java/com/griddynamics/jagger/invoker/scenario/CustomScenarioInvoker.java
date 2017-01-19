package com.griddynamics.jagger.invoker.scenario;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;

/** Responsible for custom scenario invocation
 * @author Anton Antonenko
 * @n
 * @par Details:
 * @details Executes custom scenario in one invocation.
 * Code of scenario must be put into {@link InvocationScenario#execute()} method,
 * which must be called in {@link CustomScenarioInvoker#invoke(Void, InvocationScenario)}.
 * The result of invocation can be collected by metrics and validators.
 * Note that Invoker is used in multi thread environment, so realize thread-safe implementation @n
 * @n
 * To view all invokers implementations click here @ref Main_Invokers_group
 *
 * @param <E> - Executor type
 *
 * @ingroup Main_Invokers_Base_group */
public interface CustomScenarioInvoker<E> extends Invoker<Void, InvocationResult, InvocationScenario<E>> {
    /**
     * @param nothing            this parameter comes from {@link Invoker#invoke(Object, Object)}, but is not used in {@link CustomScenarioInvoker}
     * @param invocationScenario Scenario to execute as one invocation
     * @return result of execution of invocation scenario
     * @throws InvocationException
     */
    @Override
    InvocationResult invoke(Void nothing, InvocationScenario<E> invocationScenario) throws InvocationException;
}
