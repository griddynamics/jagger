package com.griddynamics.jagger.invoker.v2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * An object that represents HTTP-response. It consists of {@link JHttpResponse#status},
 * {@link JHttpResponse#body} and {@link JHttpResponse#headers} fields. <p>
 *
 * @author Anton Antonenko
 * @since 2.0
 *
 * @ingroup Main_Http_group
 */
public class JHttpResponse<T> implements Serializable {

    private HttpStatus status;
    private T body;
    private HttpHeaders headers;

    public JHttpResponse(HttpStatus status, T body, HttpHeaders headers) {
        this.status = status;
        this.body = body;
        this.headers = headers;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public T getBody() {
        return body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Map<String, String> getCookies() {
        return headers.get("Cookie").stream()
                .map(cookieStr -> cookieStr.split("="))
                .collect(toMap(cookieArr -> cookieArr[0], cookieArr -> cookieArr[1]));
    }

    public static JHttpResponse copyOf(JHttpResponse jHttpResponse) {
        if (jHttpResponse == null)
            return null;
        return new JHttpResponse(jHttpResponse.getStatus(), jHttpResponse.getBody(), jHttpResponse.getHeaders());
    }

    @Override
    public String toString() {
        return "JHttpResponse{" +
                "status=" + status +
                ", body=" + body +
                ", headers=" + headers +
                '}';
    }
}
