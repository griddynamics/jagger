package com.griddynamics.jagger.user.test.configurations;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author asokol
 *         created 10/18/16
 */
public class JTestGroupDescriptionTest {

    private JTestGroup jTestGroup;

    @Before
    public void setUp() throws Exception {
        jTestGroup = JTestGroup.builder()
                .withTests(null)
                .build();
    }

    @Test
    public void builder() throws Exception {

        Assert.assertNotNull(jTestGroup);
    }

}