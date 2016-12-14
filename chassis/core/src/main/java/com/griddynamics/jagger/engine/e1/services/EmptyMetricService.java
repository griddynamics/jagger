package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmptyMetricService implements MetricService {

    private static Logger log = LoggerFactory.getLogger(EmptyMetricService.class);

    private JaggerPlace jaggerPlace;

    public EmptyMetricService(JaggerPlace jaggerPlace) {
        this.jaggerPlace = jaggerPlace;
    }

    @Override
    public void createMetric(MetricDescription metricDescription) {
        log.warn("Can't create metric with id {}. MetricService is not supported in {}", metricDescription.getMetricId(), jaggerPlace);
    }

    @Override
    public void saveValue(String metricId, Number value) {
        log.warn("Can't save metric value with id {}. MetricService is not supported in {}", metricId, jaggerPlace);
    }

    @Override
    public void saveValue(String metricId, Number value, long timeStamp) {
        log.warn("Can't save metric value with id {}. MetricService is not supported in {}", metricId, jaggerPlace);
    }

    @Override
    public void flush() {
        log.warn("Can't to do flush. MetricService is not supported in {}", jaggerPlace);
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
