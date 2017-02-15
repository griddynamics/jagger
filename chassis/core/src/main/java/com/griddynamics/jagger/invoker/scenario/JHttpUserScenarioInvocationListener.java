package com.griddynamics.jagger.invoker.scenario;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.griddynamics.jagger.invoker.scenario.DefaultAggregatorsProvider.AVG_AGGREGATOR;
import static com.griddynamics.jagger.invoker.scenario.DefaultAggregatorsProvider.FAILS_AGGREGATOR;
import static com.griddynamics.jagger.invoker.scenario.DefaultAggregatorsProvider.MAX_AGGREGATOR;
import static com.griddynamics.jagger.invoker.scenario.DefaultAggregatorsProvider.MIN_AGGREGATOR;
import static com.griddynamics.jagger.invoker.scenario.DefaultAggregatorsProvider.PERCENTILE_AGGREGATOR;
import static com.griddynamics.jagger.invoker.scenario.DefaultAggregatorsProvider.STD_DEV_AGGREGATOR;
import static com.griddynamics.jagger.invoker.scenario.DefaultAggregatorsProvider.SUCCESS_AGGREGATOR;
import static com.griddynamics.jagger.invoker.scenario.DefaultAggregatorsProvider.SUM_AGGREGATOR;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.ITERATIONS_SAMPLES;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.ITERATION_SAMPLES_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_SEC;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.SUCCESS_RATE;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.generateMetricDisplayName;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.generateMetricId;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.generateScenarioId;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.generateScenarioStepId;

/**
 * This invocation listener adds default metrics to invocations of {@link JHttpUserScenarioInvoker} such as
 * Success rate, Iteration samples and Latency.
 */
public class JHttpUserScenarioInvocationListener extends ServicesAware implements Provider<InvocationListener> {
    private final Set<String> createdMetrics = new ConcurrentSkipListSet<>();
    private List<MetricAggregatorProvider> latencyAggregatorProviders = new ArrayList<>();

    public JHttpUserScenarioInvocationListener() {}

    private JHttpUserScenarioInvocationListener(Builder builder) {
        this.latencyAggregatorProviders = newArrayList(builder.latencyAggregatorProviders);
    }

    public static Builder builder() {return new Builder();}

    public static class Builder {
        Set<MetricAggregatorProvider> latencyAggregatorProviders = new HashSet<>();

        public Builder withLatencyAvgStddevAggregators() {
            this.latencyAggregatorProviders.addAll(newArrayList(STD_DEV_AGGREGATOR, AVG_AGGREGATOR));
            return this;
        }

        public Builder withLatencyMinMaxAggregators() {
            this.latencyAggregatorProviders.addAll(newArrayList(MAX_AGGREGATOR, MIN_AGGREGATOR));
            return this;
        }

        public Builder withLatencyPercentileAggregators(Double percentile, Double... percentiles) {
            this.latencyAggregatorProviders.add(PERCENTILE_AGGREGATOR(percentile));
            Arrays.stream(percentiles).forEach(p -> this.latencyAggregatorProviders.add(PERCENTILE_AGGREGATOR(p)));
            return this;
        }

        public JHttpUserScenarioInvocationListener build() {
            return new JHttpUserScenarioInvocationListener(this);
        }
    }

    @Override
    protected void init() {}

    @Override
    public InvocationListener provide() {
        return new InvocationListener() {
            @Override
            public void onStart(InvocationInfo invocationInfo) { }

            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
                if (invocationInfo.getResult() != null) {
                    JHttpUserScenarioInvocationResult invocationResult = ((JHttpUserScenarioInvocationResult) invocationInfo.getResult());
                    List<JHttpUserScenarioStepInvocationResult> stepInvocationResults = invocationResult.getStepInvocationResults();
                    String scenarioId = generateScenarioId(invocationResult.getScenarioId());

                    // Create & save scenario metrics
                    if (!createdMetrics.contains(scenarioId)) {
                        createdMetrics.add(scenarioId);
                        createScenarioMetricDescriptions(scenarioId, invocationResult.getScenarioDisplayName());
                    }
                    getMetricService().saveValue(generateMetricId(scenarioId, ITERATION_SAMPLES_ID), 1);
                    getMetricService().saveValue(generateMetricId(scenarioId, LATENCY_ID),
                            invocationInfo.getDuration() / 1000.0); // ms -> s
                    getMetricService().saveValue(generateMetricId(scenarioId, SUCCESS_RATE), invocationResult.getSucceeded() ? 1 : 0);


                    // Create & save step metrics
                    Integer stepIndex = 1; // index 0 for scenario (not steps) metrics
                    for (JHttpUserScenarioStepInvocationResult stepResult : stepInvocationResults) {
                        String scenarioStepId = generateScenarioStepId(invocationResult.getScenarioId(), stepResult.getStepId(), stepIndex);
                        if (!createdMetrics.contains(scenarioStepId)) {
                            createdMetrics.add(scenarioStepId);
                            createScenarioStepMetricDescriptions(scenarioStepId,String.format("%02d. %s ", stepIndex, stepResult.getStepDisplayName()));
                        }
                        getMetricService().saveValue(generateMetricId(scenarioStepId, LATENCY_ID),
                                stepResult.getLatency().doubleValue() / 1000); // ms -> s
                        getMetricService().saveValue(generateMetricId(scenarioStepId, SUCCESS_RATE), stepResult.getSucceeded() ? 1 : 0);
                        getMetricService().saveValue(generateMetricId(scenarioStepId, ITERATION_SAMPLES_ID), 1);

                        stepIndex++;
                    }
                }
            }

            private void createScenarioMetricDescriptions(String scenarioId, String scenarioDisplayName) {
                getMetricService().createMetric(
                        new MetricDescription(generateMetricId(scenarioId, ITERATION_SAMPLES_ID)).
                                displayName(generateMetricDisplayName(scenarioDisplayName, ITERATIONS_SAMPLES)).
                                addAggregator(SUM_AGGREGATOR));

                getMetricService().createMetric(
                        new MetricDescription(generateMetricId(scenarioId, LATENCY_ID)).
                                displayName(generateMetricDisplayName(scenarioDisplayName, LATENCY_SEC)).
                                addAggregator(AVG_AGGREGATOR));

                getMetricService().createMetric(
                        new MetricDescription(generateMetricId(scenarioId, SUCCESS_RATE)).
                                displayName(generateMetricDisplayName(scenarioDisplayName, SUCCESS_RATE)).
                                plotData(true).
                                addAggregator(SUCCESS_AGGREGATOR).
                                addAggregator(FAILS_AGGREGATOR));
            }

            private void createScenarioStepMetricDescriptions(String scenarioStepId, String scenarioStepDisplayName) {
                MetricDescription metricDescription =
                        new MetricDescription(generateMetricId(scenarioStepId,LATENCY_ID)).
                                displayName(generateMetricDisplayName(scenarioStepDisplayName,LATENCY_SEC))
                                .plotData(true);
                if (latencyAggregatorProviders.isEmpty())
                    latencyAggregatorProviders.addAll(newHashSet(MAX_AGGREGATOR, MIN_AGGREGATOR, STD_DEV_AGGREGATOR, AVG_AGGREGATOR));
                latencyAggregatorProviders.forEach(metricDescription::addAggregator);
                getMetricService().createMetric(metricDescription);

                getMetricService().createMetric(
                        new MetricDescription(generateMetricId(scenarioStepId, ITERATION_SAMPLES_ID)).
                                displayName(generateMetricDisplayName(scenarioStepDisplayName, ITERATIONS_SAMPLES)).
                                addAggregator(SUM_AGGREGATOR));

                getMetricService().createMetric(
                        new MetricDescription(generateMetricId(scenarioStepId, SUCCESS_RATE)).
                                displayName(generateMetricDisplayName(scenarioStepDisplayName, SUCCESS_RATE)).
                                plotData(true).
                                addAggregator(SUCCESS_AGGREGATOR).
                                addAggregator(FAILS_AGGREGATOR));
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) {
                //TODO: JFG-1122
            }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) {
                //TODO: JFG-1122
            }
        };
    }
}
