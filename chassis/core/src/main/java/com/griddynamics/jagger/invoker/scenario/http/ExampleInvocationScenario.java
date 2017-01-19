package com.griddynamics.jagger.invoker.scenario.http;

import com.griddynamics.jagger.invoker.scenario.InvocationScenario;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;

import java.net.URI;

public class ExampleInvocationScenario implements InvocationScenario<SpringBasedHttpClient> {

    private SpringBasedHttpClient executor;

    @Override
    public ExampleInvocationResult execute() {
        Long duration;
        Integer requestsExecuted = 0;
        Long totalRequestsSize = 0L;

        JHttpEndpoint endpoint = new JHttpEndpoint(URI.create("https://jagger.griddynamics.net:443"));
        JHttpQuery query1 = new JHttpQuery().get().responseBodyType(String.class).path("index.html");
        JHttpQuery query2 = new JHttpQuery().get().responseBodyType(String.class).path("screenshots.html");
        JHttpQuery query3 = new JHttpQuery().get().responseBodyType(String.class).path("download.html");

        long startTime = System.currentTimeMillis();

        JHttpResponse<String> response1 = executor.execute(endpoint, query1);
        requestsExecuted++;
        totalRequestsSize += response1.getBody().length();

        JHttpResponse<String> response2 = executor.execute(endpoint, query2);
        requestsExecuted++;
        totalRequestsSize += response2.getBody().length();

        JHttpResponse<String> response3 = executor.execute(endpoint, query3);
        requestsExecuted++;
        totalRequestsSize += response3.getBody().length();

        duration = System.currentTimeMillis() - startTime;

        return new ExampleInvocationResult(duration, requestsExecuted, totalRequestsSize);
    }

    @Override
    public SpringBasedHttpClient getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(SpringBasedHttpClient executor) {
        this.executor = executor;
    }
}
