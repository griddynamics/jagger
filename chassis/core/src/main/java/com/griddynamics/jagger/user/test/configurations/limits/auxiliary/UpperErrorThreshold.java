package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * @author asokol
 *         created 11/29/16
 */
public class UpperErrorThreshold {
    private final Double value;

    private UpperErrorThreshold(Double value) {
        this.value = value;
    }

    public static UpperErrorThreshold of(Double value) {
        return new UpperErrorThreshold(value);
    }

    public Double value() {
        return value;
    }
}
