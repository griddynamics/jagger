package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;

import java.util.Collections;
import java.util.List;

/**
 * Corresponds to plots on "Metric" tab
 * User: amikryukov
 * Date: 12/2/13
 */
// Completely repeats MetricNode, but will be handled by different handler in tree
public class PlotNode extends MetricNode {

    public PlotNode (MetricNameDto metricName) {
        super(metricName);
    }

    public PlotNode() {
        super();
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
