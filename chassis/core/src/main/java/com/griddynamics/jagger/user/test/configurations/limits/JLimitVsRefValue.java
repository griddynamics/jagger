package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

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
     * Builder for {@link JLimit} to compare with the reference value.
     *
     * @param metricId unique id of a metric
     * @param refValue   the for comparison.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(String metricId, RefValue refValue) {
        return new Builder(metricId, refValue);
    }
    
    /**
     * Builder for {@link JLimit} o compare metrics,
     * collected during {@link com.griddynamics.jagger.invoker.scenario.JHttpUserScenario} execution, with the reference value.
     *
     * @param scenarioId unique id of a user scenario
     * @param stepId unique id of a user step
     * @param metricId unique id of a metric
     * @param refValue for comparison
     * @return a builder for {@link JLimit}
     */
    public static JLimitVsRefValue.Builder builder(String scenarioId, String stepId, String metricId, RefValue refValue) {
        return new Builder(StandardMetricsNamesUtil.generateScenarioStepMetricRegexp(scenarioId, stepId, metricId), refValue);
    }

    /**
     * Builder for {@link JLimit} to compare with the reference value.
     *
     * @param metricId unique id of a metric
     * @param refValue   the for comparison.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(JMetricName metricId, RefValue refValue) {
        return new Builder(metricId.transformToString(), refValue);
    }
    
    /**
     * Builder for {@link JLimit} o compare metrics,
     * collected during {@link com.griddynamics.jagger.invoker.scenario.JHttpUserScenario} execution, with the reference value.
     *
     * @param scenarioId is a unique id of a user scenario
     * @param stepId is a unique id of a user step
     * @param metricId is a unique id of a metric
     * @param refValue for comparison
     * @return a builder for {@link JLimit}
     */
    public static JLimitVsRefValue.Builder builder(String scenarioId, String stepId, JMetricName metricId, RefValue refValue) {
        return new Builder(StandardMetricsNamesUtil.generateScenarioStepMetricRegexp(scenarioId, stepId, metricId.transformToString()), refValue);
    }

    public static class Builder extends JLimit.Builder {
        private RefValue refValue;

        private Builder(String metricId, RefValue refValue) {
            Objects.requireNonNull(metricId);
            Objects.requireNonNull(refValue);

            this.metricId = metricId;
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
