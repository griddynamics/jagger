package com.griddynamics.jagger.user.test.configurations;

/**
 * The class contains description of a test.
 * Here a user can set end points and queries for a test.
 */
public class JTestDescription {

    private String name;
    private String version;
    private String description;
    private Iterable endpoints;
    private Iterable queries;


    private JTestDescription(Builder builder) {
        this.name = builder.name;
        this.version = builder.version;
        this.description = builder.description;
        this.endpoints = builder.endpoints;
        this.queries = builder.queries;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private String name;
        private String version;
        private String description;
        private Iterable endpoints;
        private Iterable queries;

        private Builder() {

        }

        /**
         * Set name for a test.
         * Some thinks that name for a test and its id is absolutely the same things.
         *
         * @param name the name of a test.
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Set the version of a test.
         * At the moment of creating this documentation the author had no idea what for this parameter may be set.
         *
         * @param version the version of a test.
         */
        public Builder withVersion(String version) {
            this.version = version;
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


    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
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
