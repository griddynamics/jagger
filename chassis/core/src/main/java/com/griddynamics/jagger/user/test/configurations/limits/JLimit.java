package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowErrThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowWarnThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpWarnThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpErrThresh;

import java.util.Objects;

/**
 * Allow to compare your results with predefined reference values or baseline session values
 * and decide whether performance of your system meet acceptance criteria or not. As a result of comparison you
 * can make decision and mark this test session with status flag (OK, WARNING, FATAL, ERROR).
 * In WebUI and PDF report summary values will be highlighted according to results of comparison.
 */
public abstract class JLimit {

    private final String metricId;
    private final Double lowWarnThresh;
    private final Double upperWarningThreshold;
    private final Double lowerErrorThreshold;
    private final Double upperErrorThreshold;

    JLimit(Builder builder) {
        this.metricId = builder.metricId;
        this.lowWarnThresh = builder.lowWarnThresh.value();
        this.upperWarningThreshold = builder.upWarnThresh.value();
        this.lowerErrorThreshold = builder.lowErrThresh.value();
        this.upperErrorThreshold = builder.upErrThresh.value();
    }


    public abstract static class Builder {
        String metricId;
        LowWarnThresh lowWarnThresh = LowWarnThresh.of(1.0);
        UpWarnThresh upWarnThresh = UpWarnThresh.of(1.0);
        LowErrThresh lowErrThresh = LowErrThresh.of(1.0);
        UpErrThresh upErrThresh = UpErrThresh.of(1.0);

        // I'm really sorry for that, but I have no idea how to do it better.
        private boolean initialized;


        /**
         * Set limits for warnings criteria only.
         * Cannot be initialized more than once.
         *
         * @param lowWarnThresh relative lower warning threshold.
         * @param upWarnThresh  relative upper warning threshold.
         */
        public Builder withOnlyWarnings(LowWarnThresh lowWarnThresh, UpWarnThresh upWarnThresh) {
            if (initialized) {
                throw new IllegalArgumentException("It is already initialized with values: " +
                        this.lowWarnThresh + ", " + this.lowErrThresh + ", " + this.upWarnThresh + ", " + this.upErrThresh);
            }
            Objects.requireNonNull(lowWarnThresh);
            Objects.requireNonNull(upWarnThresh);

            this.lowErrThresh = LowErrThresh.of(Double.NEGATIVE_INFINITY);
            this.upErrThresh = UpErrThresh.of(Double.POSITIVE_INFINITY);
            this.lowWarnThresh = lowWarnThresh;
            this.upWarnThresh = upWarnThresh;

            initialized = true;

            return this;
        }

        /**
         * Set limits for errors criteria only.
         * Cannot be initialized more than once.
         *
         * @param lowErrThresh relative lower error threshold.
         * @param upErrThresh  relative upper error threshold.
         */
        public Builder withOnlyErrors(LowErrThresh lowErrThresh, UpErrThresh upErrThresh) {
            if (initialized) {
                throw new IllegalArgumentException("It is already initialized with values: " +
                        this.lowWarnThresh + ", " + this.lowErrThresh + ", " + this.upWarnThresh + ", " + this.upErrThresh);
            }
            Objects.requireNonNull(lowErrThresh);
            Objects.requireNonNull(upErrThresh);

            this.lowErrThresh = lowErrThresh;
            this.upErrThresh = upErrThresh;
            this.lowWarnThresh = LowWarnThresh.of(this.lowErrThresh.value());
            this.upWarnThresh = UpWarnThresh.of(this.upErrThresh.value());

            initialized = true;

            return this;
        }

        /**
         * Set limits for upper limits only.
         * Cannot be initialized more than once.
         *
         * @param upWarnThresh relative upper warning threshold.
         * @param upErrThresh  relative upper error threshold.
         */
        public Builder withOnlyUpperThresholds(UpWarnThresh upWarnThresh, UpErrThresh upErrThresh) {
            if (initialized) {
                throw new IllegalArgumentException("It is already initialized with values: " +
                        this.lowWarnThresh + ", " + this.lowErrThresh + ", " + this.upWarnThresh + ", " + this.upErrThresh);
            }
            Objects.requireNonNull(upWarnThresh);
            Objects.requireNonNull(upErrThresh);

            this.lowErrThresh = LowErrThresh.of(Double.NEGATIVE_INFINITY);
            this.upErrThresh = upErrThresh;
            this.lowWarnThresh = LowWarnThresh.of(Double.NEGATIVE_INFINITY);
            this.upWarnThresh = upWarnThresh;

            initialized = true;

            return this;
        }

        /**
         * Set limits for warnings criteria only.
         * Cannot be initialized more than once.
         *
         * @param lowErrThresh  relative lower error threshold.
         * @param lowWarnThresh relative lower warning threshold.
         */
        public Builder withOnlyLowerThresholds(LowErrThresh lowErrThresh, LowWarnThresh lowWarnThresh) {
            if (initialized) {
                throw new IllegalArgumentException("It is already initialized with values: " +
                        this.lowWarnThresh + ", " + this.lowErrThresh + ", " + this.upWarnThresh + ", " + this.upErrThresh);
            }
            Objects.requireNonNull(lowWarnThresh);
            Objects.requireNonNull(lowErrThresh);

            this.lowErrThresh = lowErrThresh;
            this.upErrThresh = UpErrThresh.of(Double.POSITIVE_INFINITY);
            this.lowWarnThresh = lowWarnThresh;
            this.upWarnThresh = UpWarnThresh.of(Double.POSITIVE_INFINITY);

            initialized = true;

            return this;
        }

        /**
         * Set all limits.
         * Cannot be initialized more than once.
         *
         * @param lowErrThresh  relative lower error threshold.
         * @param lowWarnThresh relative lower warning threshold.
         * @param upWarnThresh  relative upper warning threshold.
         * @param upErrThresh   relative upper error threshold.
         */
        public Builder withExactLimits(LowErrThresh lowErrThresh, LowWarnThresh lowWarnThresh, UpWarnThresh upWarnThresh, UpErrThresh upErrThresh) {
            if (initialized) {
                throw new IllegalArgumentException("It is already initialized with values: " +
                        this.lowErrThresh + ", " + this.lowWarnThresh + ", " + this.upWarnThresh + ", " + this.upErrThresh);
            }
            Objects.requireNonNull(lowWarnThresh);
            Objects.requireNonNull(lowErrThresh);
            Objects.requireNonNull(upWarnThresh);
            Objects.requireNonNull(upErrThresh);

            this.lowErrThresh = lowErrThresh;
            this.upErrThresh = upErrThresh;
            this.lowWarnThresh = lowWarnThresh;
            this.upWarnThresh = upWarnThresh;

            initialized = true;

            return this;
        }

        /**
         * Create {@link JLimit} instance.
         * If non of methods that sets limits were called the default value 1.0 for all limits will be set.
         *
         * @return instance of {@link JLimit}.
         */
        public abstract JLimit build();

    }

    public String getMetricId() {
        return metricId;
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
