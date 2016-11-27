package com.griddynamics.jagger.invoker.v2;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.invoker.InvocationException;

import static java.lang.String.format;


/**
 * Default HTTP-invoker that invokes services of SuT via http(s) protocol. <p>
 * By default as HTTP-client {@link SpringBasedHttpClient} is used here, but it can be updated with {@link DefaultHttpInvoker#DefaultHttpInvoker}
 * constructor.
 *
 * @author Anton Antonenko
 * @see AbstractHttpInvoker
 * @since 2.0
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
        Preconditions.checkNotNull(endpoint, "JHttpEndpoint is null!");
        try {
            return httpClient.execute(endpoint, query);
        } catch (Exception e) {
            throw new InvocationException(format("Exception occurred during execution of query %s to endpoint %s.", query, endpoint), e);
        }
    }
}
