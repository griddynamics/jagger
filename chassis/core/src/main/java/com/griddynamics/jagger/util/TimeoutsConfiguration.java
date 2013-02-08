package com.griddynamics.jagger.util;

public class TimeoutsConfiguration {

    private static final TimeoutsConfiguration defaultTimeouts = new TimeoutsConfiguration(30000, 3600000, 30000, 10000, 300000);

    private final int workloadStartTimeout;
    private final int workloadStopTimeout;
    private final int workloadPollingTimeout;
    private final int calibrationTimeout;
    private final int calibrationStartTimeout;

    private TimeoutsConfiguration(int workloadStartTimeout, int workloadStopTimeout, int workloadPollingTimeout, int calibrationStartTimeout, int calibrationTimeout) {
        this.calibrationStartTimeout = calibrationStartTimeout;
        this.calibrationTimeout = calibrationTimeout;
        this.workloadPollingTimeout = workloadPollingTimeout;
        this.workloadStartTimeout = workloadStartTimeout;
        this.workloadStopTimeout = workloadStopTimeout;
    }

    public static TimeoutsConfiguration getDefaultTimeouts() {
        return defaultTimeouts;
    }

    public int getCalibrationStartTimeout() {
        return calibrationStartTimeout;
    }

    public int getCalibrationTimeout() {
        return calibrationTimeout;
    }

    public int getWorkloadPollingTimeout() {
        return workloadPollingTimeout;
    }

    public int getWorkloadStartTimeout() {
        return workloadStartTimeout;
    }

    public int getWorkloadStopTimeout() {
        return workloadStopTimeout;
    }
}
