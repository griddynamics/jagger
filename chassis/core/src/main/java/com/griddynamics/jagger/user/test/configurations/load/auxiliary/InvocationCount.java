package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

/**
 * The number of invocation for {@link com.griddynamics.jagger.user.test.configurations.load.JLoadProfile}.
 */
public class InvocationCount {
    private final int invocationCount;

    private InvocationCount(int invocationCount) {
        if (invocationCount <= 0) {
            throw new IllegalArgumentException(String.format("Number of invocation must be > 0. Provided value is %s", invocationCount));
        }
        this.invocationCount = invocationCount;
    }

    public int value() {
        return invocationCount;
    }

    public static InvocationCount of(int invocationCount) {
        return new InvocationCount(invocationCount);
    }
}
