package com.griddynamics.jagger.engine.e1.collector.testsuite;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.collector.test.AbstractTestInfo;
import com.griddynamics.jagger.util.GeneralNodeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Class, which contains some information about test suite execution
 *
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 */
public class TestSuiteInfo extends AbstractTestInfo {
    private String sessionId;
    private Map<NodeId, GeneralNodeInfo> generalNodeInfo = new HashMap<>();
    private Long startTime;
    private Long endTime;
    
    public TestSuiteInfo(String sessionId, Map<NodeId, GeneralNodeInfo> generalNodeInfo) {
        this.sessionId = sessionId;
        this.generalNodeInfo = generalNodeInfo;
    }
    
    @Override
    public Long getStartTime() {
        return startTime;
    }
    
    @Override
    public Long getEndTime() {
        return endTime;
    }
    
    /**
     * Returns session id
     */
    @Override
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public void setStartTime() {
        startTime = System.currentTimeMillis();
    }
    
    public void setEndTime() {
        endTime = System.currentTimeMillis();
    }
    
    /**
     * Returns information about nodes where jagger kernels and agents are running
     */
    public Map<NodeId, GeneralNodeInfo> getGeneralNodeInfo() { return generalNodeInfo; }
    
    public void setGeneralNodeInfo(Map<NodeId, GeneralNodeInfo> generalNodeInfo) {
        this.generalNodeInfo = generalNodeInfo;
    }
}
