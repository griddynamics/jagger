/** @package com.griddynamics.jagger.invoker.v2
 * package for test definition of http load tests
 */
package com.griddynamics.jagger.invoker.v2;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;

/**
 * An object that represents abstract HTTP-invoker that invokes services of SuT via http protocol. <p>
 * Extending classes should provide its own implementation of
 * {@link #invoke(JHttpQuery query, JHttpEndpoint endpoint)} method.<p>
 * Also, {@link HTTP_CLIENT httpClient} must be provided by constructor {@link AbstractHttpInvoker#AbstractHttpInvoker(HTTP_CLIENT)}.<br/>
 *
 * @param <HTTP_CLIENT> the type of the HTTP-client (look at {@link JHttpClient})<p>
 * @author Anton Antonenko
 * @see Invoker
 * @since 2.0
 *
 * @ingroup Main_Http_group
 */
@SuppressWarnings("unused")
public abstract class AbstractHttpInvoker<HTTP_CLIENT extends JHttpClient> implements Invoker<JHttpQuery, JHttpResponse, JHttpEndpoint> {

    /**
     * {@link JHttpClient} implementation to be used by invoker
     */
    protected HTTP_CLIENT httpClient;

    public AbstractHttpInvoker(HTTP_CLIENT httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * This method must be implemented by extending classes. <p>
     * It must perform HTTP <b>query</b> to the <b>endpoint</b> using {@link HTTP_CLIENT httpClient}.
     *
     * @param endpoint {@link JHttpEndpoint} to which query must be performed
     * @param query    {@link JHttpQuery} to perform
     * @return {@link JHttpResponse} - the result of the query
     * @throws InvocationException thrown if invocation failed
     */
    @Override
    public abstract JHttpResponse invoke(JHttpQuery query, JHttpEndpoint endpoint) throws InvocationException;

    public HTTP_CLIENT getHttpClient() {
        return httpClient;
    }
}