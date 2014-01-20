package com.griddynamics.jagger.engine.e1.collector.test;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;

/** Class, which contains some information about test execution
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 * */
public class TestInfo {
    private WorkloadTask test;
    private String sessionId;

    private int threads;
    private int samples;
    private int startedSamples;
    private long duration;


    public TestInfo(){
    }

    public TestInfo(WorkloadTask test, String sessionId){
        this.test = test;
        this.sessionId = sessionId;
    }

    /** Returns current test */
    public WorkloadTask getTest() {
        return test;
    }

    public void setTest(WorkloadTask test) {
        this.test = test;
    }

    /** Returns current number of threads, used by Jagger to generate load */
    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    /** Returns total number of completed samples (all responses from SUT) */
    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    /** Returns total number of started samples (invokes) */
    public int getStartedSamples() {
        return startedSamples;
    }

    public void setStartedSamples(int startedSamples) {
        this.startedSamples = startedSamples;
    }

    /** Returns test duration */
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
