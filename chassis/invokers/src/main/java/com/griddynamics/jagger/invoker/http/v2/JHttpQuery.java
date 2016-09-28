package com.griddynamics.jagger.invoker.http.v2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aantonenko on 9/27/16.
 */
public class JHttpQuery<T> implements Serializable {

    private HttpMethod method;
    private HttpHeaders headers;
    private T body;
    private Map<String, String> queryParams;
    private Map<String, Object> clientParams;

    public JHttpQuery<T> method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public JHttpQuery<T> get() {
        return method(HttpMethod.GET);
    }

    public JHttpQuery<T> post() {
        return method(HttpMethod.POST);
    }

    public JHttpQuery<T> put() {
        return method(HttpMethod.PUT);
    }

    public JHttpQuery<T> patch() {
        return method(HttpMethod.PATCH);
    }

    public JHttpQuery<T> delete() {
        return method(HttpMethod.DELETE);
    }

    public JHttpQuery<T> trace() {
        return method(HttpMethod.TRACE);
    }

    public JHttpQuery<T> head() {
        return method(HttpMethod.HEAD);
    }

    public JHttpQuery<T> options() {
        return method(HttpMethod.OPTIONS);
    }

    public JHttpQuery<T> headers(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public JHttpQuery<T> headers(Map<String, List<String>> headers) {
        initHeadersIfNull();
        this.headers.putAll(headers);
        return this;
    }

    public JHttpQuery<T> header(String key, List<String> value) {
        initHeadersIfNull();
        headers.put(key, value);
        return this;
    }

    public JHttpQuery<T> cookies(Map<String, String> cookies) {
        initHeadersIfNull();
        cookies.entrySet().forEach(entry -> this.headers.add("Cookie", entry.getKey() + "=" + entry.getValue()));
        return this;
    }

    public JHttpQuery<T> body(T body) {
        this.body = body;
        return this;
    }

    public JHttpQuery<T> queryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public JHttpQuery<T> queryParam(String paramName, String paramValue) {
        if (queryParams == null)
            this.queryParams = new HashMap<>();
        this.queryParams.put(paramName, paramValue);
        return this;
    }

    public JHttpQuery<T> clientParams(Map<String, Object> clientParams) {
        this.clientParams = clientParams;
        return this;
    }

    public JHttpQuery<T> clientParam(String paramName, Object paramValue) {
        if (clientParams == null)
            this.clientParams = new HashMap<>();
        this.clientParams.put(paramName, paramValue);
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public T getBody() {
        return body;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, Object> getClientParams() {
        return clientParams;
    }

    private void initHeadersIfNull() {
        if (this.headers == null)
            this.headers = new HttpHeaders();
    }
}
