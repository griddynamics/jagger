package com.griddynamics.jagger.jaas.exceptions;

/**
 * Supposed to be thrown in case requested REST resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public static final ResourceNotFoundException DB_RESOURCE_NFE = new ResourceNotFoundException("Db");
    
    public static final ResourceNotFoundException PROJECT_RESOURCE_NFE = new ResourceNotFoundException("Project");
    
    public static final ResourceNotFoundException SESSION_RESOURCE_NFE = new ResourceNotFoundException("Session");
    
    public static ResourceNotFoundException getDbResourceNfe() {
        return DB_RESOURCE_NFE;
    }
    
    public static ResourceNotFoundException getProjectResourceNfe() {
        return PROJECT_RESOURCE_NFE;
    }
    
    public static ResourceNotFoundException getSessionResourceNfe() {
        return SESSION_RESOURCE_NFE;
    }
    
    public ResourceNotFoundException(String resourceName) {
        super(String.format("%s resource with provided id not found.", resourceName));
    }
}
