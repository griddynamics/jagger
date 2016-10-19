package com.griddynamics.jagger.user.test.configurations;

import lombok.Builder;
import lombok.ToString;

/**
 * The class contains description of a test.
 * Here a user can set end points and queries for a test.
 */
@Builder
@ToString
public class JTestDescription {

    private String name;
    private String version;
    private String description;
    private Iterable endpoints;
    private Iterable queries;

}
