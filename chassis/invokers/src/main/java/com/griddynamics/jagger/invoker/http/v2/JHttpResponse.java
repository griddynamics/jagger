package com.griddynamics.jagger.invoker.http.v2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * An object that represents HTTP-response. It consists of {@link JHttpResponse#status},
 * {@link JHttpResponse#body} and {@link JHttpResponse#headers} fields. <p>
 *
 * @author Anton Antonenko
 * @since 1.2.7
 */
public class JHttpResponse<T> {

    private HttpStatus status;
    private T body;
    private HttpHeaders headers;

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
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
