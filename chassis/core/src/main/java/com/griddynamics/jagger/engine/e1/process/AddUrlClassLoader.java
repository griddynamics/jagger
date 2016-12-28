package com.griddynamics.jagger.engine.e1.process;

import com.griddynamics.jagger.coordinator.Command;

/**
 * A command to add and use a custom url class loader for a provided classes url.
 * @n
 * Created by Andrey Badaev
 * Date: 26/12/16
 */
public class AddUrlClassLoader implements Command<Boolean> {
    
    private String classesUrl;
    private String sessionId;
    
    public AddUrlClassLoader(final String sessionId, final String classesUrl) {
        this.sessionId = sessionId;
        this.classesUrl = classesUrl;
    }
    
    public static AddUrlClassLoader create(final String sessionId, final String classesUrl) {
        return new AddUrlClassLoader(sessionId, classesUrl);
    }
    
    public String getClassesUrl() {
        return classesUrl;
    }
    
    public void setClassesUrl(String classesUrl) {
        this.classesUrl = classesUrl;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public String getSessionId() {
        return sessionId;
    }
}
