package com.griddynamics.jagger.invoker.http.v2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An object that represents HTTP-request. It consists of {@link JHttpQuery#method},
 * {@link JHttpQuery#headers}, {@link JHttpQuery#body}, {@link JHttpQuery#queryParams} and {@link JHttpQuery#clientParams} fields.
 * <p>
 * It contains methods which makes query construction concise and easy to read.
 *
 * @param <T> type of the query {@link JHttpQuery#body}
 * @author Anton Antonenko
 * @apiNote Example of query construction:
 * <pre>{@code
 * HashMap<String, String> cookies = new HashMap<>();
 * cookies.put("JSESSIONID", "0123456789");
 *
 * HttpHeaders headers = new HttpHeaders();
 * headers.add("header", "value");
 *
 * JHttpQuery<Integer> httpQuery = new JHttpQuery<Integer>()
 *    .get()
 *    .cookies(cookies)
 *    .cookie("name", "value")
 *    .headers(headers)
 *    .header("header", newArrayList("value1", "value2"))
 *    .queryParam("query param", "value")
 *    .clientParam("client param", new Object())
 *    .body(42);
 * }</pre>
 * @since 1.2.7
 */
@SuppressWarnings("unused")
public class JHttpQuery<T> implements Serializable {

    private HttpMethod method;
    private HttpHeaders headers;
    private T body;
    private Map<String, String> queryParams;
    private Map<String, Object> clientParams;

    /**
     * Sets parameter {@link HttpMethod method} to {@link JHttpQuery#method} field. <p>
     *
     * @param method {@link HttpMethod} to be used in request
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().method(HttpMethod.GET); }
     */
    public JHttpQuery<T> method(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * Sets {@link JHttpQuery#method} to {@link HttpMethod#GET}. <p>
     * The same as {@code this.method(HttpMethod.GET); }
     *
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().get(); }
     */
    public JHttpQuery<T> get() {
        return method(HttpMethod.GET);
    }

    /**
     * Sets {@link JHttpQuery#method} to {@link HttpMethod#POST}. <p>
     * The same as {@code this.method(HttpMethod.POST); }
     *
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().post(); }
     */
    public JHttpQuery<T> post() {
        return method(HttpMethod.POST);
    }

    /**
     * Sets {@link JHttpQuery#method} to {@link HttpMethod#PUT}. <p>
     * The same as {@code this.method(HttpMethod.PUT); }
     *
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().put(); }
     */
    public JHttpQuery<T> put() {
        return method(HttpMethod.PUT);
    }

    /**
     * Sets {@link JHttpQuery#method} to {@link HttpMethod#PATCH}. <p>
     * The same as {@code this.method(HttpMethod.PATCH); }
     *
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().patch(); }
     */
    public JHttpQuery<T> patch() {
        return method(HttpMethod.PATCH);
    }

    /**
     * Sets {@link JHttpQuery#method} to {@link HttpMethod#DELETE}. <p>
     * The same as {@code this.method(HttpMethod.DELETE); }
     *
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().delete(); }
     */
    public JHttpQuery<T> delete() {
        return method(HttpMethod.DELETE);
    }

    /**
     * Sets {@link JHttpQuery#method} to {@link HttpMethod#TRACE}. <p>
     * The same as {@code this.method(HttpMethod.TRACE); }
     *
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().trace(); }
     */
    public JHttpQuery<T> trace() {
        return method(HttpMethod.TRACE);
    }

    /**
     * Sets {@link JHttpQuery#method} to {@link HttpMethod#HEAD}. <p>
     * The same as {@code this.method(HttpMethod.HEAD); }
     *
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().head(); }
     */
    public JHttpQuery<T> head() {
        return method(HttpMethod.HEAD);
    }

    /**
     * Sets {@link JHttpQuery#method} to {@link HttpMethod#OPTIONS}. <p>
     * The same as {@code this.method(HttpMethod.OPTIONS); }
     *
     * @return this
     * @apiNote Usage: <p>
     * {@code JHttpQuery httpQuery = new JHttpQuery().options(); }
     */
    public JHttpQuery<T> options() {
        return method(HttpMethod.OPTIONS);
    }

    /**
     * Sets parameter {@link HttpHeaders headers} to {@link JHttpQuery#headers} field.
     *
     * @param headers headers to be added to query
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * HttpHeaders headers = new HttpHeaders();
     * headers.add("header", "value")
     *
     * JHttpQuery httpQuery = new JHttpQuery().headers(headers);
     * }</pre>
     */
    public JHttpQuery<T> headers(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    /**
     * Sets parameter {@link Map headers} to {@link JHttpQuery#headers} field.
     *
     * @param headers headers to be added to query
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * Map<String, List<String>> headers = new HashMap<>();
     * headers.put("header", newArrayList("value1", "value2"));
     *
     * JHttpQuery httpQuery = new JHttpQuery().headers(headers);
     * }</pre>
     */
    public JHttpQuery<T> headers(Map<String, List<String>> headers) {
        initHeadersIfNull();
        this.headers.putAll(headers);
        return this;
    }

    /**
     * Adds header "key=values" to {@link JHttpQuery#headers}.
     *
     * @param key    name of header
     * @param values values of header
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * JHttpQuery httpQuery = new JHttpQuery().header("header", newArrayList("value1", "value2"));
     * }</pre>
     */
    public JHttpQuery<T> header(String key, List<String> values) {
        initHeadersIfNull();
        headers.put(key, values);
        return this;
    }

    /**
     * Adds {@link Map cookies} to {@link JHttpQuery#headers} field.
     *
     * @param cookies cookies to be added to query
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * HashMap<String, String> cookies = new HashMap<>();
     * cookies.put("JSESSIONID", "0123456789");
     *
     * JHttpQuery httpQuery = new JHttpQuery().cookies(cookies));
     * }</pre>
     */
    public JHttpQuery<T> cookies(Map<String, String> cookies) {
        initHeadersIfNull();
        cookies.entrySet().forEach(entry -> this.headers.add("Cookie", entry.getKey() + "=" + entry.getValue()));
        return this;
    }

    /**
     * Adds Cookie header "name=value" to {@link JHttpQuery#headers} field.
     *
     * @param name  name of cookie
     * @param value value of cookie
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * JHttpQuery httpQuery = new JHttpQuery().cookie("name", "value");
     * }</pre>
     */
    public JHttpQuery<T> cookie(String name, String value) {
        initHeadersIfNull();
        this.headers.add("Cookie", name + "=" + value);
        return this;
    }

    /**
     * Sets parameter {@link T body} to {@link JHttpQuery#body} field.
     *
     * @param body {@link T body} of the query
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * JHttpQuery<Integer> httpQuery = new JHttpQuery<Integer>().body(42);
     * // or
     * JHttpQuery<Object> httpQuery = new JHttpQuery<Object>().body(new Object());
     * // or
     * JHttpQuery<String> httpQuery = new JHttpQuery<String>().body("42");
     * }</pre>
     */
    public JHttpQuery<T> body(T body) {
        this.body = body;
        return this;
    }

    /**
     * Sets parameter {@link Map queryParams} to {@link JHttpQuery#queryParams} field.
     *
     * @param queryParams {@link Map} with query parameters to be added to query
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * Map<String, String> queryParams = new HashMap<>();
     * queryParams.put("page", "1");
     * queryParams.put("sort", "asc");
     *
     * JHttpQuery httpQuery = new JHttpQuery().queryParams(queryParams);
     * }</pre>
     */
    public JHttpQuery<T> queryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    /**
     * Adds query parameter "paramName=paramValue" to {@link JHttpQuery#queryParams} field.
     *
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * JHttpQuery httpQuery = new JHttpQuery().queryParam("parameter","value");
     * }</pre>
     */
    public JHttpQuery<T> queryParam(String paramName, String paramValue) {
        if (queryParams == null)
            this.queryParams = new HashMap<>();
        this.queryParams.put(paramName, paramValue);
        return this;
    }

    /**
     * Sets parameter {@link Map clientParams} to {@link JHttpQuery#clientParams} field.
     *
     * @param clientParams {@link Map} with HTTP-client parameters to be added to query
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * Map<String, String> clientParams = new HashMap<>();
     * clientParams.put("page", "1");
     * clientParams.put("sort", "asc");
     *
     * JHttpQuery httpQuery = new JHttpQuery().clientParams(clientParams);
     * }</pre>
     */
    public JHttpQuery<T> clientParams(Map<String, Object> clientParams) {
        this.clientParams = clientParams;
        return this;
    }

    /**
     * Adds HTTP-client parameter "paramName=paramValue" to {@link JHttpQuery#clientParams} field.
     *
     * @return this
     * @apiNote Usage:
     * <pre>{@code
     * JHttpQuery httpQuery = new JHttpQuery().clientParam("parameter","value");
     * }</pre>
     */
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

    @Override
    public String toString() {
        return "JHttpQuery{" +
                "method=" + method +
                ", headers=" + headers +
                ", body=" + body +
                ", queryParams=" + queryParams +
                ", clientParams=" + clientParams +
                '}';
    }

    private void initHeadersIfNull() {
        if (this.headers == null)
            this.headers = new HttpHeaders();
    }
}
