package com.griddynamics.jagger.invoker.v2;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.DEFAULT_URI_VARIABLES;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.ERROR_HANDLER;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.INTERCEPTORS;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.MESSAGE_CONVERTERS;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.REQUEST_FACTORY;
import static com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient.JSpringBasedHttpClientParameters.URI_TEMPLATE_HANDLER;
import static java.lang.String.format;

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

    /**
     * values: {@link JSpringBasedHttpClientParameters#DEFAULT_URI_VARIABLES}, {@link JSpringBasedHttpClientParameters#ERROR_HANDLER},
     * {@link JSpringBasedHttpClientParameters#MESSAGE_CONVERTERS}, {@link JSpringBasedHttpClientParameters#URI_TEMPLATE_HANDLER},
     * {@link JSpringBasedHttpClientParameters#INTERCEPTORS}, {@link JSpringBasedHttpClientParameters#REQUEST_FACTORY}
     */
    public enum JSpringBasedHttpClientParameters {
        DEFAULT_URI_VARIABLES("defaultUriVariables"),
        ERROR_HANDLER("errorHandler"),
        MESSAGE_CONVERTERS("messageConverters"),
        URI_TEMPLATE_HANDLER("uriTemplateHandler"),
        INTERCEPTORS("interceptors"),
        REQUEST_FACTORY("requestFactory");

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
     * {@link SpringBasedHttpClient#SpringBasedHttpClient(Map)} constructor or by {@link SpringBasedHttpClient#setClientParams(Map)} setter.<p>
     * <p><p>
     * The list of client params (look at {@link JSpringBasedHttpClientParameters}): <p>
     * - {@code Map<String, ?> defaultUriVariables} (look at {@link RestTemplate#setDefaultUriVariables(Map)}) <p>
     * - {@code ResponseErrorHandler errorHandler} (look at {@link RestTemplate#setErrorHandler(ResponseErrorHandler)}) <p>
     * - {@code List<HttpMessageConverter<?>> messageConverters} (look at {@link RestTemplate#setMessageConverters(List)}) <p>
     * - {@code UriTemplateHandler uriTemplateHandler} (look at {@link RestTemplate#setUriTemplateHandler(UriTemplateHandler)}) <p>
     * - {@code List<ClientHttpRequestInterceptor> interceptors} (look at {@link RestTemplate#setInterceptors(List)}) <p>
     * - {@code ClientHttpRequestFactory requestFactory} (look at {@link RestTemplate#setRequestFactory(ClientHttpRequestFactory)}) <p>
     */
    private Map<String, Object> clientParams;

    private RestTemplate restTemplate;

    public SpringBasedHttpClient() {
        clientParams = new HashMap<>();
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(getSslAllTrustRequestFactory());
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
        clientParams.forEach((parameterKey, parameterVal) -> {
            if (parameterKey.equals(DEFAULT_URI_VARIABLES.value)) {
                restTemplate.setDefaultUriVariables((Map<String, ?>) parameterVal);
            } else if (parameterKey.equals(ERROR_HANDLER.value)) {
                restTemplate.setErrorHandler((ResponseErrorHandler) parameterVal);
            } else if (parameterKey.equals(MESSAGE_CONVERTERS.value)) {
                restTemplate.setMessageConverters((List<HttpMessageConverter<?>>) parameterVal);
            } else if (parameterKey.equals(URI_TEMPLATE_HANDLER.value)) {
                restTemplate.setUriTemplateHandler((UriTemplateHandler) parameterVal);
            } else if (parameterKey.equals(INTERCEPTORS.value)) {
                restTemplate.setInterceptors((List<ClientHttpRequestInterceptor>) parameterVal);
            } else if (parameterKey.equals(REQUEST_FACTORY.value)) {
                restTemplate.setRequestFactory((ClientHttpRequestFactory) parameterVal);
            } else {
                throw new IllegalArgumentException(format("Unknown parameter name '%s'!", parameterKey));
            }
        });
    }

    private HttpComponentsClientHttpRequestFactory getSslAllTrustRequestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        requestFactory.setHttpClient(httpClient);
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
        return clientParams;
    }

    public void setClientParams(Map<String, Object> clientParams) {
        this.clientParams = clientParams;
    }
}
