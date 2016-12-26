package com.griddynamics.jagger.invoker.v2;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriTemplateHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.CONNECT_TIMEOUT_IN_MS;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.DEFAULT_URI_VARIABLES;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.ERROR_HANDLER;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.INTERCEPTORS;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.MAX_CONN_PER_ROUTE;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.MAX_CONN_TOTAL;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.MESSAGE_CONVERTERS;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.REQUEST_FACTORY;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.URI_TEMPLATE_HANDLER;

/**
 * Implementation of {@link JHttpClient}. <p>
 * This implementation is based on the Spring {@link RestTemplate}.
 *
 * @author Anton Antonenko
 * @see JHttpClient
 * @since 2.0
 */
@SuppressWarnings({"unused", "unchecked"})
public class SpringBasedHttpClient implements JHttpClient {
    private static final Logger log = LoggerFactory.getLogger(SpringBasedHttpClient.class);
    private static final int DEFAULT_MAX_CONN_TOTAL = Integer.MAX_VALUE;
    private static final int DEFAULT_MAX_CONN_PER_ROUTE = Integer.MAX_VALUE;
    private static final int DEFAULT_CONNECT_TIMEOUT_IN_MS = 60000;

    /**
     * values: {@link JSpringBasedHttpClientParameters#DEFAULT_URI_VARIABLES}, {@link JSpringBasedHttpClientParameters#ERROR_HANDLER},
     * {@link JSpringBasedHttpClientParameters#MESSAGE_CONVERTERS}, {@link JSpringBasedHttpClientParameters#URI_TEMPLATE_HANDLER},
     * {@link JSpringBasedHttpClientParameters#INTERCEPTORS}, {@link JSpringBasedHttpClientParameters#REQUEST_FACTORY}
     */
    public enum JSpringBasedHttpClientParameters {
        DEFAULT_URI_VARIABLES("default_uri_variables"),
        ERROR_HANDLER("error_handler"),
        MESSAGE_CONVERTERS("message_converters"),
        URI_TEMPLATE_HANDLER("uri_template_handler"),
        INTERCEPTORS("interceptors"),
        REQUEST_FACTORY("request_factory"),
        MAX_CONN_TOTAL("max_conn_total"),
        MAX_CONN_PER_ROUTE("max_conn_per_route"),
        CONNECT_TIMEOUT_IN_MS("connect_timeout");

        private String value;

        JSpringBasedHttpClientParameters(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * This field is a container for {@link RestTemplate} parameters which can be passed by the
     * {@link SpringBasedHttpClient#SpringBasedHttpClient(Map)} constructor .<p>
     * <p>
     * The list of supported client params (look at {@link JSpringBasedHttpClientParameters}): <p>
     * - {@code Map<String, ?> default_uri_variables} (look at {@link RestTemplate#setDefaultUriVariables(Map)}) <p>
     * - {@code ResponseErrorHandler error_handler} (look at {@link RestTemplate#setErrorHandler(ResponseErrorHandler)}) <p>
     * - {@code List<HttpMessageConverter<?>> message_converters} (look at {@link RestTemplate#setMessageConverters(List)}) <p>
     * - {@code UriTemplateHandler uri_template_handler} (look at {@link RestTemplate#setUriTemplateHandler(UriTemplateHandler)}) <p>
     * - {@code List<ClientHttpRequestInterceptor> interceptors} (look at {@link RestTemplate#setInterceptors(List)}) <p>
     * - {@code ClientHttpRequestFactory request_factory} (look at {@link RestTemplate#setRequestFactory(ClientHttpRequestFactory)}) <p>
     * - {@code int max_conn_total} (look at {@link HttpClientBuilder#setMaxConnTotal(int)}) <p>
     * - {@code int max_conn_per_route} (look at {@link HttpClientBuilder#setMaxConnPerRoute(int)}) <p>
     * - {@code int connect_timeout} (look at {@link HttpComponentsClientHttpRequestFactory#setConnectTimeout(int)}) <p>
     */
    private final Map<String, Object> clientParams;

    private RestTemplate restTemplate;

    public SpringBasedHttpClient() {
        clientParams = new HashMap<>();
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(getRequestFactory());
        restTemplate.setErrorHandler(new AllowAllCodesResponseErrorHandler());
    }

    public SpringBasedHttpClient(Map<String, Object> clientParams) {
        this();
        this.clientParams.putAll(clientParams);
        setRestTemplateParams(this.clientParams);
    }

    @Override
    public JHttpResponse execute(JHttpEndpoint endpoint, JHttpQuery query) {
        if (query == null)
            return execute(endpoint);

        URI endpointURI = endpoint.getURI(query.getPath(), query.getQueryParams());
        RequestEntity requestEntity = mapToRequestEntity(query, endpointURI);
        ResponseEntity responseEntity;
        if (query.getResponseBodyType() != null) {
            responseEntity = restTemplate.exchange(endpointURI, query.getMethod(), requestEntity, query.getResponseBodyType());
        } else {
            responseEntity = restTemplate.exchange(endpointURI, query.getMethod(), requestEntity, byte[].class);
        }
        return mapToJHttpResponse(responseEntity);
    }

    public JHttpResponse execute(JHttpEndpoint endpoint) {
        URI endpointURI = endpoint.getURI();
        RequestEntity requestEntity = mapToRequestEntity(endpointURI);
        ResponseEntity responseEntity = restTemplate.exchange(endpointURI, HttpMethod.GET, requestEntity, byte[].class);
        return mapToJHttpResponse(responseEntity);
    }

    private void setRestTemplateParams(Map<String, Object> clientParams) {
        int maxConnPerRoute = DEFAULT_MAX_CONN_PER_ROUTE;
        int maxConnTotal = DEFAULT_MAX_CONN_TOTAL;
        int connectTimeoutInMs = DEFAULT_CONNECT_TIMEOUT_IN_MS;

        if (clientParams.containsKey(DEFAULT_URI_VARIABLES.value)) {
            restTemplate.setDefaultUriVariables((Map<String, ?>) clientParams.get(DEFAULT_URI_VARIABLES.value));
        }
        if (clientParams.containsKey(ERROR_HANDLER.value)) {
            restTemplate.setErrorHandler((ResponseErrorHandler) clientParams.get(ERROR_HANDLER.value));
        }
        if (clientParams.containsKey(MESSAGE_CONVERTERS.value)) {
            restTemplate.setMessageConverters((List<HttpMessageConverter<?>>) clientParams.get(MESSAGE_CONVERTERS.value));
        }
        if (clientParams.containsKey(URI_TEMPLATE_HANDLER.value)) {
            restTemplate.setUriTemplateHandler((UriTemplateHandler) clientParams.get(URI_TEMPLATE_HANDLER.value));
        }
        if (clientParams.containsKey(INTERCEPTORS.value)) {
            restTemplate.setInterceptors((List<ClientHttpRequestInterceptor>) clientParams.get(INTERCEPTORS.value));
        }
        if (clientParams.containsKey(MAX_CONN_PER_ROUTE.value)) {
            Object value = clientParams.get(MAX_CONN_PER_ROUTE.value);
            if (value instanceof String)
                maxConnPerRoute = Integer.parseInt((String) value);
            else
                maxConnPerRoute = (int) value;
        }
        if (clientParams.containsKey(MAX_CONN_TOTAL.value)) {
            Object value = clientParams.get(MAX_CONN_TOTAL.value);
            if (value instanceof String)
                maxConnTotal = Integer.parseInt((String) value);
            else
                maxConnTotal = (int) value;
        }
        if (clientParams.containsKey(CONNECT_TIMEOUT_IN_MS.value)) {
            Object value = clientParams.get(CONNECT_TIMEOUT_IN_MS.value);
            if (value instanceof String)
                connectTimeoutInMs = Integer.parseInt((String) value);
            else
                connectTimeoutInMs = (int) value;
        }

        if (clientParams.containsKey(REQUEST_FACTORY.value)) {
            restTemplate.setRequestFactory((ClientHttpRequestFactory) clientParams.get(REQUEST_FACTORY.value));
        }
        if (!clientParams.containsKey(REQUEST_FACTORY.value) && containsAnyRequestFactoryParam(clientParams)) {
            restTemplate.setRequestFactory(getRequestFactory(maxConnPerRoute, maxConnTotal, connectTimeoutInMs));
        } else if (clientParams.containsKey(REQUEST_FACTORY.value) && containsAnyRequestFactoryParam(clientParams)) {
            throw new IllegalArgumentException("Parameters max_conn_total, max_conn_per_route and connect_timeout cannot be set if " +
                    "request_factory parameter presents. You must configure these parameters in your request_factory entity.");
        }
    }

    private boolean containsAnyRequestFactoryParam(Map<String, Object> clientParams) {
        return clientParams.containsKey(MAX_CONN_PER_ROUTE.value) ||
                clientParams.containsKey(MAX_CONN_TOTAL.value) ||
                clientParams.containsKey(CONNECT_TIMEOUT_IN_MS.value);
    }

    private HttpComponentsClientHttpRequestFactory getRequestFactory() {
        return getRequestFactory(DEFAULT_MAX_CONN_PER_ROUTE, DEFAULT_MAX_CONN_TOTAL, DEFAULT_CONNECT_TIMEOUT_IN_MS);
    }

    private HttpComponentsClientHttpRequestFactory getRequestFactory(int maxConnPerRoute, int maxConnTotal, int connectTimeoutInMs) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setMaxConnPerRoute(maxConnPerRoute)
                .setMaxConnTotal(maxConnTotal)
                .build();
        requestFactory.setHttpClient(httpClient);
        requestFactory.setConnectTimeout(connectTimeoutInMs);
        return requestFactory;
    }

    private <T> RequestEntity<T> mapToRequestEntity(JHttpQuery<T> query, URI endpointURI) {
        return new RequestEntity<>(query.getBody(), query.getHeaders(), query.getMethod(), endpointURI);
    }

    private <T> RequestEntity<T> mapToRequestEntity(URI endpointURI) {
        return new RequestEntity<>(HttpMethod.GET, endpointURI);
    }

    private <T> JHttpResponse<T> mapToJHttpResponse(ResponseEntity<T> responseEntity) {
        return new JHttpResponse<>(responseEntity.getStatusCode(), responseEntity.getBody(), responseEntity.getHeaders());
    }

    public Map<String, Object> getClientParams() {
        return newHashMap(clientParams);
    }

    public static class AllowAllCodesResponseErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            try {
                response.getStatusCode();
            } catch (IllegalArgumentException ex) {
                throw new UnknownHttpStatusCodeException(response.getRawStatusCode(),
                        response.getStatusText(), response.getHeaders(), getResponseBody(response), getCharset(response));
            }
        }

        private byte[] getResponseBody(ClientHttpResponse response) {
            try {
                InputStream responseBody = response.getBody();
                if (responseBody != null) {
                    return FileCopyUtils.copyToByteArray(responseBody);
                }
            } catch (IOException ex) {
                // ignore
            }
            return new byte[0];
        }

        private Charset getCharset(ClientHttpResponse response) {
            HttpHeaders headers = response.getHeaders();
            MediaType contentType = headers.getContentType();
            return contentType != null ? contentType.getCharset() : null;
        }
    }
}
