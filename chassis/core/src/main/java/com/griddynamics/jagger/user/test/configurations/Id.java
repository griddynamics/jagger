package com.griddynamics.jagger.user.test.configurations;

import org.springframework.util.StringUtils;

/**
 * Represents an ID for Jagger test description entities.
 * <p>
 * Created by Andrey Badaev
 * Date: 16/11/16
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
