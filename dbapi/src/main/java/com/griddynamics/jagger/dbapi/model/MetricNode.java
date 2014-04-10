package com.griddynamics.jagger.dbapi.model;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;

import java.util.Collections;
import java.util.List;

/**
 * Node represents single end-node (without children nodes) in control tree in WebUI
 * By default node contains description of one Metric (in MetricNameDto)
 * But it is possible to group several metrics under single node
 * In this case one node in tree, but several rows in Summary table, several lines in the plot
 * User: amikryukov
 * Date: 11/26/13
 */
public class MetricNode extends AbstractIdentifyNode {

    private List<MetricNameDto> metricNameDtoList;

    public MetricNode() {}

    public List<MetricNameDto> getMetricNameDtoList() {
        return metricNameDtoList;
    }

    public void setMetricNameDtoList(List<MetricNameDto> metricNameDtoList) {
        this.metricNameDtoList = metricNameDtoList;
    }

    // preferable way to set parameters
    public void init(String id, String displayName, List<MetricNameDto> metricNameDtoList) {
        this.id = id;
        this.displayName = displayName;
        this.metricNameDtoList = metricNameDtoList;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String toString() {
        String result = "";

        result += "MetricNode{\n";
        for (MetricNameDto metricNameDto : metricNameDtoList) {
            result += metricNameDto.toString();
        }
        result += "\n}";

        return result;
    }
}
