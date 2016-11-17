package com.griddynamics.jagger.invoker.v2;

import static java.util.stream.Collectors.toMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

/**
 * An object that represents HTTP-response. It consists of {@link JHttpResponse#status},
 * {@link JHttpResponse#body} and {@link JHttpResponse#headers} fields. <p>
 *
 * @author Anton Antonenko
 * @since 1.3
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

    @Override
    public String toString() {
        return "JHttpResponse{" +
                "status=" + status +
                ", body=" + body +
                ", headers=" + headers +
                '}';
    }
}
