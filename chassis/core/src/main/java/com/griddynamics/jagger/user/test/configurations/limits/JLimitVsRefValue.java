package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;

import java.util.Objects;

/**
 * Allow to compare a performance test with some reference value.
 */
public class JLimitVsRefValue extends JLimit {

    private Double refValue;


    private JLimitVsRefValue(Builder builder) {
        super(builder);
        this.refValue = builder.refValue.value();
    }

    /**
     * Builder for {@link JLimit} to compare with current value.
     *
     * @param metricName name of metric.
     * @param refValue   the for comparison.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(String metricName, RefValue refValue) {
        return new Builder(metricName, refValue);
    }

    /**
     * Builder for {@link JLimit} to compare with current value.
     *
     * @param metricName standard metric name.
     * @param refValue   the for comparison.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(JMetricName metricName, RefValue refValue) {
        return new Builder(metricName.transformToString(), refValue);
    }


    public static class Builder extends JLimit.Builder {
        private RefValue refValue;

        private Builder(String metricName, RefValue refValue) {
            Objects.requireNonNull(metricName);
            Objects.requireNonNull(refValue);

            this.metricName = metricName;
            this.refValue = refValue;
        }

        @Override
        public JLimit build() {
            return new JLimitVsRefValue(this);
        }

    }


    public Double getRefValue() {
        return refValue;
    }
}
