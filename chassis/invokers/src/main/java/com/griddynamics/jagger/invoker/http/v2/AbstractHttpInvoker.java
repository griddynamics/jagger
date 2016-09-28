package com.griddynamics.jagger.invoker.http.v2;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;

/**
 * Created by aantonenko on 9/27/16.
 */
public abstract class AbstractHttpInvoker<HTTP_CLIENT extends JHttpClient> implements Invoker<JHttpQuery, JHttpResponse, JHttpEndpoint> {

    private HTTP_CLIENT httpClient;

    public AbstractHttpInvoker(HTTP_CLIENT httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public JHttpResponse invoke(JHttpQuery query, JHttpEndpoint endpoint) throws InvocationException {
        Preconditions.checkNotNull(query, "JHttpQuery is null!");
        Preconditions.checkNotNull(endpoint, "JHttpEndpoint is null!");
        return httpClient.execute(endpoint, query);
    }

    public HTTP_CLIENT getHttpClient() {
        return httpClient;
    }
}
