package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * @author asokol
 *         created 11/29/16
 */
public class LowWarnThresh {
    private final Double value;

    private LowWarnThresh(Double value) {
        this.value = value;
    }

    public static LowWarnThresh of(Double value) {
        return new LowWarnThresh(value);
    }

    public Double value() {
        return value;
    }

}
