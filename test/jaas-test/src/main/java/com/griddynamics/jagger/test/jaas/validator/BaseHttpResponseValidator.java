package com.griddynamics.jagger.test.jaas.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.invoker.http.HttpResponse;
import com.jayway.jsonpath.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.jayway.jsonpath.JsonPath.parse;

public abstract class BaseHttpResponseValidator<Q, E> extends ResponseValidator<Q, E, HttpResponse> {
    private static final Logger log = LoggerFactory.getLogger(BaseHttpResponseValidator.class);

    public BaseHttpResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "BaseHttpResponseValidator";
    }

    @Override
    public abstract boolean validate(Q query, E endpoint, HttpResponse result, long duration);

    protected void logResponseAsFailed(E endpoint, HttpResponse response){
        log.warn(String.format
                ("------> Failed response:\nEndpoint=%s \nStatus=%d \nBody=%s ",
                        endpoint.toString(), response.getStatusCode(), response.getBody()));
    }

    protected List<Map<String, Object>> getJsonEntriesFiltered(String jsonContent, String jsonPath, Filter filter) {
        return parse(jsonContent).read(jsonPath, filter);
    }
}