package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * @author asokol
 *         created 11/29/16
 */
public class LowErrThres {
    private final Double value;

    private LowErrThres(Double value) {
        this.value = value;
    }

    public static LowErrThres of(Double value) {
        return new LowErrThres(value);
    }

    public Double value() {
        return value;
    }

}
