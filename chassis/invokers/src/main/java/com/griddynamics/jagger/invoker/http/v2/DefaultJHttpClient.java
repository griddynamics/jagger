package com.griddynamics.jagger.invoker.http.v2;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.griddynamics.jagger.invoker.http.v2.DefaultJHttpClient.JSpringBasedHttpClientParameters.DEFAULT_URI_VARIABLES;
import static com.griddynamics.jagger.invoker.http.v2.DefaultJHttpClient.JSpringBasedHttpClientParameters.ERROR_HANDLER;
import static com.griddynamics.jagger.invoker.http.v2.DefaultJHttpClient.JSpringBasedHttpClientParameters.INTERCEPTORS;
import static com.griddynamics.jagger.invoker.http.v2.DefaultJHttpClient.JSpringBasedHttpClientParameters.MESSAGE_CONVERTERS;
import static com.griddynamics.jagger.invoker.http.v2.DefaultJHttpClient.JSpringBasedHttpClientParameters.REQUEST_FACTORY;
import static com.griddynamics.jagger.invoker.http.v2.DefaultJHttpClient.JSpringBasedHttpClientParameters.URI_TEMPLATE_HANDLER;
import static java.lang.String.format;

/**
 * Implementation of {@link JHttpClient}. <p>
 * This implementation is based on the Spring {@link RestTemplate}.
 *
 * @author Anton Antonenko
 * @see JHttpClient
 * @since 1.3
 */
@SuppressWarnings({"unused", "unchecked"})
public class DefaultJHttpClient implements JHttpClient {

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
     * {@link DefaultJHttpClient#DefaultJHttpClient(Map)} constructor or by {@link JHttpQuery#clientParams} property.<p>
     * Client parameters of query {@link JHttpQuery#clientParams} will override the same parameters of <b>clientParams</b> field
     * for the time of execution of this query.
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

    private RestTemplate restTemplate = new RestTemplate();

    public DefaultJHttpClient(Map<String, Object> clientParams) {
        this.clientParams = clientParams;
    }

    @Override
    public JHttpResponse execute(JHttpEndpoint endpoint, JHttpQuery query) {
        setRestTemplateParamsForQuery(query);
        URI endpointURI = endpoint.getURI(query.getQueryParams());

        RequestEntity requestEntity = mapToRequestEntity(query, endpointURI);
        ResponseEntity responseEntity = restTemplate.exchange(endpointURI, query.getMethod(), requestEntity, Object.class);

        restoreDefaultRestTemplateParams();
        return mapToJHttpResponse(responseEntity);
    }

    private void setRestTemplateParamsForQuery(JHttpQuery query) {
        Map<String, Object> queryClientParams = new HashMap<>(query.getClientParams());
        clientParams.entrySet().forEach(entry -> queryClientParams.putIfAbsent(entry.getKey(), entry.getValue()));
        setRestTemplateParams(queryClientParams);
    }

    private void restoreDefaultRestTemplateParams() {
        setRestTemplateParams(this.clientParams);
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

    private <T> RequestEntity<T> mapToRequestEntity(JHttpQuery<T> query, URI endpointURI) {
        return new RequestEntity<>(query.getBody(), query.getHeaders(), query.getMethod(), endpointURI);
    }

    private <T> JHttpResponse<T> mapToJHttpResponse(ResponseEntity<T> responseEntity) {
        JHttpResponse<T> jHttpResponse = new JHttpResponse<>();
        jHttpResponse.setHeaders(responseEntity.getHeaders());
        jHttpResponse.setBody(responseEntity.getBody());
        jHttpResponse.setStatus(responseEntity.getStatusCode());
        return jHttpResponse;
    }
}
