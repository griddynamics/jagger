package com.griddynamics.jagger.invoker.http.v2;

/**
 * Created by aantonenko on 9/27/16.
 */
public interface JHttpClient {

    JHttpResponse execute(JHttpEndpoint endpoint, JHttpQuery query);
}
