package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * @author asokol
 *         created 11/29/16
 */
public class LowerErrorThreshold {
    private final Double value;

    private LowerErrorThreshold(Double value) {
        this.value = value;
    }

    public static LowerErrorThreshold of(Double value) {
        return new LowerErrorThreshold(value);
    }

    public Double value() {
        return value;
    }

}
