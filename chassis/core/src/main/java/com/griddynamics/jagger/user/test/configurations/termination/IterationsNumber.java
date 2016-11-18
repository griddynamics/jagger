package com.griddynamics.jagger.user.test.configurations.termination;

/**
 * The target number of requests to the system under test.
 * I.e. at least that number of requests will be performed.
 */
public final class IterationsNumber {
    private final long iterationsNumber;

    private IterationsNumber(long iterationsNumber) {
        if (iterationsNumber <= 0) {
            throw new IllegalArgumentException(
                    String.format("Iterations number must be > 0. Provided value is %s", iterationsNumber));
        }
        this.iterationsNumber = iterationsNumber;
    }
    
    public static IterationsNumber of(long iterationsNumber) {
        return new IterationsNumber(iterationsNumber);
    }
    
    public long value() {
        return iterationsNumber;
    }
}
