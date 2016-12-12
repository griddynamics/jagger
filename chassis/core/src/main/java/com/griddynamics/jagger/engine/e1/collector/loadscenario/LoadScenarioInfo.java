package com.griddynamics.jagger.engine.e1.collector.loadscenario;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.util.GeneralNodeInfo;

import java.util.HashMap;
import java.util.Map;

/** Class, which contains some information about test suite execution
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 * */
public class LoadScenarioInfo {
    private String sessionId;
    private Map<NodeId,GeneralNodeInfo> generalNodeInfo = new HashMap<NodeId,GeneralNodeInfo>();

    private long duration;

    public LoadScenarioInfo(String sessionId, Map<NodeId,GeneralNodeInfo> generalNodeInfo){
        this.sessionId = sessionId;
        this.generalNodeInfo = generalNodeInfo;
    }

    /** Returns test-suite duration */
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    /** Returns session id */
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /** Returns information about nodes where jagger kernels and agents are running */
    public Map<NodeId, GeneralNodeInfo> getGeneralNodeInfo() { return generalNodeInfo; }

    public void setGeneralNodeInfo(Map<NodeId, GeneralNodeInfo> generalNodeInfo) { this.generalNodeInfo = generalNodeInfo; }
}
