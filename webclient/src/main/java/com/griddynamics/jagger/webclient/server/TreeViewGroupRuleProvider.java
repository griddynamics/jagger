package com.griddynamics.jagger.webclient.server;

import java.util.ArrayList;
import java.util.List;

public class TreeViewGroupRuleProvider {

    public static TreeViewGroupRule provide (String rootId, String rootName) {

//        Left here as examples
//        List<TreeViewGroupRule> MySQL_SecondLevelFilters = new ArrayList<TreeViewGroupRule>();
//        MySQL_SecondLevelFilters.add(new TreeViewGroupRule("innodb", "InnoDB", "^MySQL InnoDB .*"));
//        MySQL_SecondLevelFilters.add(new TreeViewGroupRule("bytes", "Bytes", "^MySQL Bytes .*"));
//        MySQL_SecondLevelFilters.add(new TreeViewGroupRule("quest", "Questions", "^MySQL Questions .*"));
//        TreeViewGroupRule MySQL_FirstLevelFilter = new TreeViewGroupRule("mysql","MySQL","^MySQL .*",MySQL_SecondLevelFilters);
//        TreeViewGroupRule JMX_FirstLevelFilter = new TreeViewGroupRule("jmx","JMX","^JMX .*");

        // Filter for Jagger main metrics
        TreeViewGroupRule MainParams_FirstLevelFilter = new TreeViewGroupRule("main","Main parameters",
                "(^Throughput, tps$|^Throughput$|^Latency, sec$|^Latency$" +
                "|^Iterations, samples$|^Success rate$|^Duration, sec$|^Latency\\s\\S+\\s%$|^Time Latency Percentile$)");

        List<TreeViewGroupRule> FirstLevelFilters = new ArrayList<TreeViewGroupRule>();
        FirstLevelFilters.add(MainParams_FirstLevelFilter);
        // Root filter - will match all metrics
        return new TreeViewGroupRule(rootId,rootName,".*",FirstLevelFilters);
    }
}
