package com.griddynamics.jagger.invoker.http.v2;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.invoker.InvocationException;

import static java.lang.String.format;

/**
 * Created by aantonenko on 9/29/16.
 */
@SuppressWarnings("unused")
public class DefaultHttpInvoker extends AbstractHttpInvoker {

    public DefaultHttpInvoker() {
        super(new SpringBasedHttpClient());
    }

    public DefaultHttpInvoker(JHttpClient httpClient) {
        super(httpClient);
    }

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
}
