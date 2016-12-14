package com.griddynamics.jagger.user.test.configurations.limits;

import java.util.Objects;

/**
 * Allow to compare a performance test with some baseline (another performance test, which was saved in the database).
 */
public class JLimitVsBaseline extends JLimit {

    private JLimitVsBaseline(Builder builder) {
        super(builder);
    }

    public static Builder builder(String metricName) {
        return new Builder(metricName);
    }

    public static class Builder extends JLimit.Builder {

        private Builder(String metricName) {
            Objects.requireNonNull(metricName);
            this.metricName = metricName;
        }

        @Override
        public JLimit build() {
            return new JLimitVsBaseline(this);
        }
    }
}
