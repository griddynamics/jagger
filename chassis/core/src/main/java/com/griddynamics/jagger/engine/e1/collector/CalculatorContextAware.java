package com.griddynamics.jagger.engine.e1.collector;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/18/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CalculatorContextAware<R> extends ContextAware {

    protected MetricCalculator<R> metricCalculator;

    public MetricCalculator<R> getMetricCalculator() {
        return metricCalculator;
    }

    public void setMetricCalculator(MetricCalculator<R> metricCalculator) {
        this.metricCalculator = metricCalculator;
    }
}
