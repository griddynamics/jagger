package com.griddynamics.jagger.jaas.storage.model;

import org.junit.Assert;

public class TestEnvCookiesTest {
    @org.junit.Test
    public void testExpiresConversion() throws Exception {
        long expected = (System.currentTimeMillis() / 1000) * 1000; // drop nanoseconds
        String formattedMillis = TestEnvUtils.convertToExpiresValue(expected);
        System.out.println(formattedMillis);
        long actual = TestEnvUtils.convertFromExpiresValue(formattedMillis);
        Assert.assertEquals(expected, actual);
    }
}