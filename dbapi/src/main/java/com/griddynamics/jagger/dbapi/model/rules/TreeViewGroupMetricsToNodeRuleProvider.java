package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class TreeViewGroupMetricsToNodeRuleProvider {

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    @Resource
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    /// @param agentNames - required to group monitoring metrics by metric name, agent name (pass null when not required)
    /// @param percentiles - required to combine all percentiles to single node in Plots view (pass null when not required)
    /// @param forSummary = true => filters for Summary&Trends node in control tree, false => filters for Plots
    public TreeViewGroupMetricsToNodeRule provide(Map<String, Set<String>> agentNames, Set<Double> percentiles, boolean forSummary) {
        List<TreeViewGroupMetricsToNodeRule> result = new ArrayList<TreeViewGroupMetricsToNodeRule>();

        // Create rules to combine default monitor parameters
        if ((agentNames != null) && (agentNames.size() > 0)) {
            for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> groupKeyEntry : monitoringPlotGroups.entrySet()) {
                String metricDisplayName = groupKeyEntry.getKey().getUpperName();

                // group metrics to nodes by DefaultMonitoringParameters and AgentIds
                if (agentNames.containsKey(metricDisplayName)) {
                    for (String agentId : agentNames.get(metricDisplayName)) {
                        String regex = "";
                        for (DefaultMonitoringParameters defaultMonitoringParameters : groupKeyEntry.getValue()) {
                            // not first / first time
                            if (regex.length() != 0) {
                                regex += "|";
                            } else {
                                regex += "^.*(";
                            }
                            regex += defaultMonitoringParameters.getId();
                        }
                        String safeAgentId = MonitoringIdUtils.getEscapedStringForRegex(agentId);
                        regex += ").*" + safeAgentId + ".*";

                        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, metricDisplayName + "_" + agentId, agentId, regex));
                    }
                }
            }
        }

        // Create rules to combine standard metrics together
        if (forSummary) {
            result.addAll(provideRulesForSummaryNode(percentiles));
        } else {
            result.addAll(provideRulesForPlotsNode());
        }

        return TreeViewGroupMetricsToNodeRule.Composer.compose(result);
    }

    // For Summary&Trends node we are displaying all standard metrics
    // Every metric has separate node in tree
    private List<TreeViewGroupMetricsToNodeRule> provideRulesForSummaryNode(Set<Double> percentiles) {
        List<TreeViewGroupMetricsToNodeRule> result = new ArrayList<TreeViewGroupMetricsToNodeRule>();

        // Throughput
        String regex = "^(" +
                StandardMetricsNamesUtil.THROUGHPUT_ID + "|" +
                StandardMetricsNamesUtil.THROUGHPUT_OLD_ID +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.THROUGHPUT_ID,
                StandardMetricsNamesUtil.THROUGHPUT_TPS,
                regex));

        // Latency
        regex = "^(" +
                StandardMetricsNamesUtil.LATENCY_ID + "|" +
                StandardMetricsNamesUtil.LATENCY_OLD_ID +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.LATENCY_ID,
                StandardMetricsNamesUtil.LATENCY_SEC,
                regex));

        // Latency, std dev
        regex = "^(" +
                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID + "|" +
                StandardMetricsNamesUtil.LATENCY_STD_DEV_OLD_ID +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID,
                StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC,
                regex));

        // Duration
        regex = "^(" +
                StandardMetricsNamesUtil.DURATION_ID + "|" +
                StandardMetricsNamesUtil.DURATION_OLD_ID +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.DURATION_ID,
                StandardMetricsNamesUtil.DURATION_SEC,
                regex));


        // Samples
        regex = "^(" +
                StandardMetricsNamesUtil.ITERATION_SAMPLES_ID + "|" +
                StandardMetricsNamesUtil.ITERATION_SAMPLES_OLD_ID +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.ITERATION_SAMPLES_ID,
                StandardMetricsNamesUtil.ITERATIONS_SAMPLES,
                regex));

        // Success rate
        regex = "^(" +
                StandardMetricsNamesUtil.SUCCESS_RATE_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.SUCCESS_RATE_ID,
                StandardMetricsNamesUtil.SUCCESS_RATE,
                regex));
        //Virtual Users
        regex = "^(" +
                StandardMetricsNamesUtil.VIRTUAL_USERS_ID +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.VIRTUAL_USERS_ID,
                StandardMetricsNamesUtil.VIRTUAL_USERS,
                regex));

        if ((percentiles != null) && (!percentiles.isEmpty())) {
            for (Double percentile : percentiles) {
                String percentileNameNewModel = StandardMetricsNamesUtil.getLatencyMetricName(percentile, false);
                String percentileNameOldModel = StandardMetricsNamesUtil.getLatencyMetricName(percentile, true);
                regex = "^(" +
                        percentileNameNewModel + "|" +
                        percentileNameOldModel +
                        ")$";
                result.add(new TreeViewGroupMetricsToNodeRule(
                        Rule.By.ID,
                        percentileNameNewModel,
                        percentileNameNewModel,
                        regex));
            }
        }

        return result;
    }

    // For Plots node we are combining several metrics to single node (f.e. latency & latency std dev)
    private List<TreeViewGroupMetricsToNodeRule> provideRulesForPlotsNode() {
        List<TreeViewGroupMetricsToNodeRule> result = new ArrayList<TreeViewGroupMetricsToNodeRule>();

        // Throughput
        String regex = "^(" +
                StandardMetricsNamesUtil.THROUGHPUT_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.THROUGHPUT_ID,
                StandardMetricsNamesUtil.THROUGHPUT,
                regex));

        // Virtual Users
        regex = "^(" +
                StandardMetricsNamesUtil.VIRTUAL_USERS_ID + ".*" + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.VIRTUAL_USERS_ID,
                StandardMetricsNamesUtil.VIRTUAL_USERS,
                regex));

        // Latency
        regex = "^(" +
                StandardMetricsNamesUtil.LATENCY_ID + "|" +
                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.LATENCY_ID + StandardMetricsNamesUtil.LATENCY_STD_DEV_ID,
                StandardMetricsNamesUtil.LATENCY,
                regex));

        // Time Latency Percentile
        regex = "^" + StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX + "|" +
                StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE +
                "$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE + "_id",
                StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE,
                regex));

        return result;
    }
}
