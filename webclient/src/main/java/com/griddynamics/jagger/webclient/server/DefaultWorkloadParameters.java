package com.griddynamics.jagger.webclient.server;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/4/12
 */
public enum DefaultWorkloadParameters {
    LATENCY("Latency, sec", false),
    LATENCY_STD_DEV("Latency std dev, sec", false),
    THROUGHPUT("Throughput, tps", false),
    TIME_LATENCY_PERCENTILE_40("40.0", true),
    TIME_LATENCY_PERCENTILE_50("50.0", true),
    TIME_LATENCY_PERCENTILE_60("60.0", true),
    TIME_LATENCY_PERCENTILE_70("70.0", true),
    TIME_LATENCY_PERCENTILE_80("80.0", true),
    TIME_LATENCY_PERCENTILE_90("90.0", true),
    TIME_LATENCY_PERCENTILE_95("95.0", true),
    TIME_LATENCY_PERCENTILE_99("99.0", true);

    private final String description;
    private final boolean isCumulativeCounter;

    private DefaultWorkloadParameters(String description, boolean cumulativeCounter) {
        this.description = description;
        isCumulativeCounter = cumulativeCounter;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCumulativeCounter() {
        return isCumulativeCounter;
    }

    public static DefaultWorkloadParameters fromDescription(String description) {
        for (DefaultWorkloadParameters defaultWorkloadParameter : values()) {
            if (defaultWorkloadParameter.getDescription().equalsIgnoreCase(description)) {
                return defaultWorkloadParameter;
            }
        }
        return null;
    }
}
