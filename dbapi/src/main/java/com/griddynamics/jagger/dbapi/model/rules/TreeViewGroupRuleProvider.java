package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeViewGroupRuleProvider {

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public TreeViewGroupRule provide (String rootId, String rootName) {

        List<TreeViewGroupRule> firstLevelFilters = new ArrayList<TreeViewGroupRule>();

        // Filter for Jagger main metrics. Space in display name will help to keep main parameters in the
        // top of alphabetic sorting
        TreeViewGroupRule mainParams_FirstLevelFilter = new TreeViewGroupRule(Rule.By.DISPLAY_NAME,"main"," Main parameters",
                "(^Throughput, tps$|^Throughput$|^Latency, sec$|^Latency$" +
                "|^Iterations, samples$|^Success rate$|^Duration, sec$|^Latency\\s\\S+\\s%$|^Time Latency Percentile$)");
        firstLevelFilters.add(mainParams_FirstLevelFilter);

        // Filters for Jagger monitoring parameters
        for(Map.Entry<GroupKey,DefaultMonitoringParameters[]> groupKeyEntry : monitoringPlotGroups.entrySet()) {
            String groupDisplayName = groupKeyEntry.getKey().getUpperName();

            String regex = "";
            for (DefaultMonitoringParameters defaultMonitoringParameters : groupKeyEntry.getValue()) {
                // not first / first time
                if (regex.length() != 0) {
                    regex += "|";
                }
                else {
                    regex += "^.*(";
                }
                regex += defaultMonitoringParameters.getId();
            }
            regex += ").*";

            firstLevelFilters.add(new TreeViewGroupRule(Rule.By.ID,groupDisplayName,groupDisplayName,regex));
        }

        // Root filter - will match all metrics
        return new TreeViewGroupRule(Rule.By.ID,rootId,rootName,".*",firstLevelFilters);
    }
}
