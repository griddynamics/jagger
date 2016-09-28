package com.griddynamics.jagger.invoker.http.v2;

import java.util.Map;

/**
 * Created by aantonenko on 9/27/16.
 */
public class JSpringBasedHttpClient implements JHttpClient {

    private Map<String, Object> clientParams;

    @Override
    public JHttpResponse execute(JHttpEndpoint endpoint, JHttpQuery query) {
        throw new UnsupportedOperationException();
    }
}
