package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeViewGroupMetricsToNodeRule extends Rule {

    public TreeViewGroupMetricsToNodeRule() {}
    public TreeViewGroupMetricsToNodeRule(String id, String displayName, String rule) {
        super(id,displayName,rule);
    }
    public TreeViewGroupMetricsToNodeRule(By filterBy, String id, String displayName, String rule) {
        super(filterBy,id,displayName,rule);
    }

    public <M extends MetricNode> List<M> filter(String parentId, List<M> metricNodeList) {

        List<M> result = new ArrayList<M>();
        M resultMetricNode = null;
        List<MetricNameDto> metricNameDtoList = new ArrayList<MetricNameDto>();

        Iterator<M> i = metricNodeList.iterator();
        while (i.hasNext()) {
            M metricNode = i.next();

            // match
            String metric;
            // node can contain more than single metric
            // current strategy: if at least one metric match => take it
            for (MetricNameDto metricNameDto : metricNode.getMetricNameDtoList()) {
                if (filterBy == By.DISPLAY_NAME) {
                    metric = metricNameDto.getMetricDisplayName();
                }
                else {
                    metric = metricNameDto.getMetricName();
                }
                if (metric.matches(rule)) {
                    //create result single time
                    if (resultMetricNode == null) {
                        resultMetricNode = metricNode;
                    }
                    metricNameDtoList.addAll(metricNode.getMetricNameDtoList());
                    i.remove();
                    break;
                }
            }
        }

        // result - compose single node from filtered list
        if (resultMetricNode != null) {
            // N goes for node
            resultMetricNode.init(parentId + "N_" + id,displayName,metricNameDtoList);
            result.add(resultMetricNode);
            return result;
        }
        else {
            return null;
        }
    }

    public static class Composer extends TreeViewGroupMetricsToNodeRule {
        private List<TreeViewGroupMetricsToNodeRule> treeViewGroupMetricsToNodeRules;

        public Composer(List<TreeViewGroupMetricsToNodeRule> treeViewGroupMetricsToNodeRules) {
            // remove duplicates and sort
            List<TreeViewGroupMetricsToNodeRule> temp = removeDuplicates(By.ID,treeViewGroupMetricsToNodeRules);
            this.treeViewGroupMetricsToNodeRules = sort(By.DISPLAY_NAME,temp);
        }

        public static TreeViewGroupMetricsToNodeRule compose(List<TreeViewGroupMetricsToNodeRule> treeViewGroupMetricsToNodeRules){
            return new Composer(treeViewGroupMetricsToNodeRules);
        }

        @Override
        public <M extends MetricNode> List<M> filter(String parentId, List<M> metricNodeList) {
            List<M> result = new ArrayList<M>();

            List<M> tempResult;
            for (TreeViewGroupMetricsToNodeRule rule : treeViewGroupMetricsToNodeRules) {
                tempResult = rule.filter(parentId,metricNodeList);
                if (tempResult != null) {
                    result.addAll(tempResult);
                }
            }

            if (result.size() > 0) {
                return result;
            }
            else {
                return null;
            }
        }
    }
}
