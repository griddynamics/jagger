package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

/**
 * The number of requests per second Jagger shall perform.
 *
 */
public final class RequestsPerSecond {
    private final long requestsPerSecond;

    RequestsPerSecond(long requestsPerSecond) {
        if (requestsPerSecond <= 0) {
            throw new IllegalArgumentException(
                    String.format("Requests per second must be > 0. Provided value is %s", requestsPerSecond));
        }
        this.requestsPerSecond = requestsPerSecond;
    }

    public long value() {
        return requestsPerSecond;
    }

    public static RequestsPerSecond of(long requestsPerSecond) {
        return new RequestsPerSecond(requestsPerSecond);
    }
}
