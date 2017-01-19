package com.griddynamics.jagger.invoker.batch.http;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.batch.EndpointBatch;
import com.griddynamics.jagger.invoker.batch.QueryBatch;
import com.griddynamics.jagger.invoker.batch.ResponseBatch;
import com.griddynamics.jagger.invoker.v2.JHttpClient;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;


/**
 * Default batch HTTP-invoker that invokes services of SuT via http(s) protocol. <p>
 * By default as HTTP-client {@link SpringBasedHttpClient} is used here, but it can be updated with {@link DefaultBatchHttpInvoker#DefaultBatchHttpInvoker}
 * constructor.
 *
 * @author Anton Antonenko
 * @see AbstractBatchHttpInvoker
 * @since 2.0.1
 *
 * @ingroup Main_Http_group
 */
@SuppressWarnings("unused")
public class DefaultBatchHttpInvoker extends AbstractBatchHttpInvoker<JHttpClient> {

    public DefaultBatchHttpInvoker() {
        super(new SpringBasedHttpClient());
    }

    public DefaultBatchHttpInvoker(JHttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public ResponseBatch<JHttpResponse> invoke(QueryBatch<JHttpQuery> queryBatch, EndpointBatch<JHttpEndpoint> endpointBatch) throws InvocationException {
        Preconditions.checkNotNull(endpointBatch, "JHttpEndpoint is null!");
        if (queryBatch != null) {
            Preconditions.checkState(endpointBatch.size() == queryBatch.size(),
                    format("endpointBatch size (%s) is not equal to queryBatch size (%s)", endpointBatch.size(), queryBatch.size()));
        }

        List<JHttpResponse> responses = new ArrayList<>();
        List<JHttpEndpoint> endpoints = endpointBatch.getEndpoints();
        List<JHttpQuery> queries = queryBatch != null ? queryBatch.getQueries() : Collections.emptyList();
        try {
            for (int i = 0; i < endpoints.size(); i++) {
                JHttpEndpoint jHttpEndpoint = endpoints.get(i);
                JHttpQuery jHttpQuery = isNotEmpty(queries) ? queries.get(i) : JHttpQuery.EMPTY_QUERY;
                JHttpResponse jHttpResponse = httpClient.execute(jHttpEndpoint, jHttpQuery);
                responses.add(jHttpResponse);
            }
            return new ResponseBatch<>(responses);
        } catch (Exception e) {
            throw new InvocationException(format("Exception occurred during execution of queryBatch %s to endpointBatch %s.", queryBatch, endpointBatch), e);
        }
    }
}
