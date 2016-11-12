package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;

import java.util.Collections;
import java.util.List;

/**
 * Describes {@link JTest} prototype.
 */
public class JTestDescription {

    private String id;
    private String comment;
    private Iterable endpoints;
    private Iterable queries;
    private Class<? extends Invoker> invoker;
    private List<Class<? extends ResponseValidator>> validators;

    private JTestDescription(Builder builder) {
        this.id = builder.id;
        this.comment = builder.comment;
        this.endpoints = builder.endpoints;
        this.queries = builder.queries;
        this.invoker = builder.invoker;
        this.validators = builder.validators;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String comment;
        private Iterable endpoints;
        private Iterable queries;
        private Class<? extends Invoker> invoker = DefaultHttpInvoker.class;
        private List<Class<? extends ResponseValidator>> validators = Collections.emptyList();

        private Builder() {
        }

        /**
         * Sets id for the test prototype.
         *
         * @param id the id of the test prototype.
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
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
         * Sets end points (where load will be applied during performance test) for the tests using this test prototype.
         *
         * @param endpointsProvider iterable end points.
         * @see com.griddynamics.jagger.invoker.v2.JHttpEndpoint for example.
         */
        public Builder withEndpointsProvider(Iterable endpointsProvider) {
            this.endpoints = endpointsProvider;
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
         * As one may expect, creates the object of {@link JTest} type with custom parameters.
         *
         * @return {@link JTest} object.
         */
        public JTestDescription build() {
            return new JTestDescription(this);
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
