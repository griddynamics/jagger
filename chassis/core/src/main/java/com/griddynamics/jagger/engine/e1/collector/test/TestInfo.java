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
    private int threads;
    private int samples;
    private int startedSamples;
    private long duration;


    public TestInfo(){
    }

    public TestInfo(WorkloadTask test){
        this.test = test;
    }

    /** Returns current test
     * @author Gribov Kirill
     * @n
     * */
    public WorkloadTask getTest() {
        return test;
    }

    public void setTest(WorkloadTask test) {
        this.test = test;
    }

    /** Returns current number of threads, that Jagger use to generate load
     * @author Gribov Kirill
     * @n
     * */
    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    /** Returns total number of completed samples (all responses from SUT)
     * @author Gribov Kirill
     * @n
     * */
    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    /** Returns total number of started samples (invokes)
     * @author Gribov Kirill
     * @n
     * */
    public int getStartedSamples() {
        return startedSamples;
    }

    public void setStartedSamples(int startedSamples) {
        this.startedSamples = startedSamples;
    }

    /** Returns test duration
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
