package com.griddynamics.jagger.dbapi.dto;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class MetricNameDto extends MetricName {

    // Describes origin of this metric - location in DB where data for this metric ca be taken from
    // Will be used when fetching data from DB to select correct provider of data
    private Origin origin = Origin.UNKNOWN;
    private TaskDataDto test;

    public MetricNameDto(){}

    public MetricNameDto(TaskDataDto test, String metricName) {
        super(metricName);
        this.test = test;
    }

    public MetricNameDto(TaskDataDto test, String metricName, String metricDisplayName) {
        super(metricName,metricDisplayName);
        this.test = test;
    }

    public MetricNameDto(TaskDataDto test, String metricName, String metricDisplayName, Origin origin) {
        super(metricName,metricDisplayName);
        this.test = test;
        this.origin = origin;
    }

    public Set<Long> getTaskIds() {
        return test.getIds();
    }

    public TaskDataDto getTest() {
        return test;
    }

    public void setTest(TaskDataDto test) {
        this.test = test;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return "MetricNameDto{" +
                (test != null ? "taskIds=" + test.getIds() : "") +
                ", metricName='" + metricName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricNameDto that = (MetricNameDto) o;

        if (metricName != null ? !metricName.equals(that.metricName) : that.metricName != null) return false;
        if (test != null ? !test.equals(that.test) : that.test != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = test != null ? test.hashCode() : 0;
        result = 31 * result + (metricName != null ? metricName.hashCode() : 0);
        return result;
    }

    public static enum Origin {
        UNKNOWN,                      /* default value - will produce errors during fetching */
        METRIC,                       /* custom metric */
        TEST_GROUP_METRIC,            /* custom test-group metric */
        LATENCY,
        THROUGHPUT,
        LATENCY_PERCENTILE,
        DURATION,
        STANDARD_METRICS,             /* success rate, iterations, etc */
        MONITORING,                    /* monitoring parameters saved in separate DB table */
        VALIDATOR
    }
}
