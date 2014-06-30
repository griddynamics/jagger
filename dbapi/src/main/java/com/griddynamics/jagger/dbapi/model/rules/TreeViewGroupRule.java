package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.model.MetricGroupNode;
import com.griddynamics.jagger.dbapi.model.MetricNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeViewGroupRule extends Rule{

    public TreeViewGroupRule(String id, String displayName, String rule) {
        super(id,displayName,rule);
    }
    public TreeViewGroupRule(By filterBy, String id, String displayName, String rule) {
        super(filterBy,id,displayName,rule);
    }
    public TreeViewGroupRule(String id, String displayName, String rule, List<TreeViewGroupRule> children) {
        super(id,displayName,rule);
        AssignChildren(children);
    }
    public TreeViewGroupRule(By filterBy, String id, String displayName, String rule, List<TreeViewGroupRule> children) {
        super(filterBy,id,displayName,rule);
        AssignChildren(children);
    }
    private void AssignChildren (List<TreeViewGroupRule> children) {
        // remove duplicates and sort
        List<TreeViewGroupRule> temp = removeDuplicates(By.ID,children);
        this.children = sort(By.DISPLAY_NAME, temp);
    }

    public <M extends MetricNode> MetricGroupNode<M> filter(String parentId, List<M> metricNodeList) {
        MetricGroupNode<M> result = new MetricGroupNode<M>(displayName);
        boolean returnResult = false;

        // null is required only for root node
        // when no parent available
        String id;
        if (parentId == null) {
            id = this.id;
        }
        else {
            // depth of tree is not limited => be careful with long id. They will concatenate
            // G goes for group
            id = parentId + "G_" + this.id;
        }

        // unique Id for metric group
        result.setId(id);

        // first ask all children to filter
        List<MetricGroupNode<M>> metricGroupNodeListFromChildren = new ArrayList<MetricGroupNode<M>>();
        if (children != null) {
            for (TreeViewGroupRule child : children) {
                MetricGroupNode<M> childResult = child.filter(id,metricNodeList);
                if (childResult != null) {
                    metricGroupNodeListFromChildren.add(childResult);
                }
            }
            if (metricGroupNodeListFromChildren.size() > 0) {
                result.setMetricGroupNodeList(metricGroupNodeListFromChildren);
                returnResult = true;
            }
        }

        // apply own filter
        if (rule != null) {
            List<M> metricsPerRule = new ArrayList<M>();
            Iterator<M> i = metricNodeList.iterator();
            while (i.hasNext()) {
                M metricNode = i.next();

                // match
                String metric;
                // node can contain more than single metric
                // current strategy: if at least one metric match => add node to group
                for (MetricNameDto metricNameDto : metricNode.getMetricNameDtoList()) {
                    if (filterBy == By.DISPLAY_NAME) {
                        metric = metricNameDto.getMetricDisplayName();
                    }
                    else {
                        metric = metricNameDto.getMetricName();
                    }
                    if (metric.matches(rule)) {
                        metricsPerRule.add(metricNode);
                        i.remove();
                        break;
                    }
                }
            }
            if (metricsPerRule.size() > 0) {
                result.setMetrics(metricsPerRule);
                returnResult = true;
            }
        }

        if (returnResult) {
            return result;
        }
        else {
            return null;
        }
    }

    public List<TreeViewGroupRule> getChildren() {
        return children;
    }

    private List<TreeViewGroupRule> children = null;

}
