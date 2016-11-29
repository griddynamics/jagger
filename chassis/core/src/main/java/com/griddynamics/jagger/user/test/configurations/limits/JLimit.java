package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowerWarningThreshold;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpperWarningThreshold;

/**
 * @author asokol
 *         created 11/29/16
 */
public class JLimit {

    private final String metricName;
    private final String limitDescription;
    private final Double refValue;
    private final Double lowerWarningThreshold;
    private final Double upperWarningThreshold;
    private final Double lowerErrorThreshold;
    private final Double upperErrorThreshold;

    public static Builder builder() {
        return new Builder();
    }

    private JLimit(Builder builder) {
        this.metricName = builder.metricName;
        this.limitDescription = builder.limitDescription;
        this.refValue = builder.refValue.value();
        this.lowerWarningThreshold = builder.lowerWarningThreshold.value();
        this.upperWarningThreshold = builder.upperWarningThreshold.value();
        this.lowerErrorThreshold = builder.lowerErrorThreshold.value();
        this.upperErrorThreshold = builder.upperErrorThreshold.value();
    }

    public static class Builder {
        private String metricName;
        private String limitDescription;
        private RefValue refValue;
        private LowerWarningThreshold lowerWarningThreshold;
        private UpperWarningThreshold upperWarningThreshold;
        private LowerWarningThreshold lowerErrorThreshold;
        private UpperWarningThreshold upperErrorThreshold;

        public Builder() {
        }

        public Builder withMetricValue(String metricName) {
            this.metricName = metricName;
            return this;
        }

        public Builder withLimitDescription(String limitDescription) {
            this.limitDescription = limitDescription;
            return this;
        }

        public Builder withRefValue(Double refValue) {
            this.refValue = RefValue.of(refValue);
            return this;
        }

        public Builder withLowerWarningThreshold(Double lowerWarningThreshold) {
            this.lowerWarningThreshold = LowerWarningThreshold.of(lowerWarningThreshold);
            return this;
        }

        public Builder withLowerErrorThreshold(Double lowerErrorThreshold) {
            this.lowerErrorThreshold = LowerWarningThreshold.of(lowerErrorThreshold);
            return this;
        }

        public Builder withUpperErrorThreshold(Double upperErrorThreshold) {
            this.upperErrorThreshold = UpperWarningThreshold.of(upperErrorThreshold);
            return this;
        }


        public Builder withUpperWarningThreshold(Double upperWarningThreshold) {
            this.upperWarningThreshold = UpperWarningThreshold.of(upperWarningThreshold);
            return this;
        }


        public JLimit build() {
            return new JLimit(this);
        }

    }


    public String getMetricName() {
        return metricName;
    }

    public String getLimitDescription() {
        return limitDescription;
    }

    public Double getRefValue() {
        return refValue;
    }

    public Double getLowerWarningThreshold() {
        return lowerWarningThreshold;
    }

    public Double getUpperWarningThreshold() {
        return upperWarningThreshold;
    }

    public Double getLowerErrorThreshold() {
        return lowerErrorThreshold;
    }

    public Double getUpperErrorThreshold() {
        return upperErrorThreshold;
    }
}
