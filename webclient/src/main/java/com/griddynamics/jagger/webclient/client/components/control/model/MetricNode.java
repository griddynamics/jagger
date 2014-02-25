package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;

import java.util.Collections;
import java.util.List;

/**
 * Corresponds to summary/trends metrics
 * User: amikryukov
 * Date: 11/26/13
 */
public class MetricNode extends AbstractIdentifyNode {

    private MetricNameDto metricNameDto;

    public MetricNode (MetricNameDto metricNameDto) {
        this.metricNameDto = metricNameDto;
    }

    public MetricNode() {}

    public MetricNameDto getMetricNameDto() {
        return metricNameDto;
    }

    public void setMetricNameDto(MetricNameDto metricNameDto) {
        this.metricNameDto = metricNameDto;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }

}
