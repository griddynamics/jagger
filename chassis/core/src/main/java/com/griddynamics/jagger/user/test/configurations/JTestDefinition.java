package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;

import java.util.Collections;
import java.util.List;

/** @brief Definition of the load test - describes test data sources and the protocol, used during load test
 * @n
 * @par Details:
 * @details Test definition is the base component of the @ref section_writing_test_load_scenario "load test description". With the help of the internal Builder class it allows to setup: @n
 * @li source of the endpointsProvider (where to apply load)
 * @li source of queries (what parameters of the load to set)
 * @li what protocol to use for the communication with the system under test (SUT)
 *
 * More information on the parameter of the test definition, you can find in the Builder documentation @n
 * @n
 * Code example:
 * @dontinclude  ExampleSimpleJLoadScenarioProvider.java
 * @skip  begin: following section is used for docu generation - Load test scenario configuration
 * @until end: following section is used for docu generation - Load test scenario configuration
 */
public class JTestDefinition {

    private final String id;
    private final Iterable endpoints;
    
    private String comment;
    private Iterable queries;
    private Class<? extends Invoker> invoker;
    private List<Class<? extends ResponseValidator>> validators;

    private JTestDefinition(Builder builder) {
        this.id = builder.id.value();
        this.endpoints = builder.endpointsProvider;
        
        this.comment = builder.comment;
        if (this.comment == null) {
            this.comment = "";
        }
        this.queries = builder.queries;
        this.invoker = builder.invoker;
        this.validators = builder.validators;
    }

    /** Builder of the JTestDefinition
     * @n
     * @details Constructor parameters are mandatory for the JTestDefinition. All parameters, set by setters are optional
     * @n
     * @param id - Unique id of the test definition
     * @param endpointsProvider - Source of the test data: endpoint - where load will be applied
     */
    public static Builder builder(Id id, Iterable endpointsProvider) {
        return new Builder(id, endpointsProvider);
    }

    public static class Builder {
        private final Id id;
        private final Iterable endpointsProvider;
        
        private String comment = "";
        private Iterable queries;
        private Class<? extends Invoker> invoker = DefaultHttpInvoker.class;
        private List<Class<? extends ResponseValidator>> validators = Collections.emptyList();

        private Builder(Id id, Iterable endpointsProvider) {
            this.id = id;
            this.endpointsProvider = endpointsProvider;
        }

        /** Optional: Sets human readable comment for the test definition
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
         * Optional: Sets subtypes of {@link com.griddynamics.jagger.invoker.Invoker}
         *
         * Instances of this class will be used to during Jagger test execution to send requests to the System under test. @n
         * Example:
         * @code
         * withInvoker(com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker.class)
         * @endcode
         */
        public Builder withInvoker(Class<? extends Invoker> invoker) {
            this.invoker = invoker;
            return this;
        }
    
        /**
         * Optional: Sets a list of subtypes of {@link com.griddynamics.jagger.engine.e1.collector.ResponseValidator}
         * Instances of those subtypes will be used to validate responses during Jagger test execution @n
         * Example:
         * @code
         * withValidators(Arrays.asList(com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator.class))
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator for example
         */
        public Builder withValidators(List<Class<? extends ResponseValidator>> validators) {
            this.validators = validators;
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
    
    public Class<? extends Invoker> getInvoker() {
        return invoker;
    }
    
    public String getComment() {
        return comment;
    }
    
    public List<Class<? extends ResponseValidator>> getValidators() {
        return validators;
    }
}
