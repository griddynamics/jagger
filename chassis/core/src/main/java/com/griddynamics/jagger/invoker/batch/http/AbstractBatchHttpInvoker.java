/**
 * @package com.griddynamics.jagger.invoker.v2
 * package for test definition of http load tests
 */
package com.griddynamics.jagger.invoker.batch.http;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.batch.BatchInvoker;
import com.griddynamics.jagger.invoker.batch.EndpointBatch;
import com.griddynamics.jagger.invoker.batch.QueryBatch;
import com.griddynamics.jagger.invoker.batch.ResponseBatch;
import com.griddynamics.jagger.invoker.v2.JHttpClient;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

/**
 * An object that represents abstract HTTP-invoker that invokes services of SuT via http protocol. <p>
 * Extending classes should provide its own implementation of
 * {@link #invoke(QueryBatch, EndpointBatch)} method.<p>
 * Also, {@link HTTP_CLIENT httpClient} must be provided by constructor {@link AbstractBatchHttpInvoker#AbstractBatchHttpInvoker(HTTP_CLIENT)}.<br/>
 *
 * @param <HTTP_CLIENT> the type of the HTTP-client (look at {@link JHttpClient})<p>
 * @author Anton Antonenko
 * @see Invoker
 * @since 2.0.1
 *
 * @ingroup Main_Http_group
 */
@SuppressWarnings("unused")
public abstract class AbstractBatchHttpInvoker<HTTP_CLIENT extends JHttpClient> implements BatchInvoker<JHttpQuery, JHttpResponse, JHttpEndpoint> {

    /**
     * {@link JHttpClient} implementation to be used by invoker
     */
    protected HTTP_CLIENT httpClient;

    public AbstractBatchHttpInvoker(HTTP_CLIENT httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * This method must be implemented by extending classes. <p>
     * It must perform HTTP <b>queryBatch</b> to the <b>endpointBatch</b> using {@link HTTP_CLIENT httpClient}. <p>
     * Queries in <b>queryBatch</b> must be matched with <b>endpointBatch</b> in the way when 1st query matches 1st endpoint,
     * 2nd query matches 2nd endpoint, and so on ... The size of <b>queryBatch</b> must be equal to <b>endpointBatch</b> size.<p>
     * <b>queryBatch</b> may be null. In this case requests to all endpoints in <b>endpointBatch</b> will be executed without query.
     * All pairs of query-endpoint must produce result, which must be collected to {@link ResponseBatch} and returned after all requests executed.
     *
     * @param endpointBatch {@link EndpointBatch} to which queryBatch must be performed
     * @param queryBatch    {@link QueryBatch} to perform
     * @return {@link ResponseBatch} - the result of all executions
     * @throws InvocationException thrown if invocation failed
     */
    @Override
    public abstract ResponseBatch<JHttpResponse> invoke(QueryBatch<JHttpQuery> queryBatch, EndpointBatch<JHttpEndpoint> endpointBatch) throws InvocationException;

    public HTTP_CLIENT getHttpClient() {
        return httpClient;
    }
}