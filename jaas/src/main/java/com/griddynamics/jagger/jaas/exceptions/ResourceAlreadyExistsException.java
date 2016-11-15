package com.griddynamics.jagger.jaas.exceptions;

import static java.lang.String.format;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resourceName, String resourceId) {
        super(format("%s resource with id '%s' already exists.", resourceName, resourceId));
    }
}
