package com.griddynamics.jagger.test.javabuilders.load;

import com.griddynamics.jagger.engine.e1.collector.JHttpResponseStatusValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.test.javabuilders.utils.EndpointsProvider;
import com.griddynamics.jagger.test.javabuilders.utils.JaggerPropertiesProvider;
import com.griddynamics.jagger.test.javabuilders.utils.LoadDurationMetric;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.*;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaDuration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.DurationInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;

import java.util.Collections;


public class LoadTestsDefinition {
    protected final JLimit SUCCESS_LIMIT;
    protected final MaxDurationInSeconds defaultMaxDuration;
    protected JaggerPropertiesProvider provider;

    public LoadTestsDefinition(JaggerPropertiesProvider provider) {
        this.provider = provider;
        this.SUCCESS_LIMIT = JLimitVsRefValue.builder(JMetricName.PERF_SUCCESS_RATE_OK, RefValue.of(1.0)).build();
        this.defaultMaxDuration = MaxDurationInSeconds.of(provider.getIntPropertyValue("default.max.test.duration"));
    }

    /**
     * Create {@link JTestDefinition} with request to `sleep` service with specified sleepDelay
     * @param sleepDelay in ms
     **/
    protected JTestDefinition sleepTestDefinition(int sleepDelay) {
        return testDefinition("/sleep/" + sleepDelay, "/sleep/" + sleepDelay);
    }

    /**
     * @param id definition id
     * @param path query path
     * @return {@link JTestDefinition} with GET request on specified path
     */
    protected JTestDefinition testDefinition(String id, String path){
        return JTestDefinition.builder(Id.of(id), new EndpointsProvider(provider))
                .withQueryProvider(Collections.singletonList(new JHttpQuery().get().responseBodyType(String.class).path(path)))
                .addValidator(JHttpResponseStatusValidatorProvider.of(200))
                .withComment(id)
                .addListener(new LoadDurationMetric())
                .build();
    }

    /**
     * Create limit with deviation in percent from specified val.
     * val*(1-dev) <= metric value <= val*(1+dev)
     */
    protected JLimit deviationLimit(String metric, double val, double warnDeviation, double errDeviation) {
        return JLimitVsRefValue.builder(metric, RefValue.of(val))
                .withExactLimits(LowErrThresh.of(1 - errDeviation), LowWarnThresh.of(1 - warnDeviation),
                        UpWarnThresh.of(1 + warnDeviation), UpErrThresh.of(1 + errDeviation)).build();
    }

    /**
     * Create limit with deviation in percent from specified val.
     * val*(1-dev) <= metric value <= val*(1+dev)
     */
    protected JLimit deviationLimit(JMetricName metric, double val, double warnDeviation, double errDeviation) {
        return deviationLimit(metric.transformToString(), val, warnDeviation, errDeviation);
    }

    /**
     * Create limit to check that the metric has exact value.
     * metric value === val
     */
    protected JLimit exactValueLimit(JMetricName metric, double val){
        return JLimitVsRefValue.builder(metric, RefValue.of(val)).build();
    }

    /**
     * Create limit with deviations with absolute values.
     * val-deviation <= metric value <= val+deviation
     */
    protected JLimit absoluteDeviationLimit(JMetricName metricName, double val, double warnDeviation, double errDeviation){
        return deviationLimit(metricName, val, warnDeviation/val, errDeviation/val);
    }

    /**
     * Short form to create {@link JTerminationCriteriaIterations}
     */
    protected JTerminationCriteria iterTermination(long count){
        return JTerminationCriteriaIterations.of(IterationsNumber.of(count), defaultMaxDuration);
    }

    /**
     * Short form to create {@link JTerminationCriteriaDuration}
     */
    protected JTerminationCriteria durationTermination(long duration){
        return JTerminationCriteriaDuration.of(DurationInSeconds.of(duration));
    }
}
