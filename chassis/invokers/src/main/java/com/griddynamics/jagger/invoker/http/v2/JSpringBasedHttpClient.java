package com.griddynamics.jagger.invoker.http.v2;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * Implementation of {@link JHttpClient}. <p>
 * This implementation is based on the Spring {@link RestTemplate}.
 *
 * @author Anton Antonenko
 * @see JHttpClient
 * @since 1.2.7
 */
@SuppressWarnings({"unused", "unchecked"})
public class JSpringBasedHttpClient implements JHttpClient {

    private Map<String, Object> clientParams;

    private RestTemplate restTemplate = new RestTemplate();

    public JSpringBasedHttpClient(Map<String, Object> clientParams) {
        this.clientParams = clientParams;
    }

    @Override
    public JHttpResponse execute(JHttpEndpoint endpoint, JHttpQuery query) {
        clientParams.putAll(query.getClientParams());

        URI endpointURI = endpoint.getURI(query.getQueryParams());

        RequestEntity requestEntity = mapToRequestEntity(query, endpointURI);
        ResponseEntity responseEntity = restTemplate.exchange(endpointURI, query.getMethod(), requestEntity, Object.class);

        return mapToJHttpResponse(responseEntity);
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
