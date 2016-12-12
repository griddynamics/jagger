package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

/**
 * Enum for standard metrics.
 */
public enum MetricName {

    SUCCESS_RATE_OK,
    SUCCESS_RATE_FAILS,
    THROUGHPUT,
    VIRTUAL_USERS,
    STD_DEV_LATENCY,
    DURATION,
    AVG_LATENCY;


    /**
     * Convert {@link MetricName} to String.
     *
     * @return String value of current metric name.
     */
    public String transformToString() {
        String name = null;
        switch (this) {
            case SUCCESS_RATE_OK:
                name = StandardMetricsNamesUtil.SUCCESS_RATE_OK_ID;
                break;
            case SUCCESS_RATE_FAILS:
                name = StandardMetricsNamesUtil.SUCCESS_RATE_FAILED_ID;
                break;
            case THROUGHPUT:
                name = StandardMetricsNamesUtil.THROUGHPUT_ID;
                break;
            case VIRTUAL_USERS:
                name = StandardMetricsNamesUtil.VIRTUAL_USERS_ID;
                break;
            case STD_DEV_LATENCY:
                name = StandardMetricsNamesUtil.LATENCY_STD_DEV_ID;
                break;
            case DURATION:
                name = StandardMetricsNamesUtil.DURATION_ID;
                break;
            case AVG_LATENCY:
                name = StandardMetricsNamesUtil.LATENCY_ID;
                break;
            default:
                break;

        }
        return name;
    }

}
