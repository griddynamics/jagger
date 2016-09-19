package com.griddynamics.jagger.master;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides access to session id from Spring Context.
 * Date: 09/09/16
 */
public abstract class SessionInfoAware {
    
    private SessionInfoProvider sessionInfoProvider;
    
    public SessionIdProvider getSessionInfoProvider() {
        return sessionInfoProvider;
    }
    
    @Autowired
    public void setSessionInfoProvider(SessionInfoProvider sessionInfoProvider) {
        this.sessionInfoProvider = sessionInfoProvider;
    }
    
    public String getSessionId() {
        return sessionInfoProvider.getSessionId();
    }
}
