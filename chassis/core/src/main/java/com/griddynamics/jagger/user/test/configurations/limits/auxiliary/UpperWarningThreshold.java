package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * @author asokol
 *         created 11/29/16
 */
public class UpperWarningThreshold {

    private final Double value;

    private UpperWarningThreshold(Double value) {
        this.value = value;
    }

    public static UpperWarningThreshold of(Double value) {
        return new UpperWarningThreshold(value);
    }

    public Double value() {
        return value;
    }

}
