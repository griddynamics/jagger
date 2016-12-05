package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

/**
 * The value for comparison with some metric for a performance test.
 */
public class RefValue {

    private final Double value;

    private RefValue(Double value) {
        this.value = value;
    }

    public static RefValue of(Double value) {
        return new RefValue(value);
    }

    public Double value() {
        return value;
    }

    @Override
    public String toString() {
        return "RefValue: " + value;
    }
}
