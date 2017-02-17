package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil.IdContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.extractDisplayNameFromGenerated;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.extractIdsFromGeneratedIdForScenarioComponents;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.generateScenarioRegexp;
import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.generateScenarioStepRegexp;

@SuppressWarnings("Duplicates")
@Component
public class TreeViewGroupRuleProvider {

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    @Resource
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public TreeViewGroupRule provide(String rootId, String rootName, Map<String, String> scenarioComponentsIdToDisplayName) {
        List<TreeViewGroupRule> firstLevelFilters = new ArrayList<>();

        String filterRegex = "(" +
                "^" + StandardMetricsNamesUtil.THROUGHPUT_TPS + "$|" +
                "^" + StandardMetricsNamesUtil.THROUGHPUT + "$|" +
                "^" + StandardMetricsNamesUtil.LATENCY_SEC + ".*"  + "$|" +
                "^" + StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC + "$|" +
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

        // Filters for user scenarios
        Map<String, Map<String, TreeViewGroupRule>> scenarioStepsRules = new HashMap<>();
        Map<String, String> scenarioRegexps = new HashMap<>();
        Map<String, String> scenarioDisplayNames = new HashMap<>();
        scenarioComponentsIdToDisplayName.forEach((generatedId, generatedDisplayName) -> {
            IdContainer originalIds = extractIdsFromGeneratedIdForScenarioComponents(generatedId);
            String originalDisplayName = extractDisplayNameFromGenerated(generatedDisplayName);

            if (originalIds != null) {
                // if step
                if (!originalIds.getScenarioId().equals(originalIds.getStepId())) {
                    String nodeId = originalIds.getScenarioId() + ":" + originalIds.getStepId();
                    String nodeDisplayName = originalDisplayName != null ? originalDisplayName : originalIds.getStepId();
                    String filter = generateScenarioStepRegexp(originalIds.getScenarioId(), originalIds.getStepId());
                    TreeViewGroupRule userStepFilter = new TreeViewGroupRule(Rule.By.ID, nodeId, nodeDisplayName, filter);
                    if (scenarioStepsRules.containsKey(originalIds.getScenarioId())) {
                        scenarioStepsRules.get(originalIds.getScenarioId()).put(originalIds.getStepId(), userStepFilter);
                    } else {
                        HashMap<String, TreeViewGroupRule> map = new HashMap<>();
                        map.put(originalIds.getStepId(), userStepFilter);
                        scenarioStepsRules.put(originalIds.getScenarioId(), map);
                    }
                    // if scenario
                } else {
                    scenarioRegexps.putIfAbsent(originalIds.getScenarioId(), generateScenarioRegexp(originalIds.getScenarioId()));
                    scenarioDisplayNames.putIfAbsent(originalIds.getScenarioId(), originalDisplayName);
                }
            }
        });

        scenarioRegexps.forEach((scenarioId, filter) -> {
            List<TreeViewGroupRule> childrenRules = newArrayList(scenarioStepsRules.get(scenarioId).values());
            String displayName = scenarioDisplayNames.get(scenarioId);
            firstLevelFilters.add(new TreeViewGroupRule(Rule.By.ID, scenarioId, displayName, filter, childrenRules));
        });

        // Root filter - will match all metrics
        return new TreeViewGroupRule(Rule.By.ID, rootId, rootName, ".*", firstLevelFilters);
    }
}
