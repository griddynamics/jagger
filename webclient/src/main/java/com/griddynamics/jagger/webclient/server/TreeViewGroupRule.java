package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.components.control.model.MetricGroupNode;
import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.griddynamics.jagger.webclient.client.mvp.NameTokens;

import java.util.*;

public class TreeViewGroupRule {

    public TreeViewGroupRule(String id, String displayName, String rule) {
        this.id = id;
        this.displayName = displayName;
        this.rule = rule;
    }
    public TreeViewGroupRule(String id, String displayName, String rule, List<TreeViewGroupRule> children) {
        this.id = id;
        this.displayName = displayName;
        this.rule = rule;
        this.children = sortByDisplayName(children);
    }
    public TreeViewGroupRule(String id, String displayName, List<TreeViewGroupRule> children) {
        this.id = id;
        this.displayName = displayName;
        this.children = sortByDisplayName(children);
    }

    public <M extends MetricNode> MetricGroupNode<M> filter(NameTokens.FilterOptions filterBy, String parentId, List<M> metricNodeList) {
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
            id = parentId + "_" + this.id;
        }

        // unique Id for metric group
        result.setId(id);

        // first ask all children to filter
        List<MetricGroupNode> metricGroupNodeListFromChildren = new ArrayList<MetricGroupNode>();
        if (children != null) {
            for (TreeViewGroupRule child : children) {
                MetricGroupNode childResult = child.filter(filterBy,id,metricNodeList);
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
                if (filterBy == NameTokens.FilterOptions.BY_DISPLAY_NAME) {
                    metric = metricNode.getMetricNameDto().getMetricDisplayName();
                }
                else {
                    metric = metricNode.getMetricNameDto().getMetricName();
                }
                if (metric.matches(rule)) {
                    metricsPerRule.add(metricNode);
                    i.remove();
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

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRule() {
        return rule;
    }

    public List<TreeViewGroupRule> getChildren() {
        return children;
    }

    private List<TreeViewGroupRule> sortByDisplayName(List<TreeViewGroupRule> metricGroupRuleList) {
        Collections.sort(metricGroupRuleList, new Comparator<TreeViewGroupRule>() {
            @Override
            public int compare(TreeViewGroupRule o1, TreeViewGroupRule o2) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getDisplayName(), o2.getDisplayName());
                return (res != 0) ? res : o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        return metricGroupRuleList;
    }

    private String id;
    private String displayName;
    private String rule = null;
    private List<TreeViewGroupRule> children = null;

}
