package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * Lower acceptance criterion for a performance test.
 */
public class LowErrThresh {
    private final Double value;

    private LowErrThresh(Double value) {
        this.value = value;
    }

    public static LowErrThresh of(Double value) {
        return new LowErrThresh(value);
    }

    public Double value() {
        return value;
    }


    @Override
    public String toString() {
        return "LowErrThresh: " + value;
    }
}
