package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowErrThres;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowWarnThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpWarnThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpErrThresh;

import java.util.Objects;

/**
 *
 */
public abstract class JLimit {

    private String metricName;
    private Double lowWarnThresh;
    private Double upperWarningThreshold;
    private Double lowerErrorThreshold;
    private Double upperErrorThreshold;


    JLimit(Builder builder) {
        this.metricName = builder.metricName;
        this.lowWarnThresh = builder.lowWarnThresh.value();
        this.upperWarningThreshold = builder.upWarnThresh.value();
        this.lowerErrorThreshold = builder.lowErrThres.value();
        this.upperErrorThreshold = builder.upErrThresh.value();
    }


    public abstract static class Builder {
        String metricName;
        LowWarnThresh lowWarnThresh = LowWarnThresh.of(1.0);
        UpWarnThresh upWarnThresh = UpWarnThresh.of(1.0);
        LowErrThres lowErrThres = LowErrThres.of(1.0);
        UpErrThresh upErrThresh = UpErrThresh.of(1.0);

        // I'm really sorry for that, but I have no idea how to do it better.
        private boolean initialized;


        public Builder onlyWarnings(LowWarnThresh lowWarnThresh, UpWarnThresh upWarnThresh) {
            if (initialized) {
                throw new IllegalArgumentException("The limits cannot be initialized more than once.");
            }
            Objects.requireNonNull(lowWarnThresh);
            Objects.requireNonNull(upWarnThresh);

            this.lowErrThres = LowErrThres.of(Double.NEGATIVE_INFINITY);
            this.upErrThresh = UpErrThresh.of(Double.POSITIVE_INFINITY);
            this.lowWarnThresh = lowWarnThresh;
            this.upWarnThresh = upWarnThresh;

            initialized = true;

            return this;
        }

        public Builder onlyErrors(LowErrThres lowErrThres, UpErrThresh upErrThresh) {
            if (initialized) {
                throw new IllegalArgumentException("The limits cannot be initialized more than once.");
            }
            Objects.requireNonNull(lowErrThres);
            Objects.requireNonNull(upErrThresh);

            this.lowErrThres = lowErrThres;
            this.upErrThresh = upErrThresh;
            this.lowWarnThresh = LowWarnThresh.of(Double.NEGATIVE_INFINITY);
            this.upWarnThresh = UpWarnThresh.of(Double.POSITIVE_INFINITY);

            initialized = true;

            return this;
        }

        public Builder onlyUpperThresholds(UpWarnThresh upWarnThresh, UpErrThresh upErrThresh) {
            if (initialized) {
                throw new IllegalArgumentException("The limits cannot be initialized more than once.");
            }
            Objects.requireNonNull(upWarnThresh);
            Objects.requireNonNull(upErrThresh);

            this.lowErrThres = LowErrThres.of(Double.NEGATIVE_INFINITY);
            this.upErrThresh = upErrThresh;
            this.lowWarnThresh = LowWarnThresh.of(Double.NEGATIVE_INFINITY);
            this.upWarnThresh = upWarnThresh;

            initialized = true;

            return this;
        }

        public Builder onlyLowerThresholds(LowWarnThresh lowWarnThresh, LowErrThres lowErrThres) {
            if (initialized) {
                throw new IllegalArgumentException("The limits cannot be initialized more than once.");
            }
            Objects.requireNonNull(lowWarnThresh);
            Objects.requireNonNull(lowErrThres);

            this.lowErrThres = lowErrThres;
            this.upErrThresh = UpErrThresh.of(Double.POSITIVE_INFINITY);
            this.lowWarnThresh = lowWarnThresh;
            this.upWarnThresh = UpWarnThresh.of(Double.POSITIVE_INFINITY);

            initialized = true;

            return this;
        }

        public Builder exactLimits(LowWarnThresh lowWarnThresh, LowErrThres lowErrThres, UpWarnThresh upWarnThresh, UpErrThresh upErrThresh) {
            if (initialized) {
                throw new IllegalArgumentException("The limits cannot be initialized more than once.");
            }
            Objects.requireNonNull(lowWarnThresh);
            Objects.requireNonNull(lowErrThres);
            Objects.requireNonNull(upWarnThresh);
            Objects.requireNonNull(upErrThresh);

            this.lowErrThres = lowErrThres;
            this.upErrThresh = upErrThresh;
            this.lowWarnThresh = lowWarnThresh;
            this.upWarnThresh = upWarnThresh;

            initialized = true;

            return this;
        }


        public abstract JLimit build();

    }

    public String getMetricName() {
        return metricName;
    }

    public Double getLowWarnThresh() {
        return lowWarnThresh;
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
