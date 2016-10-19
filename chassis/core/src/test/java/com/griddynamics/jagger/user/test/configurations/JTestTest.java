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
                .build();
    }

    @Test
    public void builder() throws Exception {
        Assert.assertThat(jTest.getId(), is(id));
    }

}