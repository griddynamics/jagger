package com.griddynamics.jagger.master;

import com.griddynamics.jagger.jaas.storage.model.TestEnvUtils;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus;
import com.griddynamics.jagger.jaas.storage.model.TestSuiteEntity;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
 * Handles Jagger Master node to JaaS communication -
 * initial registration and further status updates with commands parsing from JaaS response.
 */
public class MasterToJaasCoordinator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterToJaasCoordinator.class);
    
    private volatile boolean standBy = true;
    private final RestTemplate restTemplate = new RestTemplate();
    private volatile String envId;
    private final URI jaasEndpoint;
    private final int statusReportIntervalSeconds;
    private final Set<String> availableConfigs;
    private boolean registered = false;
    private StatusExchangeThread statusExchangeThread;
    
    public MasterToJaasCoordinator(String envId, String jaasEndpoint, int statusReportIntervalSeconds,
                                   Set<String> availableConfigs) {
        this.envId = envId;
        try {
            this.jaasEndpoint = new URI(jaasEndpoint);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Incorrect JaaS endpoint", e);
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
    
    public void register() {
        final String sessionCookie = doRegister();
        statusExchangeThread = new StatusExchangeThread(sessionCookie);
        statusExchangeThread.start();
        registered = true;
    }
    
    public String awaitConfigToExecute() throws TerminateException {
        if (!registered) {
            throw new IllegalStateException("must be registered");
        }
        try {
            statusExchangeThread.setPendingRequestEntity();
            String configName;
            do {
                configName = statusExchangeThread.nextConfigToExecute.poll(1, TimeUnit.MINUTES);
                if (!standBy) {
                    throw new TerminateException("Master execution can't be proceeded");
                }
            } while (configName == null);
            statusExchangeThread.setRunningRequestEntity(configName);
            return configName;
        } catch (InterruptedException e) {
            statusExchangeThread.interrupt();
            throw new TerminateException("Master execution can't be proceeded");
        }
    }
    
    private String doRegister() {
        URI envsUri = UriComponentsBuilder.newInstance().uri(jaasEndpoint).path("/envs").build().toUri();
    
        TestEnvironmentEntity testEnvironmentEntity = buildTestEnvironmentEntityWith(TestEnvironmentStatus.PENDING);
        ResponseEntity<String> responseEntity = null;
        boolean posted = false;
        do {
            try {
                responseEntity = restTemplate.postForEntity(envsUri, testEnvironmentEntity, String.class);
                posted = true;
            } catch (HttpClientErrorException e) {
                if (!isEnvIdUnacceptable(e)) {
                    throw e;
                }
                envId = UUID.randomUUID().toString();
                LOGGER.warn("Changing env id from {} to {}", testEnvironmentEntity.getEnvironmentId(), envId);
                testEnvironmentEntity.setEnvironmentId(envId);
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
        testEnvironmentEntity.setTestSuites(availableConfigs.stream()
                                                            .map(TestSuiteEntity::new)
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
    
    private final class StatusExchangeThread extends Thread {
        
        private String sessionCookie;
    
        private final SynchronousQueue<String> nextConfigToExecute = new SynchronousQueue<>();
        
        private volatile RequestEntity<TestEnvironmentEntity> requestEntity;
    
        public StatusExchangeThread(String sessionCookie) {
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
        
        public void setPendingRequestEntity() {
            this.requestEntity = buildPendingRequestEntity();
        }
        
        public void setRunningRequestEntity(String configName) {
            RequestEntity<TestEnvironmentEntity> requestEntity = buildPendingRequestEntity();
            requestEntity.getBody().setStatus(TestEnvironmentStatus.RUNNING);
            requestEntity.getBody().setRunningTestSuite(new TestSuiteEntity(configName));
    
            this.requestEntity = requestEntity;
        }
    
        @Override
        public void run() {
            do {
                try {
                    updateStatus();
                    Thread.sleep(statusReportIntervalSeconds * 1000);
                } catch (InterruptedException e) {
                    standBy = false;
                }
            } while (standBy);
        }
        
        private void updateStatus() throws InterruptedException {
            LOGGER.debug("Performing status update...");
            try {
                ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
                tryToOfferNextConfigToExecute(responseEntity);
            } catch (HttpServerErrorException e) {
                LOGGER.warn("Server error during update", e);
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    reRegister();
                } else {
                    LOGGER.error("Finishing work due to HTTP client exception.", e);
                    standBy = false;
                }
            }
        }
        
        private void reRegister() {
            this.sessionCookie = doRegister();
            if (requestEntity.getBody().getStatus() == TestEnvironmentStatus.PENDING) {
                setPendingRequestEntity();
            } else {
                setRunningRequestEntity(requestEntity.getBody().getRunningTestSuite().getTestSuiteId());
            }
        }
        
        private void tryToOfferNextConfigToExecute(ResponseEntity<String> responseEntity) throws InterruptedException {
            if (requestEntity.getBody().getStatus() != TestEnvironmentStatus.PENDING) {
                return;
            }
            String configName = getNextConfigToExecute(responseEntity);
            if (configName == null) {
                return;
            }
            if (!nextConfigToExecute.offer(configName, 1, TimeUnit.MINUTES)) {
                LOGGER.warn("Didn't manage to put next config name into a queue");
            }
        }
        
        private String getNextConfigToExecute(ResponseEntity<String> responseEntity) {
            List<String> configNameHeaders = responseEntity.getHeaders().get(TestEnvUtils.CONFIG_NAME_HEADER);
            
            if (CollectionUtils.isEmpty(configNameHeaders)) {
                return null;
            }
    
            if (configNameHeaders.size() > 1) {
                LOGGER.warn(
                        "There are more then 1 {} header value in response. Using the 1st one",
                        TestEnvUtils.CONFIG_NAME_HEADER
                );
            }
            
            String configName = configNameHeaders.get(0);
            if (!availableConfigs.contains(configName)) {
                LOGGER.warn(
                        "Received config name '{}' in not among available ones: {}\nCan not execute it", configName,
                        availableConfigs
                );
                configName = null;
            }
            
            return configName;
        }
    }
}
