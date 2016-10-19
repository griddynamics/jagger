package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JRpsLoad;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author asokol
 *         created 10/19/16
 */
public class JTestConfigurationTest {

    JLoad load;
    JTermination termination;
    JTestDescription description;
    JTest test;
    JTestGroup testGroup;
    JTestConfiguration testConfiguration;

    @Before
    public void setUp() throws Exception {
        load = JRpsLoad.builder()
                .warmUpTimeInSeconds(42L)
                .build();

        termination = JTerminationIterations.builder()
                .iterationCount(123)
                .maxDurationInSeconds(42)
                .build();

        description = JTestDescription.builder()
                .description("What a neat description")
                .endpoints(Arrays.asList("first", "second"))
                .queries(Arrays.asList("first", "second"))
                .name("name")
                .version("version")
                .build();

        test = JTest.builder()
                .testDescription(description)
                .load(load)
                .termination(termination)
                .id("ID")
                .build();

        testGroup = JTestGroup.builder()
                .tests(Collections.singletonList(test))
                .build();

        testConfiguration = JTestConfiguration.builder()
                .testGroups(Collections.singletonList(testGroup))
                .build();


    }

    @Test
    public void builder() throws Exception {

        System.out.println(testConfiguration);
    }

}