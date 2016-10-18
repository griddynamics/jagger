package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.engine.e1.scenario.Calibrator;
import com.griddynamics.jagger.engine.e1.scenario.OneNodeCalibrator;
import com.griddynamics.jagger.invoker.QueryPoolScenarioFactory;
import com.griddynamics.jagger.invoker.ScenarioFactory;

/**
 * @author asokol
 *         created 10/18/16
 */
public class JTestDescription {

    private String name;
    private String version;
    private String description;

    private ScenarioFactory<?, ?, ?> scenarioFactory;

    private JTestDescription(Builder builder) {
        this.name = builder.name;
        this.version = builder.version;
        this.description = builder.description;
        this.scenarioFactory = builder.scenarioFactory;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private String name;
        private String version;
        private String description;
        private ScenarioFactory<?, ?, ?> scenarioFactory;

        private Builder() {
            scenarioFactory = new QueryPoolScenarioFactory<>();
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

        public Builder withEnpointsProvider(Iterable endpointsProvider) {
            ((QueryPoolScenarioFactory) scenarioFactory).setEndpointProvider(endpointsProvider);
            return this;
        }

        public Builder withQueryProvider(Iterable queryProvider) {
            ((QueryPoolScenarioFactory) scenarioFactory).setQueryProvider(queryProvider);
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

    public ScenarioFactory<?, ?, ?> getScenarioFactory() {
        return scenarioFactory;
    }
}
