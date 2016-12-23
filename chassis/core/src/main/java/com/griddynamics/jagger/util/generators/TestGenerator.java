package com.griddynamics.jagger.util.generators;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener;
import com.griddynamics.jagger.engine.e1.collector.limits.LimitSetConfig;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import static com.griddynamics.jagger.util.generators.TerminationGenerator.generateTermination;
import static com.griddynamics.jagger.util.generators.TestDefinitionGenerator.generatePrototype;
import static com.griddynamics.jagger.util.generators.WorkloadGenerator.generateLoad;

/**
 * @author asokol
 *         created 11/6/16
 *         Generates {@link WorkloadTask} entity from user-defined {@link JLoadTest} entity.
 */
class TestGenerator {
    static WorkloadTask generateFromTest(JLoadTest jLoadTest,
                                         BaselineSessionProvider baselineSessionProvider,
                                         LimitSetConfig limitSetConfig, String testGroupName) {
        WorkloadTask task = generatePrototype(jLoadTest.getTestDescription());
        task.setName(getName(jLoadTest.getId(), testGroupName));
        task.setParentTaskId(testGroupName);
        task.setVersion("0");
    
        List<Provider<TestListener>> testListeners = Lists.newArrayList(jLoadTest.getListeners());
        testListeners.add(new CollectThreadsTestListener());
        task.setTestListeners(testListeners);
    
        task.setTerminateStrategyConfiguration(generateTermination(jLoadTest.getTermination()));
    
        task.setClockConfiguration(generateLoad(jLoadTest.getLoad()));
        
        task.setLimits(LimitGenerator.generate(jLoadTest.getLimits(), baselineSessionProvider, limitSetConfig));

        return task;
    }

    public static String getName(String id, String testGroupName) {
        if (StringUtils.isBlank(id)){
            return testGroupName;
        }
        return String.format("%s [%s]", testGroupName, id);
    }
}
