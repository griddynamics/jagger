package com.griddynamics.jagger.util.generators;

import com.griddynamics.jagger.engine.e1.collector.limits.Limit;
import com.griddynamics.jagger.engine.e1.collector.limits.LimitSet;
import com.griddynamics.jagger.engine.e1.collector.limits.LimitSetConfig;
import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author asokol
 *         created 11/29/16
 */
public class LimitGenerator {
    private static int count;

    public static LimitSet generate(List<JLimit> limits, BaselineSessionProvider baselineSessionProvider, LimitSetConfig limitSetConfig) {
        LimitSet limitSet = new LimitSet();
        limitSet.setId(count++ + "--limit_set");
        limitSet.setBaselineSessionProvider(baselineSessionProvider);
        limitSet.setLimitSetConfig(limitSetConfig);

        return limitSet;
    }


    private static List<Limit> generateListOfLimits(List<JLimit> limits) {
        return limits.stream()
                .map(LimitGenerator::generateLimit)
                .collect(Collectors.toList());
    }

    private static Limit generateLimit(JLimit jLimit) {
        Limit limit = new Limit();
        limit.setMetricName(jLimit.getMetricName());
        limit.setLimitDescription(jLimit.getLimitDescription());
        limit.setRefValue(jLimit.getRefValue());

        limit.setLowerErrorThreshold(jLimit.getLowerErrorThreshold());
        limit.setLowerWarningThreshold(jLimit.getLowerWarningThreshold());
        limit.setUpperErrorThreshold(jLimit.getUpperErrorThreshold());
        limit.setUpperWarningThreshold(jLimit.getUpperWarningThreshold());

        return limit;
    }
}
