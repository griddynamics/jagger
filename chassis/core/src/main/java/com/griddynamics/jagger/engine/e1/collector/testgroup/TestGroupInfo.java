package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.master.CompositeTask;

/** Class, which contains information about test-group execution
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 *
 * @ingroup Main_Listeners_group
 */
public class TestGroupInfo {
    private CompositeTask testGroup;
    private String sessionId;

    private long duration;

    public TestGroupInfo(){
    }

    public TestGroupInfo(CompositeTask testGroup, String sessionId){
        this.testGroup = testGroup;
        this.sessionId = sessionId;
    }

    /** Returns full information about current test-group */
    public CompositeTask getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(CompositeTask testGroup) {
        this.testGroup = testGroup;
    }

    /** Returns test-group execution duration */
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
}
