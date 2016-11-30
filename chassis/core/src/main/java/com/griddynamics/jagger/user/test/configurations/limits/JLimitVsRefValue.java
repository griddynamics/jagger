package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;

import java.util.Objects;

/**
 * @author asokol
 *         created 11/30/16
 */
public class JLimitVsRefValue extends JLimit {

    private Double refValue;


    private JLimitVsRefValue(Builder builder) {
        super(builder);
        this.refValue = builder.refValue.value();
    }

    public static Builder builder(String metricName, RefValue refValue) {
        return new Builder(metricName, refValue);
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
