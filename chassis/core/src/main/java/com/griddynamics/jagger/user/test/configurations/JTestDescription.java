package com.griddynamics.jagger.user.test.configurations;

/**
 * Describes {@link JTest} prototype.
 */
public class JTestDescription {

    private String id;
    private String comment;
    private Iterable endpoints;
    private Iterable queries;

    private JTestDescription(Builder builder) {
        this.id = builder.id;
        this.comment = builder.comment;
        this.endpoints = builder.endpoints;
        this.queries = builder.queries;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String comment;
        private Iterable endpoints;
        private Iterable queries;

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
         * @param endpointsProvider iterable end points. See JHttpEndpoint for example.
         */
        public Builder withEndpointsProvider(Iterable endpointsProvider) {
            this.endpoints = endpointsProvider;
            return this;
        }


        /**
         * Sets queries (what load will be applied during performance test) for the tests using this test prototype.
         *
         * @param queryProvider iterable queries. See JHttpQuery for example.
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
        return comment;
    }

    public Iterable getEndpoints() {
        return endpoints;
    }

    public Iterable getQueries() {
        return queries;
    }
}
