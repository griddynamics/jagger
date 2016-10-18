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
    private JTest jTest;

    @Before
    public void setUp() throws Exception {
        jTest = JTest.builder()
                .withId(id)
                .build();
    }

    @Test
    public void builder() throws Exception {
        Assert.assertThat(jTest.getId(), is(id));
    }

}