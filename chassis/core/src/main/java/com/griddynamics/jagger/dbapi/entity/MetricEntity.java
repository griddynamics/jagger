package com.griddynamics.jagger.dbapi.entity;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricEntity {
    private String metricId;
    private String displayName;
    private Double summaryValue;

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getSummaryValue() {
        return summaryValue;
    }

    public void setSummaryValue(Double summaryValue) {
        this.summaryValue = summaryValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricEntity entity = (MetricEntity) o;

        if (displayName != null ? !displayName.equals(entity.displayName) : entity.displayName != null) return false;
        if (metricId != null ? !metricId.equals(entity.metricId) : entity.metricId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = metricId != null ? metricId.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        return result;
    }
}
