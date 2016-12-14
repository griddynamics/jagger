package com.griddynamics.jagger.engine.e1.services.data.service;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;

/** Class is a model of some metric
 *
 * @authors
 * Gribov Kirill, Latnikov Dmitry
 *
 * @details
 * MetricEntity is a model of metric. It can present some standard metrics (latency, throughput), @n
 * monitoring metrics (CPU utilization, Heap memory usage) or custom metrics. @n
 * This model is used to get test results from database with use of @ref DataService @n
 * Model contains following information about metric: @n
 * @li metric id
 * @li metric display name
 * @li is summary and detailed info available in DB for this metric
 *
 */
public class MetricEntity {
    /** Internal metric model */
    private MetricNameDto metricNameDto;

    /** True if summary value available for this metric */
    private boolean summaryAvailable = false;

    /** True if detailed results for plot (values vs time) available for this metric */
    private boolean plotAvailable = false;

    public void setMetricNameDto(MetricNameDto metricNameDto) {
        this.metricNameDto = metricNameDto;
    }

    /** Get internal metric model. Internal model is point to change in future. No compatibility guaranteed */
    public MetricNameDto getMetricNameDto() {
        return metricNameDto;
    }

    /** Get metric id */
    public String getMetricId() {
        return metricNameDto.getMetricName();
    }

    /** Get metric display name - label displayed in reports */
    public String getDisplayName() {
        return metricNameDto.getMetricDisplayName();
    }

    /** Get flag: is summary value available for this metric */
    public boolean isSummaryAvailable() {
        return summaryAvailable;
    }

    public void setSummaryAvailable(boolean summaryAvailable) {
        this.summaryAvailable = summaryAvailable;
    }

    /** Get flag: is detailed results for plot (values vs time) available for this metric */
    public boolean isPlotAvailable() {
        return plotAvailable;
    }

    public void setPlotAvailable(boolean plotAvailable) {
        this.plotAvailable = plotAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricEntity that = (MetricEntity) o;

        if (plotAvailable != that.plotAvailable) return false;
        if (summaryAvailable != that.summaryAvailable) return false;
        if (!metricNameDto.equals(that.metricNameDto)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = metricNameDto.hashCode();
        result = 31 * result + (summaryAvailable ? 1 : 0);
        result = 31 * result + (plotAvailable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetricEntity{" +
                "metricNameDto=" + metricNameDto +
                ", summaryAvailable=" + summaryAvailable +
                ", plotAvailable=" + plotAvailable +
                '}';
    }
}
