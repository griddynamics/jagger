package com.griddynamics.jagger.user.test.configurations;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.QueryPoolLoadBalancer;
import com.griddynamics.jagger.invoker.RandomLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinLoadBalancer;
import com.griddynamics.jagger.invoker.RoundRobinPairSupplierFactory;
import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.invoker.v2.DefaultInvokerProvider;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;

import java.util.List;

/**
 * @brief Definition of the load test - describes test data sources and the protocol, used during load test
 * @n
 * @par Details:
 * @details Test definition is the base component of the @ref section_writing_test_load_scenario "load test description". With the help of the internal Builder class it allows to setup: @n
 * @li source of the endpointsProvider (where to apply load)
 * @li source of queries (what parameters of the load to set)
 * @li what protocol to use for the communication with the system under test (SUT)
 * @li how to validate SUT responses
 * @li what additional user defined actions to execute during communication with SUT
 *
 * More information on the parameter of the test definition, you can find in the Builder documentation @n
 * @n Code example:
 * @dontinclude ExampleSimpleJLoadScenarioProvider.java
 * @skip begin: following section is used for docu generation - Load test scenario configuration
 * @until end: following section is used for docu generation - Load test scenario configuration
 */
public class JTestDefinition {

    private final String id;
    private final Iterable endpoints;

    private final String comment;
    private final Iterable queries;
    private final Provider<Invoker> invoker;
    private final List<ResponseValidatorProvider> validators;
    private final List<Provider<InvocationListener>> listeners;
    private final QueryPoolLoadBalancer loadBalancer;

    private JTestDefinition(Builder builder) {
        this.id = builder.id.value();
        this.endpoints = builder.endpointsProvider;

        this.comment = (builder.comment == null) ? "" : builder.comment;
        this.queries = builder.queries;
        this.invoker = builder.invoker;
        this.validators = builder.validators;
        this.listeners = builder.listeners;
        this.loadBalancer = builder.loadBalancer;
    }

    /**
     * Builder of the JTestDefinition
     * @n
     * @param id                - Unique id of the test definition
     * @param endpointsProvider - Source of the test data: endpoint - where load will be applied
     * @n
     * @details Constructor parameters are mandatory for the JTestDefinition. All parameters, set by setters are optional
     * @n
     */
    public static Builder builder(Id id, Iterable endpointsProvider) {
        return new Builder(id, endpointsProvider);
    }

    public static class Builder {
        private final Id id;
        private final Iterable endpointsProvider;

        private String comment = "";
        private Iterable queries;
        private Provider<Invoker> invoker = DefaultInvokerProvider.of(DefaultHttpInvoker.class);
        private List<ResponseValidatorProvider> validators = Lists.newArrayList();
        private List<Provider<InvocationListener>> listeners = Lists.newArrayList();
        private QueryPoolLoadBalancer loadBalancer;

        private Builder(Id id, Iterable endpointsProvider) {
            this.id = id;
            this.endpointsProvider = endpointsProvider;
            this.loadBalancer = new RandomLoadBalancer() {{
                setPairSupplierFactory(new RoundRobinPairSupplierFactory());
                setRandomSeed(31);
            }};
        }

        /**
         * Optional: Sets human readable comment for the test definition
         *
         * @param comment the comment of the test definition
         */
        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Optional: Sets queries (what load will be applied during performance test) for the tests using this test prototype
         *
         * @param queryProvider iterable queries.
         * @see com.griddynamics.jagger.invoker.v2.JHttpQuery for example.
         */
        public Builder withQueryProvider(Iterable queryProvider) {
            this.queries = queryProvider;
            return this;
        }

        /**
         * Optional: Sets load balancer (subtypes of {@link QueryPoolLoadBalancer}).
         * Default is {@link RoundRobinLoadBalancer}
         *
         * @param loadBalancer load balancer.
         */
        public Builder withLoadBalancer(QueryPoolLoadBalancer loadBalancer) {
            this.loadBalancer = loadBalancer;
            return this;
        }

        /**
         * Optional: Sets subtypes of {@link com.griddynamics.jagger.invoker.Invoker}
         *
         * Instances of this invoker will be used during Jagger test execution to send requests to the System under test. @n
         * Example:
         * @code
         *      .withInvoker(DefaultInvokerProvider.of(DefaultHttpInvoker.class))
         * @endcode
         */
        public Builder withInvoker(Provider<Invoker> invoker) {
            this.invoker = invoker;
            return this;
        }

        /**
         * Optional: Adds a list of subtypes of {@link com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider}
         * Instances of those subtypes will be used to validate responses during Jagger test execution @n
         * Example:
         * @code
         *      addValidator(new ExampleResponseValidatorProvider("we are always good"))
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.ExampleResponseValidatorProvider for example
         */
        public Builder addValidators(List<ResponseValidatorProvider> validators) {
            this.validators.addAll(validators);
            return this;
        }
    
        /**
         * Optional: Adds a subtype of {@link com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider}
         * Instances of those subtypes will be used to validate responses during Jagger test execution
         * @n
         * Example:
         * @code
         *      addValidator(new ExampleResponseValidatorProvider("we are always good"))
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.ExampleResponseValidatorProvider for example
         */
        public Builder addValidator(ResponseValidatorProvider validator) {
            this.validators.add(validator);
            return this;
        }
    
        /**
         * Optional: Adds instances of subtypes of {@link com.griddynamics.jagger.engine.e1.Provider<InvocationListener>}
         * @b IMPORTANT: listener code will be executed during every invocation = every request to SUT
         * @n
         * Try to avoid slow operations in invocation listener code. They will slow down your workload
         * @n
         * Example:
         * @code
         *      addListeners(Arrays.asList(new NotNullInvocationListener()))
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener for example
         */
        public Builder addListeners(List<Provider<InvocationListener>> listeners) {
            this.listeners.addAll(listeners);
            return this;
        }
    
        /**
         * Optional: Adds a subtype instance of {@link com.griddynamics.jagger.engine.e1.Provider<InvocationListener>}
         * @b IMPORTANT: listener code will be executed during every invocation = every request to SUT
         * @n
         * Try to avoid slow operations in invocation listener code. They will slow down your workload
         * @n
         * Example:
         * @code
         *      addListener(new NotNullInvocationListener())
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.invocation.NotNullInvocationListener for example
         */
        public Builder addListener(Provider<InvocationListener> listener) {
            this.listeners.add(listener);
            return this;
        }
    
        /**
         * Creates the object of JTestDefinition type with custom parameters.
         *
         * @return JTestDefinition object.
         */
        public JTestDefinition build() {
            return new JTestDefinition(this);
        }
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return comment;
    }

    public Iterable getEndpoints() {
        return endpoints;
    }

    public Iterable getQueries() {
        return queries;
    }

    public Provider<Invoker> getInvoker() {
        return invoker;
    }

    public String getComment() {
        return comment;
    }

    public List<ResponseValidatorProvider> getValidators() {
        return validators;
    }
    
    public List<Provider<InvocationListener>> getListeners() {
        return listeners;
    }

    public QueryPoolLoadBalancer getLoadBalancer() {
        return loadBalancer;
    }
}
