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
     * @param metricId metric name.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(String metricId) {
        return new Builder(metricId);
    }

    /**
     * Builder for {@link JLimit} to compare with baseline.
     *
     * @param metricId name of standard metric.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(JMetricName metricId) {
        return new Builder(metricId.transformToString());
    }

    public static class Builder extends JLimit.Builder {

        private Builder(String metricId) {
            Objects.requireNonNull(metricId);
            this.metricId = metricId;
        }

        @Override
        public JLimit build() {
            return new JLimitVsBaseline(this);
        }
    }
}
