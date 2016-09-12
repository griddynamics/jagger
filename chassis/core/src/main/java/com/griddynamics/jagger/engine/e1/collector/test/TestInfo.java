package com.griddynamics.jagger.engine.e1.collector.test;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;

/**
 * Contains information related to an instance
 * of {@link com.griddynamics.jagger.engine.e1.scenario.WorkloadTask} class.
 *
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 */
public class TestInfo extends TaskBasedTestInfo<WorkloadTask> {
    
    private int threads;
    private int samples;
    private int startedSamples;
    
    public TestInfo(WorkloadTask test) {
        super(test);
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
    
    @Override
    protected void doOnStop() {
        super.doOnStop();
        setThreads(0);
    }
}
