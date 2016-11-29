package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowerErrorThreshold;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowerWarningThreshold;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpperErrorThreshold;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpperWarningThreshold;

/**
 * @author asokol
 *         created 11/29/16
 */
public class JLimit {

    private final String metricName;
    private String limitDescription;
    private Double refValue;
    private final Double lowerWarningThreshold;
    private final Double upperWarningThreshold;
    private final Double lowerErrorThreshold;
    private final Double upperErrorThreshold;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LowerWarningThreshold lowerWarningThreshold,
                                  UpperWarningThreshold upperWarningThreshold,
                                  LowerErrorThreshold lowerErrorThreshold,
                                  UpperErrorThreshold upperErrorThreshold) {
        return new Builder(lowerWarningThreshold, upperWarningThreshold, lowerErrorThreshold, upperErrorThreshold);

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
        private String limitDescription = "";
        private RefValue refValue = RefValue.of(null);
        private LowerWarningThreshold lowerWarningThreshold;
        private UpperWarningThreshold upperWarningThreshold;
        private LowerErrorThreshold lowerErrorThreshold;
        private UpperErrorThreshold upperErrorThreshold;

        public Builder() {
        }

        public Builder(LowerWarningThreshold lowerWarningThreshold,
                       UpperWarningThreshold upperWarningThreshold,
                       LowerErrorThreshold lowerErrorThreshold,
                       UpperErrorThreshold upperErrorThreshold) {
            this.lowerErrorThreshold = lowerErrorThreshold;
            this.upperErrorThreshold = upperErrorThreshold;
            this.lowerWarningThreshold = lowerWarningThreshold;
            this.upperWarningThreshold = upperWarningThreshold;
        }


        public Builder withMetricName(String metricName) {
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

        public Builder onlyWarnings(LowerWarningThreshold lowerWarningThreshold, UpperWarningThreshold upperWarningThreshold) {
            this.lowerErrorThreshold = LowerErrorThreshold.of(Double.NEGATIVE_INFINITY);
            this.upperErrorThreshold = UpperErrorThreshold.of(Double.POSITIVE_INFINITY);
            this.lowerWarningThreshold = lowerWarningThreshold;
            this.upperWarningThreshold = upperWarningThreshold;
            return this;
        }

        public Builder onlyErrors(LowerErrorThreshold lowerErrorThreshold, UpperErrorThreshold upperErrorThreshold) {
            this.lowerErrorThreshold = lowerErrorThreshold;
            this.upperErrorThreshold = upperErrorThreshold;
            this.lowerWarningThreshold = LowerWarningThreshold.of(Double.NEGATIVE_INFINITY);
            this.upperWarningThreshold = UpperWarningThreshold.of(Double.POSITIVE_INFINITY);
            return this;
        }

        public Builder onlyUpperThresholds(UpperWarningThreshold upperWarningThreshold, UpperErrorThreshold upperErrorThreshold) {
            this.lowerErrorThreshold = LowerErrorThreshold.of(Double.NEGATIVE_INFINITY);
            this.upperErrorThreshold = upperErrorThreshold;
            this.lowerWarningThreshold = LowerWarningThreshold.of(Double.NEGATIVE_INFINITY);
            this.upperWarningThreshold = upperWarningThreshold;
            return this;
        }

        public Builder onlyLowerThresholds(LowerWarningThreshold lowerWarningThreshold, LowerErrorThreshold lowerErrorThreshold) {
            this.lowerErrorThreshold = lowerErrorThreshold;
            this.upperErrorThreshold = UpperErrorThreshold.of(Double.POSITIVE_INFINITY);
            this.lowerWarningThreshold = lowerWarningThreshold;
            this.upperWarningThreshold = UpperWarningThreshold.of(Double.POSITIVE_INFINITY);
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
