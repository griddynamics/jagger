package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * @author asokol
 *         created 11/29/16
 */
public class UpWarnThresh {

    private final Double value;

    private UpWarnThresh(Double value) {
        this.value = value;
    }

    public static UpWarnThresh of(Double value) {
        return new UpWarnThresh(value);
    }

    public Double value() {
        return value;
    }

}
