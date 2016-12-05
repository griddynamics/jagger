package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * Lower warning criterion for a performance test.
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

    @Override
    public String toString() {
        return "LowWarnThresh: " + value;
    }
}
