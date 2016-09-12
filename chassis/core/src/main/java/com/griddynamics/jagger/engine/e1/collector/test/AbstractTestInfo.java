package com.griddynamics.jagger.engine.e1.collector.test;

/**
 * Created by Andrey Badaev (andrey.badaev@kohls.com)
 * Date: 09/09/16
 */
public abstract class AbstractTestInfo extends AbstractTestListener<AbstractTestInfo> {
    
    public abstract Long getStartTime();
    public abstract Long getEndTime();
    public abstract String getSessionId();
    
    public long getDuration() {
        Long startTime = getStartTime();
        if (startTime == null) {
            return 0L;
        }
        Long endTime = getEndTime();
        if (endTime == null) {
            return System.currentTimeMillis() - startTime;
        }
        
        return endTime - startTime;
    }
}
