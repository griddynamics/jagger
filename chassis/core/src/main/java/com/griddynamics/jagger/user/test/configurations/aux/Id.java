package com.griddynamics.jagger.user.test.configurations.aux;

import org.springframework.util.StringUtils;

/**
 * Represents an ID for Jagger load test description entities.
 *
 */
public final class Id {
    
    private final String id;
    
    public Id(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Id must be not-null and non-empty");
        }
        this.id = id;
    }
    
    public static Id of(String id) {
        return new Id(id);
    }
    
    public String value() {
        return id;
    }
}
