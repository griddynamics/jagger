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

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.DURATION_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.DURATION_SEC;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.ITERATIONS_SAMPLES;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.ITERATION_SAMPLES_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_PERCENTILE_ID_REGEX;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_SEC;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_STD_DEV_AGG_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.SUCCESS_RATE;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.SUCCESS_RATE_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.THROUGHPUT;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.THROUGHPUT_ID;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.THROUGHPUT_TPS;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.VIRTUAL_USERS;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.VIRTUAL_USERS_ID;

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
        String regex = "^(" + THROUGHPUT_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, THROUGHPUT_ID, THROUGHPUT_TPS, regex));

        // Latency
        regex = "^(" + LATENCY_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, LATENCY_ID, LATENCY_SEC, regex));

        // Latency, std dev
        regex = "^(" + LATENCY_STD_DEV_AGG_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, LATENCY_STD_DEV_AGG_ID, LATENCY_STD_DEV_SEC, regex));

        // Duration
        regex = "^(" + DURATION_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, DURATION_ID, DURATION_SEC, regex));

        // Samples
        regex = "^(" + ITERATION_SAMPLES_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, ITERATION_SAMPLES_ID, ITERATIONS_SAMPLES, regex));

        // Success rate
        regex = "^(" + SUCCESS_RATE_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, SUCCESS_RATE_ID, SUCCESS_RATE, regex));
        //Virtual Users
        regex = "^(" + VIRTUAL_USERS_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, VIRTUAL_USERS_ID, VIRTUAL_USERS, regex));

        if ((percentiles != null) && (!percentiles.isEmpty())) {
            for (Double percentile : percentiles) {
                String percentileId = StandardMetricsNamesUtil.getLatencyMetricId(percentile);
                String percentileName = StandardMetricsNamesUtil.getLatencyMetricDisplayName(percentile);
                regex = "^" + percentileId + "$";
                result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, percentileId, percentileName, regex));
            }
        }

        return result;
    }

    // For Plots node we are combining several metrics to single node (f.e. latency & latency std dev)
    private List<TreeViewGroupMetricsToNodeRule> provideRulesForPlotsNode() {
        List<TreeViewGroupMetricsToNodeRule> result = new ArrayList<TreeViewGroupMetricsToNodeRule>();

        // Throughput
        String regex = "^(" + THROUGHPUT_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, THROUGHPUT_ID, THROUGHPUT, regex));

        // Virtual Users
        regex = "^(" + VIRTUAL_USERS_ID + ".*" + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID, VIRTUAL_USERS_ID, VIRTUAL_USERS, regex));

        // Latency
        regex = "^(" + LATENCY_ID + "|" + LATENCY_STD_DEV_AGG_ID + ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, LATENCY_ID + LATENCY_STD_DEV_AGG_ID, LATENCY_SEC, regex));

        // Time Latency Percentile
        regex = "^" + LATENCY_PERCENTILE_ID_REGEX + "|" + TIME_LATENCY_PERCENTILE + "$";
        result.add(new TreeViewGroupMetricsToNodeRule(Rule.By.ID, TIME_LATENCY_PERCENTILE + "_id", TIME_LATENCY_PERCENTILE, regex));

        return result;
    }
}
