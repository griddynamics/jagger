package com.griddynamics.jagger.webclient.server;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public enum Plot {
    LATENCY("Latency", "sec"),
    LATENCY_STD_DEV("Latency std deviation", "sec"),
    THROUGHPUT("Throughput", "tps"),
    TIME_LATENCY_PERCENTILE("Time Latency Percentile", "sec"),
    TIME_LATENCY_PERCENTILE_40("40", "sec"),
    TIME_LATENCY_PERCENTILE_50("50", "sec"),
    TIME_LATENCY_PERCENTILE_60("60", "sec"),
    TIME_LATENCY_PERCENTILE_70("70", "sec"),
    TIME_LATENCY_PERCENTILE_80("80", "sec"),
    TIME_LATENCY_PERCENTILE_90("90", "sec"),
    TIME_LATENCY_PERCENTILE_95("95", "sec"),
    TIME_LATENCY_PERCENTILE_99("99", "sec");

    private final String text;
    private final String unitOfMeasurement;

    private Plot(String text, String unitOfMeasurement) {
        this.text = text;
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public String getText() {
        return text;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public static Plot fromText(String text) {
        for (Plot plot : Plot.values()) {
            if (plot.getText().equalsIgnoreCase(text)) {
                return plot;
            }
        }
        return null;
    }
}
