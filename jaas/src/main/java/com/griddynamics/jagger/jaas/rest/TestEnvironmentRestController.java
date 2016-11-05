package com.griddynamics.jagger.jaas.rest;

import com.griddynamics.jagger.jaas.exceptions.ResourceAlreadyExistsException;
import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import com.griddynamics.jagger.jaas.exceptions.TestEnvironmentInvalidIdException;
import com.griddynamics.jagger.jaas.exceptions.TestEnvironmentNoSessionException;
import com.griddynamics.jagger.jaas.exceptions.TestEnvironmentSessionNotFoundException;
import com.griddynamics.jagger.jaas.service.TestEnvironmentService;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.ofInstant;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(value = "/envs")
public class TestEnvironmentRestController extends AbstractController {

    private static final String ENVIRONMENT_SESSION_COOKIE = "Environment-Session";
    private static final String HEADER_DATE_FORMAT = "dd MMM yyyy HH:mm:ss O";
    private static final String ENV_ID_PATTERN = "^[a-zA-Z0-9\\._\\-]{1,249}$";

    @Value("${environments.ttl.minutes}")
    private int environmentsTtlMinutes;

    private TestEnvironmentService testEnvService;

    @Autowired
    public TestEnvironmentRestController(TestEnvironmentService testEnvironmentService) {
        this.testEnvService = testEnvironmentService;
    }

    @GetMapping(value = "/{envId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TestEnvironmentEntity> getTestEnvironment(@PathVariable String envId, HttpServletResponse response) {
        ResponseEntity<TestEnvironmentEntity> responseEntity = produceGetResponse(testEnvService, function -> testEnvService.read(envId));
        if (responseEntity.getStatusCode() == OK)
            setExpiresHeader(response, responseEntity.getBody());
        return responseEntity;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TestEnvironmentEntity>> getTestEnvironments() {
        return produceGetResponse(testEnvService, function -> testEnvService.readAll());
    }

    @PutMapping(value = "/{envId}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateTestEnvironment(@PathVariable String envId, @RequestBody TestEnvironmentEntity testEnv,
                                                   HttpServletRequest request, HttpServletResponse response) {
        if (!testEnvService.exists(envId))
            throw ResourceNotFoundException.getTestEnvResourceNfe();

        String sessionId = getSessionCookie(request);
        if (sessionId == null)
            throw new TestEnvironmentNoSessionException();
        if (!testEnvService.existsWithSessionId(envId, sessionId))
            throw new TestEnvironmentSessionNotFoundException(envId, sessionId);

        testEnv.setEnvironmentId(envId);
        TestEnvironmentEntity updated = testEnvService.update(testEnv);
        setExpiresHeader(response, updated);
        setSessionCookie(response, updated, environmentsTtlMinutes * 60);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createTestEnvironment(@RequestBody TestEnvironmentEntity testEnv, HttpServletResponse response) {
        validateTestEnvId(testEnv);
        if (testEnvService.exists(testEnv.getEnvironmentId()))
            throw new ResourceAlreadyExistsException("Test Environment", testEnv.getEnvironmentId());

        TestEnvironmentEntity created = testEnvService.create(testEnv);
        setExpiresHeader(response, created);
        setSessionCookie(response, created, environmentsTtlMinutes * 60);
        return ResponseEntity.created(fromCurrentRequest().path("/{envId}")
                .buildAndExpand(testEnv.getEnvironmentId()).toUri()).build();
    }

    private void validateTestEnvId(TestEnvironmentEntity testEnv) {
        String envId = testEnv.getEnvironmentId();
        Pattern envIdPattern = Pattern.compile(ENV_ID_PATTERN);
        Matcher matcher = envIdPattern.matcher(envId);
        if (!matcher.matches())
            throw new TestEnvironmentInvalidIdException(envId, envIdPattern);
    }

    private void setExpiresHeader(HttpServletResponse response, TestEnvironmentEntity testEnv) {
        response.addHeader("Environment-Expires", getFormattedExpirationDate(testEnv, HEADER_DATE_FORMAT));
    }

    private void setSessionCookie(HttpServletResponse response, TestEnvironmentEntity testEnv, int cookieMaxAgeSeconds) {
        Cookie cookie = new Cookie(ENVIRONMENT_SESSION_COOKIE, testEnv.getSessionId());
        cookie.setMaxAge(cookieMaxAgeSeconds);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ENVIRONMENT_SESSION_COOKIE)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getFormattedExpirationDate(TestEnvironmentEntity testEnv, String dateFormat) {
        return ofInstant(ofEpochMilli(testEnv.getExpirationTimestamp()), UTC).format(ofPattern(dateFormat));
    }
}
