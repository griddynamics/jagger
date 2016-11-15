package com.griddynamics.jagger.jaas.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.griddynamics.jagger.jaas.exceptions.ResourceAlreadyExistsException;
import com.griddynamics.jagger.jaas.exceptions.ResourceNotFoundException;
import com.griddynamics.jagger.jaas.exceptions.TestEnvironmentInvalidIdException;
import com.griddynamics.jagger.jaas.exceptions.TestEnvironmentSessionNotFoundException;
import com.griddynamics.jagger.jaas.exceptions.WrongTestEnvironmentStatusException;
import com.griddynamics.jagger.jaas.service.TestEnvironmentService;
import com.griddynamics.jagger.jaas.storage.model.TestEnvUtils;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/envs")
@Api(description = "This is the API for Jagger Test Environments. It provides endpoints for reading, creating and updating Test Environments. "
        + "Deleting is performed automatically by cleaning job. Expiration time of environments is set by property 'environments.ttl.minutes'.")
public class TestEnvironmentRestController extends AbstractController {

    private static final String ENV_ID_PATTERN = "^[a-zA-Z0-9\\._\\-]{1,249}$";

    @Value("${environments.ttl.minutes}")
    private int environmentsTtlMinutes;

    private TestEnvironmentService testEnvService;

    @Autowired
    public TestEnvironmentRestController(TestEnvironmentService testEnvironmentService) {
        this.testEnvService = testEnvironmentService;
    }

    @GetMapping(value = "/{envId}", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(response = TestEnvironmentEntity.class, value = "Retrieves Test Environment by envId.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Test Environment is found.", responseHeaders = @ResponseHeader(name = TestEnvUtils.EXPIRES_HEADER,
                    description = "This header stores expiration time of Test Environment.", response = ZonedDateTime.class)),
            @ApiResponse(code = 404, message = "Test Environment with provided envId is not found.")})
    public ResponseEntity<TestEnvironmentEntity> getTestEnvironment(@PathVariable String envId, HttpServletResponse response) {
        ResponseEntity<TestEnvironmentEntity> responseEntity = produceGetResponse(testEnvService, function -> testEnvService.read(envId));
        if (responseEntity.getStatusCode() == OK)
            setExpiresHeader(response, responseEntity.getBody());
        return responseEntity;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ApiOperation(response = TestEnvironmentEntity.class, responseContainer = "List", value = "Retrieves all Test Environments.")
    public ResponseEntity<List<TestEnvironmentEntity>> getTestEnvironments() {
        return produceGetResponse(testEnvService, function -> testEnvService.readAll());
    }

    @PutMapping(value = "/{envId}", consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates Test Environment by envId.", notes = "This operation can be performed only if '"
            + TestEnvUtils.SESSION_COOKIE + "' cookie is "
            + "present and valid. To obtain this cookie POST to /envs must be performed firstly. "
            + "This cookie is valid only for Test Environment with "
            + "envId which was specified in POST request body.")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Update is successful."),
            @ApiResponse(code = 404, message = "Test Environment with provided envId or sessionId is not found."),
            @ApiResponse(code = 400, message = TestEnvUtils.SESSION_COOKIE + " cookie is not present.")})
    public ResponseEntity<?> updateTestEnvironment(@CookieValue(TestEnvUtils.SESSION_COOKIE) String sessionId,
                                                   @PathVariable String envId,
                                                   @RequestBody TestEnvironmentEntity testEnv,
                                                   HttpServletResponse response) {
        if (!testEnvService.exists(envId))
            throw ResourceNotFoundException.getTestEnvResourceNfe();

        if (!testEnvService.existsWithSessionId(envId, sessionId))
            throw new TestEnvironmentSessionNotFoundException(envId, sessionId);

        testEnv.setEnvironmentId(envId);
        TestEnvironmentEntity updated = testEnvService.update(testEnv);
        response.addCookie(getSessionCookie(updated));
        return ResponseEntity.accepted().build();
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates Test Environment.",
                  notes = "If operation successful, '" + TestEnvUtils.SESSION_COOKIE
                          + "' cookie is returned with response, "
            + "which is required for PUT request. This cookie is valid only for Test Environment with envId which was specified in request body.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Creation is successful."),
            @ApiResponse(code = 400, message = "envId doesn't match " + ENV_ID_PATTERN + " OR status is inconsistent with runningTestSuite."),
            @ApiResponse(code = 409, message = "Test Environment with provided envId already exists.")})
    public ResponseEntity<?> createTestEnvironment(@RequestBody TestEnvironmentEntity testEnv, HttpServletResponse response) {
        validateTestEnv(testEnv);
        if (testEnvService.exists(testEnv.getEnvironmentId()))
            throw new ResourceAlreadyExistsException("Test Environment", testEnv.getEnvironmentId());

        TestEnvironmentEntity created = testEnvService.create(testEnv);
        response.addCookie(getSessionCookie(created));
        return ResponseEntity.created(fromCurrentRequest().path("/{envId}")
                                                          .buildAndExpand(testEnv.getEnvironmentId()).toUri()).build();
    }

    private void validateTestEnv(TestEnvironmentEntity testEnv) {
        String envId = testEnv.getEnvironmentId();
        Pattern envIdPattern = Pattern.compile(ENV_ID_PATTERN);
        Matcher matcher = envIdPattern.matcher(envId);
        if (!matcher.matches())
            throw new TestEnvironmentInvalidIdException(envId, envIdPattern);

        if (testEnv.getRunningTestSuite() == null && testEnv.getStatus() == TestEnvironmentEntity.TestEnvironmentStatus.RUNNING
                || testEnv.getRunningTestSuite() != null && testEnv.getStatus() == TestEnvironmentEntity.TestEnvironmentStatus.PENDING)
            throw new WrongTestEnvironmentStatusException(testEnv.getStatus(), testEnv.getRunningTestSuite());
    }

    private void setExpiresHeader(HttpServletResponse response, TestEnvironmentEntity testEnv) {
        response.addHeader(TestEnvUtils.EXPIRES_HEADER, getFormattedExpirationDate(testEnv));
    }

    private Cookie getSessionCookie(TestEnvironmentEntity testEnv) {
        Cookie cookie = new Cookie(TestEnvUtils.SESSION_COOKIE, testEnv.getSessionId());
        cookie.setMaxAge(environmentsTtlMinutes * 60);
        cookie.setPath("/");
    
        return cookie;
    }

    private String getFormattedExpirationDate(TestEnvironmentEntity testEnv) {
        return TestEnvUtils.convertToExpiresValue(testEnv.getExpirationTimestamp());
    }
}
