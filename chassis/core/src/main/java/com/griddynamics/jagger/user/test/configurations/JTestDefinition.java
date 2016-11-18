package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.user.test.configurations.aux.Id;

import java.util.Collections;
import java.util.List;

/**
 * Definition for {@link JLoadTest} instance,
 * i.e. what and how will be triggered during Jagger test execution
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
        this.endpoints = builder.endpoints;
        
        this.comment = builder.comment;
        this.queries = builder.queries;
        this.invoker = builder.invoker;
        this.validators = builder.validators;
    }

    public static Builder builder(Id id, Iterable endpoints) {
        return new Builder(id, endpoints);
    }

    public static class Builder {
        private final Id id;
        private final Iterable endpoints;
        
        private String comment;
        private Iterable queries;
        private Class<? extends Invoker> invoker = DefaultHttpInvoker.class;
        private List<Class<? extends ResponseValidator>> validators = Collections.emptyList();

        private Builder(Id id, Iterable endpoints) {
            this.id = id;
            this.endpoints = endpoints;
        }

        /**
         * Sets human readable comment for the test prototype.
         *
         * @param comment the comment of the test prototype.
         */
        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Sets queries (what load will be applied during performance test) for the tests using this test prototype.
         *
         * @param queryProvider iterable queries.
         * @see com.griddynamics.jagger.invoker.v2.JHttpQuery for example.
         */
        public Builder withQueryProvider(Iterable queryProvider) {
            this.queries = queryProvider;
            return this;
        }

        /**
         * Sets subtypes of {@link com.griddynamics.jagger.invoker.Invoker}.
         * Instances of this class will be used to during Jagger test execution.
         * <p/>
         * Example:
         *      <code>withInvoker(com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker.class)</code>
         */
        public Builder withInvoker(Class<? extends Invoker> invoker) {
            this.invoker = invoker;
            return this;
        }
    
        /**
         * Sets a list of subtypes of {@link ResponseValidator}
         * Instances of those subtypes will be used to validate responses during Jagger test execution.
         * <p/>
         * Example:
         *      <code>withValidators(Arrays.asList(com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator.class))</code>
         * <p/>
         * @see com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator for example
         */
        public Builder withValidators(List<Class<? extends ResponseValidator>> validators) {
            this.validators = validators;
            return this;
        }

        /**
         * As one may expect, creates the object of {@link JLoadTest} type with custom parameters.
         *
         * @return {@link JLoadTest} object.
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
