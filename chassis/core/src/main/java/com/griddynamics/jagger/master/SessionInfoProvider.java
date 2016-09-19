package com.griddynamics.jagger.master;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by Andrey Badaev (andrey.badaev@kohls.com)
 * Date: 08/09/16
 */
@Component
public class SessionInfoProvider implements SessionIdProvider {
    
    private volatile long startTime;
    private volatile long endTime;
    private volatile Integer kernelsCount;
    
    
    @Resource(name = "sessionIdProvider")
    private SessionIdProvider sessionIdProvider;
    
    @Override
    public String getSessionId() {
        return sessionIdProvider.getSessionId();
    }
    
    @Override
    public String getSessionName() {
        return sessionIdProvider.getSessionName();
    }
    
    @Override
    public String getSessionComment() {
        return sessionIdProvider.getSessionComment();
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public Integer getKernelsCount() {
        return kernelsCount;
    }
    
    public void setKernelsCount(Integer kernelsCount) {
        this.kernelsCount = kernelsCount;
    }
}
