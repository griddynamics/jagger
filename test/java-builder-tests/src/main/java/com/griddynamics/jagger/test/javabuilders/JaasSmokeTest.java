package com.griddynamics.jagger.test.javabuilders;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;

import java.util.Collections;


public class JaasSmokeTest {
    public JLoadScenario getHttpScenario(){

        JLoadTest getSessionsTest = getTest("get sessions", new JHttpQuery().get().path("/jaas/db/sessions"));
        JLoadTest getExecutionsTest = getTest("get execution", new JHttpQuery().get().path("/jaas/executions"));
        JLoadTest postExecutionsTest = getTest("POST", new JHttpQuery<String>()
                .post().body("{\"envId\": \"1\",\"loadScenarioId\": \"sid\"}")
                .header("Content-Type", "application/json")
                .path("/jaas/executions"));

// TODO rework with custom invoker when JFG-1018 will be fixed
// JLoadTest deleteProjectTest = getTest("DELETE", new JHttpQuery().delete().path("/jaas/executions/1"));

        JParallelTestsGroup testsGroup1 = JParallelTestsGroup.builder(Id.of("Group 1"), getSessionsTest, getExecutionsTest).build();
        JParallelTestsGroup testsGroup2 = JParallelTestsGroup.builder(Id.of("Group 2"), postExecutionsTest).build();
        JParallelTestsGroup testsGroup3 = JParallelTestsGroup.builder(Id.of("Group 3"), getExecutionsTest).build();

        return JLoadScenario.builder(Id.of("JaasSmokeTest"), testsGroup1, testsGroup2, testsGroup3).build();
    }


    private Iterable<JHttpEndpoint> getEndpoints() {
        // TODO oskliarov: when JFG-972 will be done use properties
        return Collections.singletonList(new JHttpEndpoint("http://localhost:8088"));
    }

    private JLoadTest getTest(String id, JHttpQuery q){
        JTestDefinition testDefinition = JTestDefinition.builder(Id.of(id+" definition"), getEndpoints())
                .withQueryProvider(Collections.singletonList(q))
                .build();
        return JLoadTest.builder(Id.of(id+" test"), testDefinition, getLoadProfile(), getTerminationCriteria()).build();
    }

    private JTerminationCriteria getTerminationCriteria() {
        return JTerminationCriteriaIterations.of(IterationsNumber.of(1), MaxDurationInSeconds.of(10));
    }

    private JLoadProfile getLoadProfile() {
        return JLoadProfileRps.builder(RequestsPerSecond.of(1)).withMaxLoadThreads(2).withWarmUpTimeInSeconds(1).build();
    }

}
