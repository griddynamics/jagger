package com.griddynamics.jagger.jaas.exceptions;

/**
 * Produces {@link ResourceNotFoundException} instances for specific resources.
 */
public class ResourceNotFoundFactory {
    
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
}
