package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;

import java.util.Objects;

/**
 * Allow to compare a performance test with some baseline (another performance test, which was saved in the database).
 */
public class JLimitVsBaseline extends JLimit {

    private JLimitVsBaseline(Builder builder) {
        super(builder);
    }

    /**
     * Builder for {@link JLimit} to compare with baseline.
     *
     * @param metricName metric name.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(String metricName) {
        return new Builder(metricName);
    }

    /**
     * Builder for {@link JLimit} to compare with baseline.
     *
     * @param metricName name of standard metric.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(JMetricName metricName) {
        return new Builder(metricName.transformToString());
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
