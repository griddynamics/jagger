package com.griddynamics.jagger;

import static java.util.Collections.singletonList;

import com.griddynamics.jagger.engine.e1.collector.NotNullResponseValidator;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.aux.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileRps;
import com.griddynamics.jagger.user.test.configurations.load.aux.MaxLoadThreads;
import com.griddynamics.jagger.user.test.configurations.load.aux.RequestsPerSecond;
import com.griddynamics.jagger.user.test.configurations.load.aux.WarmUpTimeInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.aux.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.aux.MaxDurationInSeconds;

// begin: following section is used for docu generation - Load test scenario configuration
public class ExampleSimpleJLoadScenarioProvider {

    public static JLoadScenario getExampleJaggerLoadScenario() {

        JTestDefinition jTestDefinition = JTestDefinition.builder(Id.of("td_example"), new ExampleEndpointsProvider()).build();

        JLoadProfile jLoadProfileRps = JLoadProfileRps.of(RequestsPerSecond.of(10), MaxLoadThreads.of(10), WarmUpTimeInSeconds.of(10));
        
        JTerminationCriteria jTerminationCriteria = JTerminationCriteriaIterations.of(IterationsNumber.of(500), MaxDurationInSeconds.of(30));
        
        JLoadTest jLoadTest = JLoadTest.builder(Id.of("lt_example"), jTestDefinition, jLoadProfileRps, jTerminationCriteria).build();
        
        JParallelTestsGroup jParallelTestsGroup = JParallelTestsGroup.builder(Id.of("ptg_example"), jLoadTest).build();
        
        // To launch your load scenario, set 'jagger.load.scenario.id.to.execute' property's value equal to the load scenario id
        // You can do it via system properties or in the 'environment.properties' file
        return JLoadScenario.builder(Id.of("ls_example"), jParallelTestsGroup).build();
    }
}
// end: following section is used for docu generation - Load test scenario configuration

