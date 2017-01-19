package com.griddynamics.jagger.invoker.batch;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class ResponseBatch<R> {
    private List<R> responses;

    public ResponseBatch(List<R> responses) {
        this.responses = Objects.requireNonNull(responses);
    }

    public List<R> getResponses() {
        return newArrayList(responses);
    }

    public int size() {
        return responses.size();
    }
}
