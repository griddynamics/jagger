package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * @author asokol
 *         created 11/29/16
 */
public class LowerWarningThreshold {
    private final Double value;

    private LowerWarningThreshold(Double value) {
        this.value = value;
    }

    public static LowerWarningThreshold of(Double value) {
        return new LowerWarningThreshold(value);
    }

    public Double value() {
        return value;
    }

}
