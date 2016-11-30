package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * @author asokol
 *         created 11/29/16
 */
public class UpErrThresh {
    private final Double value;

    private UpErrThresh(Double value) {
        this.value = value;
    }

    public static UpErrThresh of(Double value) {
        return new UpErrThresh(value);
    }

    public Double value() {
        return value;
    }
}
