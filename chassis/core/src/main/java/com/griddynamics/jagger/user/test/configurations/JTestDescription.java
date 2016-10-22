package com.griddynamics.jagger.user.test.configurations;

/**
 * The class contains description of a test.
 * Here a user can set end points and queries for a test.
 */
public class JTestDescription {

    private String id;
    private String description;
    private Iterable endpoints;
    private Iterable queries;

    private JTestDescription(Builder builder) {
        this.id = builder.id;
        this.description = builder.description;
        this.endpoints = builder.endpoints;
        this.queries = builder.queries;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String description;
        private Iterable endpoints;
        private Iterable queries;

        private Builder() {

        }

        /**
         * Set id for a test.
         *
         * @param id the name of a test.
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }


        /**
         * Set description for a test.
         *
         * @param description the description.
         */
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets end points for a test.
         *
         * @param endpointsProvider iterable end points. {@link java.util.List}, for example.
         */
        public Builder withEndpointsProvider(Iterable endpointsProvider) {
            this.endpoints = endpointsProvider;
            return this;
        }

        /**
         * Sets queries for a test.
         *
         * @param queryProvider iterable queries. {@link java.util.List}, for example.
         * @return
         */
        public Builder withQueryProvider(Iterable queryProvider) {
            this.queries = queryProvider;
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
        return description;
    }

    public Iterable getEndpoints() {
        return endpoints;
    }

    public Iterable getQueries() {
        return queries;
    }
}
