package com.griddynamics.jagger.user.test.configurations;

/**
 * @author asokol
 *         created 10/18/16
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

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withEndpointsProvider(Iterable endpointsProvider) {
            this.endpoints = endpointsProvider;
            return this;
        }

        public Builder withQueryProvider(Iterable queryProvider) {
            this.queries = queryProvider;
            return this;
        }

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
