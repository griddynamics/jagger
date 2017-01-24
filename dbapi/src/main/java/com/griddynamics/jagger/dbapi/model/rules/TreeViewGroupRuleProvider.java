package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
@Component
public class TreeViewGroupRuleProvider {

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    @Resource
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public <M extends MetricNode> TreeViewGroupRule provide(String rootId, String rootName, List<M> metricNodes) {
        List<TreeViewGroupRule> firstLevelFilters = new ArrayList<>();

        String filterRegex = "(" +
                "^" + StandardMetricsNamesUtil.THROUGHPUT_TPS + "$|" +
                "^" + StandardMetricsNamesUtil.THROUGHPUT + "$|" +
                "^" + StandardMetricsNamesUtil.LATENCY_SEC + "$|" +
                "^" + StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC + "$|" +
                "^" + StandardMetricsNamesUtil.LATENCY + "$|" +
                "^" + StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX + "$|" +
                "^" + StandardMetricsNamesUtil.ITERATIONS_SAMPLES + "$|" +
                "^" + StandardMetricsNamesUtil.SUCCESS_RATE + ".*" + "$|" +
                "^" + StandardMetricsNamesUtil.DURATION_SEC + "$|" +
                "^" + StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE + "$|" +
                "^" + StandardMetricsNamesUtil.VIRTUAL_USERS + ".*" + "$" +
                ")";

        // Filter for Jagger main metrics. Space in display name will help to keep main parameters in the
        // top of alphabetic sorting
        TreeViewGroupRule mainParamsFirstLevelFilter = new TreeViewGroupRule(Rule.By.DISPLAY_NAME, "main", " Main parameters", filterRegex);
        firstLevelFilters.add(mainParamsFirstLevelFilter);

        // Filters for Jagger monitoring parameters
        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> groupKeyEntry : monitoringPlotGroups.entrySet()) {
            String groupDisplayName = groupKeyEntry.getKey().getUpperName();

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
            regex += ").*";

            firstLevelFilters.add(new TreeViewGroupRule(Rule.By.ID, groupDisplayName, groupDisplayName, regex));
        }

        String userScenarioRegex = "^.*USER-SCENARIO_(.*)_STEP#(\\d+)_(.*)(-.*)?METRIC$";
        Pattern pattern = Pattern.compile(userScenarioRegex);
        Map<String, Map<String, TreeViewGroupRule>> scenarioSteps = new HashMap<>();
        for (M metricNode : metricNodes) {
            String metricNodeId = metricNode.getId();
            Matcher matcher = pattern.matcher(metricNodeId);
            if (matcher.matches()) {
                String scenarioId = matcher.group(1);
                String stepNumber = matcher.group(2);
                String stepId = matcher.group(3);

                String nodeId = scenarioId + ":" + stepId;
                String stepRegex = "^.*USER-SCENARIO_" + scenarioId + "_STEP#" + stepNumber + "_.*$";
                TreeViewGroupRule userStepFilter = new TreeViewGroupRule(Rule.By.ID, nodeId, "Step " + stepNumber, stepRegex);
                if (scenarioSteps.containsKey(scenarioId)) {
                    scenarioSteps.get(scenarioId).put(stepNumber, userStepFilter);
                } else {
                    HashMap<String, TreeViewGroupRule> map = new HashMap<>();
                    map.put(stepNumber, userStepFilter);
                    scenarioSteps.put(scenarioId, map);
                }
            }
        }

        scenarioSteps.forEach((scenarioId, stepsRules) -> {
            String scenarioRegex = "(^.*USER-SCENARIO_" + scenarioId + "_STEP#.*$)|(^.*" + scenarioId + "-sum.*$)";
            List<TreeViewGroupRule> childrenRules = new ArrayList<>(stepsRules.values());
            firstLevelFilters.add(new TreeViewGroupRule(Rule.By.ID, scenarioId, scenarioId, scenarioRegex, childrenRules));
        });

        // Root filter - will match all metrics
        return new TreeViewGroupRule(Rule.By.ID, rootId, rootName, ".*", firstLevelFilters);
    }
}
