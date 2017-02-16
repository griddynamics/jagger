package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.JMetricName;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

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
     * @param metricId unique id of the metric.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(String metricId) {
        return new Builder(metricId);
    }
    
    
    /**
     * Builder for {@link JLimit} to compare metrics,
     * collected during {@link com.griddynamics.jagger.invoker.scenario.JHttpUserScenario} execution, with the baseline.
     *
     * @param scenarioId unique id of a user scenario
     * @param stepId unique id of a user step
     * @param metricId unique id of a metric
     * @return a builder for {@link JLimit}
     */
    public static Builder builder(String scenarioId, String stepId, String metricId) {
        return new Builder(StandardMetricsNamesUtil.generateScenarioStepMetricRegexp(scenarioId, stepId, metricId));
    }
    
    /**
     * Builder for {@link JLimit} to compare with baseline.
     *
     * @param metricId unique id of the metric.
     * @return builder for {@link JLimit}.
     */
    public static Builder builder(JMetricName metricId) {
        return new Builder(metricId.transformToString());
    }
    
    /**
     * Builder for {@link JLimit} to compare metrics,
     * collected during {@link com.griddynamics.jagger.invoker.scenario.JHttpUserScenario} execution, with the baseline.
     *
     * @param scenarioId unique id of a user scenario
     * @param stepId unique id of a user step
     * @param metricId unique id of a metric
     * @return a builder for {@link JLimit}
     */
    public static Builder builder(String scenarioId, String stepId, JMetricName metricId) {
        return new Builder(StandardMetricsNamesUtil.generateScenarioStepMetricRegexp(scenarioId, stepId, metricId.transformToString()));
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
