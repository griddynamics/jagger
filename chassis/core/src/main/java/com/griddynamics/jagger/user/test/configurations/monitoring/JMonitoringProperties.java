package com.griddynamics.jagger.user.test.configurations.monitoring;

/**
 * @author asokol
 *         created 11/21/16
 */
public class JMonitoringProperties {

    private int minAgentsNumber;
    private int minKernelsNumber;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(int minAgentsNumber) {
        return new Builder(minAgentsNumber);
    }

    private JMonitoringProperties(Builder builder) {
        this.minAgentsNumber = builder.minAgentsNumber;
        this.minKernelsNumber = builder.minKernelsNumber;
    }

    public static class Builder {
        private int minAgentsNumber = 1;
        private int minKernelsNumber = 1;

        public Builder() {
        }

        public Builder(int minAgentsNumber) {
            this.minAgentsNumber = minAgentsNumber;
        }

        public Builder withminAgentsNumber(int minAgentsNumber) {
            this.minAgentsNumber = minAgentsNumber;
            return this;
        }

        public Builder withminKernelsNumber(int minKernelsNumber) {
            this.minKernelsNumber = minKernelsNumber;
            return this;
        }

        /**
         * Creates the object of {@link JMonitoringProperties} type with custom parameters.
         *
         * @return {@link JMonitoringProperties} object.
         */
        public JMonitoringProperties build() {
            return new JMonitoringProperties(this);
        }

    }

    public int getMinAgentsNumber() {
        return minAgentsNumber;
    }

    public int getMinKernelsNumber() {
        return minKernelsNumber;
    }
}
