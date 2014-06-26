package com.griddynamics.jagger.dbapi.model.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides TreeGroupRule with predefined groups.
 * Requires format for group.
 */
public class LegendTreeViewGroupRuleProvider {

    public TreeViewGroupRule provide(String rootId,
                                     Collection<String> legendGroups, String idFormatRegex) {

        List<TreeViewGroupRule> firstLevelFilters = new ArrayList<TreeViewGroupRule>();

        for (String legendGroup: legendGroups) {

            String regex = String.format(idFormatRegex, legendGroup);
            firstLevelFilters.add(new TreeViewGroupRule(Rule.By.ID, legendGroup, legendGroup, regex));
        }

        // Root filter - will match all metrics
        return new TreeViewGroupRule(Rule.By.ID, rootId, rootId, ".*", firstLevelFilters);
    }
}