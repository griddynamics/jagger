package com.griddynamics.jagger.engine.e1.process;

import com.griddynamics.jagger.coordinator.Command;

/**
 * Created by Andrey Badaev
 * Date: 26/12/16
 */
public class RemoveUrlClassLoader implements Command<Boolean> {
    
    private String sessionId;
    
    public RemoveUrlClassLoader(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public static RemoveUrlClassLoader create(String sessionId) {
        return new RemoveUrlClassLoader(sessionId);
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    @Override
    public String getSessionId() {
        return sessionId;
    }
}
