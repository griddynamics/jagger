package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Component
public class TreeViewGroupRuleProvider {

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    @Resource
    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public TreeViewGroupRule provide (String rootId, String rootName) {

        List<TreeViewGroupRule> firstLevelFilters = new ArrayList<TreeViewGroupRule>();

        String filterRegex = "(" +
        "^" + StandardMetricsNamesUtil.THROUGHPUT_TPS + "$|" +
        "^" + StandardMetricsNamesUtil.THROUGHPUT + "$|" +
        "^" + StandardMetricsNamesUtil.LATENCY_SEC + "$|" +
        "^" + StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC + "$|" +
        "^" + StandardMetricsNamesUtil.LATENCY + "$|" +
        "^" + StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX + "$|" +
        "^" + StandardMetricsNamesUtil.ITERATIONS_SAMPLES + "$|" +
        "^" + StandardMetricsNamesUtil.SUCCESS_RATE + "$|" +
        "^" + StandardMetricsNamesUtil.DURATION_SEC + "$|" +
        "^" + StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE + "$|" +
        "^" + StandardMetricsNamesUtil.FAIL_COUNT + "$|" +
        ")";

        // Filter for Jagger main metrics. Space in display name will help to keep main parameters in the
        // top of alphabetic sorting
        TreeViewGroupRule mainParams_FirstLevelFilter = new TreeViewGroupRule(Rule.By.DISPLAY_NAME,"main"," Main parameters",filterRegex);
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
