package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * Upper acceptance criterion for a performance test.
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

    @Override
    public String toString() {
        return "UpErrThresh: " + value;
    }
}
