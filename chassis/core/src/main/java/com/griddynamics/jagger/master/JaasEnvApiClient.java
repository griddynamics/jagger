package com.griddynamics.jagger.master;

import com.griddynamics.jagger.jaas.storage.model.LoadScenarioEntity;
import com.griddynamics.jagger.jaas.storage.model.TestEnvUtils;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Closeable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Handles communication to JaaS environment API -
 * initial registration and further status updates with commands parsing from JaaS response.
 */
public class JaasEnvApiClient implements Closeable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JaasEnvApiClient.class);
    
    private volatile boolean standBy = true;
    private final RestTemplate restTemplate = new RestTemplate();
    private volatile String envId;
    private final URI jaasEndpoint;
    private final int statusReportIntervalSeconds;
    private final Set<String> availableConfigs;
    private boolean registered = false;
    private StatusExchangeThread statusExchangeThread;
    
    public JaasEnvApiClient(String envId,
                            String jaasEndpoint,
                            int statusReportIntervalSeconds,
                            Set<String> availableConfigs) {
        this.envId = envId;
        try {
            this.jaasEndpoint = new URI(jaasEndpoint);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("Incorrect JaaS endpoint %s", jaasEndpoint), e);
        }
    
        if (availableConfigs == null) {
            availableConfigs = Collections.emptySet();
        }
        this.availableConfigs = availableConfigs;
        
        if (statusReportIntervalSeconds <= 0) {
            LOGGER.warn(
                    "Provided status reporting interval seconds is less or equals to zero - {}.\n"
                    + "Using the default value - 15 seconds",
                    statusReportIntervalSeconds
            );
            statusReportIntervalSeconds = 15;
        }
        this.statusReportIntervalSeconds = statusReportIntervalSeconds;
    
    }
    
    public void register() throws InterruptedException {
        final String sessionCookie = doRegister();
        statusExchangeThread = new StatusExchangeThread(sessionCookie);
        statusExchangeThread.start();
        registered = true;
    }
    
    public JaasResponse awaitNextExecution() throws TerminateException, InterruptedException {
        if (!registered) {
            throw new IllegalStateException("must be registered");
        }
        statusExchangeThread.setPendingRequestEntity();
        JaasResponse jaasResponse;
        do {
            jaasResponse = statusExchangeThread.nextConfigToExecute.poll(1, TimeUnit.MINUTES);
            if (!standBy) {
                throw getTerminateException();
            }
        }
        while (jaasResponse == null);
        
        return jaasResponse;
    }
    
    private TerminateException getTerminateException() throws TerminateException {
        throw new TerminateException("Communication to JaaS Env API can't be proceeded");
    }
    
    private String doRegister() throws InterruptedException {
        URI envsUri = UriComponentsBuilder.newInstance().uri(jaasEndpoint).path("/envs").build().toUri();
    
        TestEnvironmentEntity testEnvironmentEntity = buildTestEnvironmentEntityWith(TestEnvironmentStatus.PENDING);
        ResponseEntity<String> responseEntity = null;
        boolean posted = false;
        do {
            try {
                responseEntity = restTemplate.postForEntity(envsUri, testEnvironmentEntity, String.class);
                posted = true;
                LOGGER.info("POST request sent to {} with body {}.", envsUri, testEnvironmentEntity);
            } catch (HttpClientErrorException e) {
                if (!isEnvIdUnacceptable(e)) {
                    throw e;
                }
                envId = UUID.randomUUID().toString();
                LOGGER.warn("Changing env id from {} to {}", testEnvironmentEntity.getEnvironmentId(), envId);
                testEnvironmentEntity.setEnvironmentId(envId);
            } catch (HttpServerErrorException | ResourceAccessException e) {
                LOGGER.warn("Error during registration to '{}'", envsUri, e);
                TimeUnit.SECONDS.sleep(statusReportIntervalSeconds);
            }
        } while (!posted);
    
        final String sessionCookie = extractSessionCookie(responseEntity);
        LOGGER.info("Environment {} successfully registered to JaaS with session cookie - {}", envId, sessionCookie);
        return sessionCookie;
    }
    
    private TestEnvironmentEntity buildTestEnvironmentEntityWith(TestEnvironmentStatus status) {
        TestEnvironmentEntity testEnvironmentEntity = new TestEnvironmentEntity();
        testEnvironmentEntity.setEnvironmentId(envId);
        testEnvironmentEntity.setStatus(status);
        testEnvironmentEntity.setLoadScenarios(availableConfigs.stream()
                                                            .map(LoadScenarioEntity::new)
                                                            .collect(Collectors.toList())
        );
        
        return testEnvironmentEntity;
    }
    
    private boolean isEnvIdUnacceptable(HttpClientErrorException exception) {
        if (exception.getStatusCode() == HttpStatus.CONFLICT) {
            LOGGER.warn("Env with id {} already registered", envId);
            return true;
        }
        // If env id doesn't match acceptable pattern.
        if (exception.getStatusCode() == HttpStatus.BAD_REQUEST && exception.getResponseBodyAsString().contains("doesn't match pattern")) {
            LOGGER.warn("Env id {} doesn't match pattern", envId);
            return true;
        }
        return false;
    }
    
    
    private String extractSessionCookie(ResponseEntity<String> responseEntity) {
        List<String> sessionCookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE).stream()
                                                    .filter(s -> s.contains(TestEnvUtils.SESSION_COOKIE))
                                                    .collect(Collectors.toList());
    
        if (sessionCookies.size() != 1) {
            throw new IllegalStateException(String.format("There are %s values for '%s' cookie", sessionCookies.size(),
                    TestEnvUtils.SESSION_COOKIE
            ));
        }
    
        String rawSessionCookie = sessionCookies.get(0);
        int endIndex = rawSessionCookie.indexOf(";");
        if (endIndex < 0) {
            endIndex = rawSessionCookie.length() - 1;
        }
        return rawSessionCookie.substring(0, endIndex);
    }
    
    public boolean isStandBy() {
        return standBy;
    }
    
    public boolean isRegistered() {
        return registered;
    }
    
    @Override
    public void close() {
        standBy = false;
    }
    
    private final class StatusExchangeThread extends Thread {
        
        private String sessionCookie;
    
        private final SynchronousQueue<JaasResponse> nextConfigToExecute = new SynchronousQueue<>();
        
        private volatile RequestEntity<TestEnvironmentEntity> requestEntity;

        StatusExchangeThread(String sessionCookie) {
            this.sessionCookie = sessionCookie;
            this.requestEntity = buildPendingRequestEntity();

            setName(this.getClass().getName());
        }
        
        private RequestEntity<TestEnvironmentEntity> buildPendingRequestEntity() {
            TestEnvironmentEntity testEnvEntity = buildTestEnvironmentEntityWith(TestEnvironmentStatus.PENDING);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.COOKIE, sessionCookie);
            URI updatesUri = UriComponentsBuilder.newInstance()
                                                 .uri(jaasEndpoint)
                                                 .path("/envs")
                                                 .path("/" + envId)
                                                 .build().toUri();
    
            return new RequestEntity<>(testEnvEntity, httpHeaders, HttpMethod.PUT, updatesUri);
        }
        
        void setPendingRequestEntity() {
            this.requestEntity = buildPendingRequestEntity();
        }
        
        void setRunningRequestEntity(String loadScenarioName) {
            RequestEntity<TestEnvironmentEntity> requestEntity = buildPendingRequestEntity();
            requestEntity.getBody().setStatus(TestEnvironmentStatus.RUNNING);
            requestEntity.getBody().setRunningLoadScenario(new LoadScenarioEntity(loadScenarioName));
    
            this.requestEntity = requestEntity;
        }

        @Override
        public void run() {
            do {
                try {
                    updateStatus();
                    TimeUnit.SECONDS.sleep(statusReportIntervalSeconds);
                } catch (InterruptedException e) {
                    standBy = false;
                }
            } while (standBy);
        }
        
        private void updateStatus() throws InterruptedException {
            LOGGER.debug("Performing status update...");
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
                LOGGER.info("PUT request sent to {} with body {}.", requestEntity.getUrl(), requestEntity.getBody());
                tryToOfferNextConfigToExecute(responseEntity);
            } catch (HttpServerErrorException | ResourceAccessException e) {
                LOGGER.warn("Server error during update by url '{}'", requestEntity.getUrl(), e);
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    reRegister();
                } else {
                    LOGGER.error("Finishing work due to HTTP client exception.", e);
                    standBy = false;
                }
            }
        }
        
        private void reRegister() throws InterruptedException {
            this.sessionCookie = doRegister();
            if (requestEntity.getBody().getStatus() == TestEnvironmentStatus.PENDING) {
                setPendingRequestEntity();
            } else {
                setRunningRequestEntity(requestEntity.getBody().getRunningLoadScenario().getLoadScenarioId());
            }
        }
        
        private void tryToOfferNextConfigToExecute(ResponseEntity<String> responseEntity) throws InterruptedException {
            if (requestEntity.getBody().getStatus() != TestEnvironmentStatus.PENDING) {
                return;
            }
    
            JaasResponse jaasResponse = extractJaasResponse(responseEntity);
            if (jaasResponse == null) {
                return;
            }
            
            if (!nextConfigToExecute.offer(jaasResponse, 1, TimeUnit.MINUTES)) {
                LOGGER.warn("Didn't manage to put next config name into a queue");
            }
        }
        
        private JaasResponse extractJaasResponse(ResponseEntity<String> responseEntity) {
            String executionId = extractHeader(responseEntity, TestEnvUtils.EXECUTION_ID_HEADER);
            if (executionId == null) {
                return null;
            }
    
            JaasResponse jaasResponse = new JaasResponse();
            jaasResponse.executionId = executionId;
            
            return jaasResponse;
        }
    
        private String extractHeader(final ResponseEntity<String> responseEntity, final String headerName) {
            List<String> configNameHeaders = responseEntity.getHeaders().get(headerName);
            
            if (CollectionUtils.isEmpty(configNameHeaders)) {
                return null;
            }
    
            if (configNameHeaders.size() > 1) {
                LOGGER.warn("There are more then 1 {} header value in response. Using the 1st one", headerName);
            }
            
            return configNameHeaders.get(0);
        }
    }
    
    public static class JaasResponse {
        private String executionId;
    
        public String getExecutionId() {
            return executionId;
        }
    }
}
