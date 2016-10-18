package com.griddynamics.jagger.user.test.configurations;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * @author asokol
 *         created 10/18/16
 */
public class JTestTest {

    private String id = "Some ID";
    private JTestDescription jTestDescription;
    private long requestsPerSecond = 100L;
    private long maxLoadThreads = 350L;
    private long warmUpTimeInSeconds = 42L;
    private JTest jTest;

    @Before
    public void setUp() throws Exception {

        jTestDescription = JTestDescription.builder()
                .withName("Name")
                .withVersion("Version")
                .withDescription("What a neat description")
                .build();

        jTest = JTest.builder()
                .withId(id)
                .withJTestDescription(jTestDescription)
                .withLoadRps(requestsPerSecond, maxLoadThreads, warmUpTimeInSeconds)
                .withTerminationBackground()
                .build();
    }

    @Test
    public void builder() throws Exception {
        Assert.assertThat(jTest.getId(), is(id));
        Assert.assertThat(jTest.getTerminationType(), is(JTest.TerminationType.TERMINATION_BACKGROUND));
        Assert.assertThat(jTest.getRequestsPerSecond(), is(requestsPerSecond));
        Assert.assertThat(jTest.getWarmUpTimeInSeconds(), is(warmUpTimeInSeconds));
        Assert.assertThat(jTest.getMaxLoadThreads(), is(maxLoadThreads));
    }

}