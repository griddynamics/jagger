package com.griddynamics.jagger.util;

public class TimeoutsConfiguration {

    private static final TimeoutsConfiguration defaultTimeouts = new TimeoutsConfiguration( new Timeout (30000,""),
                                                                                            new Timeout (3600000,""),
                                                                                            new Timeout (30000,""),
                                                                                            new Timeout (10000,""),
                                                                                            new Timeout (300000,""));

    private final Timeout workloadStartTimeout;
    private final Timeout workloadStopTimeout;
    private final Timeout workloadPollingTimeout;
    private final Timeout calibrationTimeout;
    private final Timeout calibrationStartTimeout;

    private TimeoutsConfiguration(Timeout workloadStartTimeout, Timeout workloadStopTimeout, Timeout workloadPollingTimeout, Timeout calibrationStartTimeout, Timeout calibrationTimeout) {
        this.calibrationStartTimeout = calibrationStartTimeout;
        this.calibrationTimeout = calibrationTimeout;
        this.workloadPollingTimeout = workloadPollingTimeout;
        this.workloadStartTimeout = workloadStartTimeout;
        this.workloadStopTimeout = workloadStopTimeout;
    }

    public static TimeoutsConfiguration getDefaultTimeouts() {
        return defaultTimeouts;
    }

    public Timeout getCalibrationStartTimeout() {
        return calibrationStartTimeout;
    }

    public Timeout getCalibrationTimeout() {
        return calibrationTimeout;
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
