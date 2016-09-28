package com.griddynamics.jagger.invoker.http.v2;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.http.ApacheAbstractHttpInvoker;
import com.griddynamics.jagger.invoker.http.HttpInvoker;

import static java.lang.String.format;

/**
 * An object that represents abstract HTTP-invoker that invokes services of SuT via http protocol. <p>
 * Extending classes should provide its own implementation of
 * {@link #invoke(JHttpQuery query, JHttpEndpoint endpoint)} method.<p>
 * Also, {@link HTTP_CLIENT httpClient} must be provided by constructor {@link AbstractHttpInvoker#AbstractHttpInvoker(HTTP_CLIENT)}.<br/>
 *
 * @param <HTTP_CLIENT> the type of the HTTP-client (look at {@link JHttpClient})<p>
 * @author Anton Antonenko
 * @see Invoker
 * @see ApacheAbstractHttpInvoker
 * @see HttpInvoker
 * @since 1.2.7
 */
@SuppressWarnings("unused")
public abstract class AbstractHttpInvoker<HTTP_CLIENT extends JHttpClient> implements Invoker<JHttpQuery, JHttpResponse, JHttpEndpoint> {

    /**
     * {@link JHttpClient} implementation to be used by invoker
     */
    private HTTP_CLIENT httpClient;

    public AbstractHttpInvoker(HTTP_CLIENT httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Performs HTTP <b>query</b> to the <b>endpoint</b> using {@link HTTP_CLIENT httpClient}.
     *
     * @param endpoint {@link JHttpEndpoint} to which query must be performed
     * @param query    {@link JHttpQuery} to perform
     * @return {@link JHttpResponse} - the result of the query
     * @throws InvocationException thrown if invocation failed
     */
    @Override
    public JHttpResponse invoke(JHttpQuery query, JHttpEndpoint endpoint) throws InvocationException {
        Preconditions.checkNotNull(query, "JHttpQuery is null!");
        Preconditions.checkNotNull(endpoint, "JHttpEndpoint is null!");
        try {
            return httpClient.execute(endpoint, query);
        } catch (Exception e) {
            throw new InvocationException(format("Exception occurred during execution of query %s to endpoint %s.", query, endpoint), e);
        }
    }

    public HTTP_CLIENT getHttpClient() {
        return httpClient;
    }
}
