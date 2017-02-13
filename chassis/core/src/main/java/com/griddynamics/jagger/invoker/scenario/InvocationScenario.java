package com.griddynamics.jagger.invoker.scenario;

/** The <code>InvocationScenario</code> interface should be implemented by any
 * class whose instances are intended to be executed by {@link CustomScenarioInvoker} subtypes. The
 * class must override a method <code>execute</code> and put invocation scenario logic there.
 * Also, that class must declare a field for executor of type <code>E</code>, but no initialize it -
 * it must be injected in implementation of {@link CustomScenarioInvoker}.
 * This executor must be used in scenario for making all requests.
 *
 * @param <E> invocation scenario executor type
 */
public interface InvocationScenario<E> {
    InvocationResult execute();

    E getExecutor();

    void setExecutor(E executor);
}
