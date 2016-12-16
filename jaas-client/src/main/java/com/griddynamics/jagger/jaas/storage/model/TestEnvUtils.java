package com.griddynamics.jagger.jaas.storage.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Custom HTTP Cookies for {@link TestEnvironmentEntity}
 */
public final class TestEnvUtils {
    
    private TestEnvUtils() {
    }
    
    private static final String PREFIX = "Environment-";
    
    public static final String SESSION_COOKIE = PREFIX + "Session";
    public static final String EXPIRES_HEADER = PREFIX + "Expires";
    public static final String LOAD_SCENARIO_HEADER = PREFIX + "Load-Scenario-To-Execute";
    public static final String TEST_PROJECT_URL_HEADER = PREFIX + "Test-Project-Url";

    private static final DateTimeFormatter EXPIRES_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneId.of("GMT"));
    
    public static long convertFromExpiresValue(String value) {
        return Instant.from(EXPIRES_FORMATTER.parse(value)).toEpochMilli();
    }
    
    public static String convertToExpiresValue(long value) {
        return EXPIRES_FORMATTER.format(Instant.ofEpochMilli(value));
    }
}
