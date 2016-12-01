package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * Upper warning criterion for a performance test.
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

    @Override
    public String toString() {
        return "UpWarnThresh: " + value;
    }
}
