package com.griddynamics.jagger.invoker.scenario;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.springframework.http.HttpHeaders;

import java.util.LinkedList;

public class CopyUtil {

    public static JHttpEndpoint copyOf(JHttpEndpoint jHttpEndpoint) {
        if (jHttpEndpoint == null)
            return null;
        return new JHttpEndpoint(jHttpEndpoint.getURI());
    }

    public static JHttpQuery copyOf(JHttpQuery jHttpQuery) {
        if (jHttpQuery == null)
            return null;

        JHttpQuery copy = new JHttpQuery()
                .body(jHttpQuery.getBody())
                .queryParams(jHttpQuery.getQueryParams())
                .path(jHttpQuery.getPath())
                .responseBodyType(jHttpQuery.getResponseBodyType())
                .headers(jHttpQuery.getHeaders());

        switch (jHttpQuery.getMethod()) {
            case DELETE: copy.delete(); break;
            case GET: copy.get(); break;
            case HEAD: copy.head(); break;
            case OPTIONS: copy.options(); break;
            case PATCH: copy.patch(); break;
            case POST: copy.post(); break;
            case PUT: copy.put(); break;
            case TRACE: copy.trace(); break;
        }
        return copy;
    }

    public static JHttpResponse copyOf(JHttpResponse jHttpResponse) {
        if (jHttpResponse == null)
            return null;
        return new JHttpResponse(jHttpResponse.getStatus(), jHttpResponse.getBody(), jHttpResponse.getHeaders());
    }

    public static HttpHeaders copyOf(HttpHeaders headers) {
        if (headers == null)
            return null;
        HttpHeaders headersCopy = new HttpHeaders();
        headers.forEach((key, values) -> headersCopy.put(key, new LinkedList<>(values)));
        return headersCopy;
    }
}
