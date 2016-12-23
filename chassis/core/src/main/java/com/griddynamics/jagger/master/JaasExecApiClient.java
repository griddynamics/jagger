package com.griddynamics.jagger.master;

import com.griddynamics.jagger.jaas.storage.model.TestExecutionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Handles communication to JaaS execution API.
 */
public class JaasExecApiClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JaasExecApiClient.class);
    
    private final URI executionUri;
    private final RestTemplate restTemplate = new RestTemplate();
    
    public JaasExecApiClient(String executionId, String jaasEndpoint) {
        URI jaasEndpointUri;
        try {
            jaasEndpointUri = new URI(jaasEndpoint);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Incorrect JaaS endpoint", e);
        }
        this.executionUri = UriComponentsBuilder.newInstance()
                                                .uri(jaasEndpointUri)
                                                .path("/executions")
                                                .path("/" + executionId)
                                                .build()
                                                .toUri();
    }
    
    public Optional<TestExecutionEntity> getExecution() {
        try {
            return doWithRetries(rt -> restTemplate.getForEntity(executionUri, TestExecutionEntity.class).getBody());
        } catch (HttpClientErrorException e) {
            LOGGER.error("A client error during a GET request to {}.", executionUri, e);
            throw e;
        }
    }
    
    public void startExecution() {
        Optional<TestExecutionEntity> executionEntity = getExecution();
        executionEntity.ifPresent(entity -> {
            entity.setStatus(TestExecutionEntity.TestExecutionStatus.RUNNING);
            updateExecution(entity);
        });
    }
    
    public void completeExecution(final String sessionId) {
        Optional<TestExecutionEntity> executionEntity = getExecution();
        executionEntity.ifPresent(entity -> {
            entity.setStatus(TestExecutionEntity.TestExecutionStatus.COMPLETED);
            entity.setSessionId(sessionId);
            updateExecution(entity);
        });
    }
    
    public void failExecution(final String errorMessage) {
        Optional<TestExecutionEntity> executionEntity = getExecution();
        executionEntity.ifPresent(entity -> {
            entity.setStatus(TestExecutionEntity.TestExecutionStatus.FAILED);
            entity.setErrorMessage(errorMessage);
            updateExecution(entity);
        });
    }
    
    private void updateExecution(TestExecutionEntity executionEntity) {
        try {
            doWithRetries(rt -> {
                restTemplate.put(executionUri, executionEntity);
                return null;
            });
        } catch (HttpClientErrorException e) {
            LOGGER.error("A client error during a PUT request to {} with body {}", executionUri, executionEntity, e);
        }
    }
    
    private <T> Optional<T> doWithRetries(Function<RestTemplate, T> function) {
        int retriesTimeout = 10;
        int retriesLeft = 5;
        do {
            try {
                return Optional.ofNullable(function.apply(restTemplate));
            } catch (HttpServerErrorException e) { // will retry in case that is a temporarily issue
                LOGGER.warn("A server error during a request", e);
                try {
                    TimeUnit.SECONDS.sleep(retriesTimeout);
                    retriesLeft--;
                    retriesTimeout *= 2;
                } catch (InterruptedException e1) {
                    retriesLeft = 0;
                }
            }
        }
        while (retriesLeft > 0);
        
        return Optional.empty();
    }
}
