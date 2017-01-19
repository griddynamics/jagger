package com.griddynamics.jagger.invoker.scenario.http;

import com.griddynamics.jagger.invoker.scenario.InvocationResult;

public class ExampleInvocationResult implements InvocationResult {
    private Long duration;
    private Integer requestsExecuted;
    private Long totalRequestsSize;

    public ExampleInvocationResult(Long duration, Integer requestsExecuted, Long totalRequestsSize) {
        this.duration = duration;
        this.requestsExecuted = requestsExecuted;
        this.totalRequestsSize = totalRequestsSize;
    }

    public Long getDuration() {
        return duration;
    }

    public Integer getRequestsExecuted() {
        return requestsExecuted;
    }

    public Long getTotalRequestsSize() {
        return totalRequestsSize;
    }
}
