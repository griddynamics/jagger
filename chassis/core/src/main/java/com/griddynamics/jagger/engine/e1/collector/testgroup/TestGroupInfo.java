package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.master.CompositeTask;

/** Class, which contains some information about test-group execution
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 * */
public class TestGroupInfo {
    private CompositeTask testGroup;
    private long duration;

    public TestGroupInfo(){
    }

    public TestGroupInfo(CompositeTask testGroup){
        this.testGroup = testGroup;
    }

    /** Returns current test-group
     * @author Gribov Kirill
     * @n
     * */
    public CompositeTask getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(CompositeTask testGroup) {
        this.testGroup = testGroup;
    }

    /** Returns test-group duration
     * @author Gribov Kirill
     * @n
     * */
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
