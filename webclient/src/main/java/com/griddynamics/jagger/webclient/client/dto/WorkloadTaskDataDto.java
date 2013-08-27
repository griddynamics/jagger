package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 18.03.13
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
public class WorkloadTaskDataDto implements Serializable {

    private String name;
    private String version;
    private String comment;
    private String sessionId;
    private String taskId;
    private Integer number;
    private Integer samples;
    private String clock;
    private Integer clockValue;
    private String termination;
    private Integer kernels;
    private BigDecimal totalDuration;
    private String duration;
    private BigDecimal throughput;
    private Integer failuresCount;
    private BigDecimal successRate;
    private BigDecimal avgLatency;
    private BigDecimal stdDevLatency;
    private List<String> latency;
    private Map<String, String> customMetrics;

    public Map<String, String> getCustomMetrics() {
        return customMetrics;
    }

    public void setCustomMetrics(Map<String, String> customMetrics) {
        this.customMetrics = customMetrics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getSamples() {
        return samples;
    }

    public void setSamples(Integer samples) {
        this.samples = samples;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public Integer getClockValue() {
        return clockValue;
    }

    public void setClockValue(Integer clockValue) {
        this.clockValue = clockValue;
    }

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public Integer getKernels() {
        return kernels;
    }

    public void setKernels(Integer kernels) {
        this.kernels = kernels;
    }

    public BigDecimal getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(BigDecimal totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public BigDecimal getThroughput() {
        return throughput;
    }

    public void setThroughput(BigDecimal throughput) {
        this.throughput = throughput;
    }

    public Integer getFailuresCount() {
        return failuresCount;
    }

    public void setFailuresCount(Integer failuresCount) {
        this.failuresCount = failuresCount;
    }

    public BigDecimal getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(BigDecimal successRate) {
        this.successRate = successRate;
    }

    public BigDecimal getAvgLatency() {
        return avgLatency;
    }

    public void setAvgLatency(BigDecimal avgLatency) {
        this.avgLatency = avgLatency;
    }

    public BigDecimal getStdDevLatency() {
        return stdDevLatency;
    }

    public void setStdDevLatency(BigDecimal stdDevLatency) {
        this.stdDevLatency = stdDevLatency;
    }

    public List<String> getLatency() {
        return latency;
    }

    public void setLatency(List<String> latency) {
        this.latency = latency;
    }
}
