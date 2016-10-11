package com.griddynamics.jagger.invoker.http.v2;

/**
 * An object that represents HTTP-client. <br/>
 * <p>
 * Implementation classes should provide an implementation
 * for {@link #execute(JHttpEndpoint endpoint, JHttpQuery query)} method. <br/>
 *
 * @author Anton Antonenko
 * @since 1.3
 */
public interface JHttpClient {

    /**
     * Performs HTTP <b>query</b> to the <b>endpoint</b>.
     *
     * @param endpoint {@link JHttpEndpoint} to which query must be performed
     * @param query    {@link JHttpQuery} to perform
     * @return {@link JHttpResponse} - the result of the query
     * @see JHttpResponse
     * @see JHttpEndpoint
     * @see JHttpQuery
     */
    JHttpResponse execute(JHttpEndpoint endpoint, JHttpQuery query);
}
