package com.griddynamics.jagger.util;

public class TimeoutsConfiguration {

    private static final TimeoutsConfiguration defaultTimeouts = new TimeoutsConfiguration( new Timeout (30000,""),
                                                                                            new Timeout (3600000,""),
                                                                                            new Timeout (30000,""));

    private final Timeout workloadStartTimeout;
    private final Timeout workloadStopTimeout;
    private final Timeout workloadPollingTimeout;

    private TimeoutsConfiguration(Timeout workloadStartTimeout, Timeout workloadStopTimeout, Timeout workloadPollingTimeout) {
        this.workloadPollingTimeout = workloadPollingTimeout;
        this.workloadStartTimeout = workloadStartTimeout;
        this.workloadStopTimeout = workloadStopTimeout;
    }

    public static TimeoutsConfiguration getDefaultTimeouts() {
        return defaultTimeouts;
    }

    public Timeout getWorkloadPollingTimeout() {
        return workloadPollingTimeout;
    }

    public Timeout getWorkloadStartTimeout() {
        return workloadStartTimeout;
    }

    public Timeout getWorkloadStopTimeout() {
        return workloadStopTimeout;
    }
}
