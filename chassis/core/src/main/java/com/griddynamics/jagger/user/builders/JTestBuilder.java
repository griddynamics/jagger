package com.griddynamics.jagger.user.builders;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.limits.LimitSet;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.engine.e1.scenario.InfiniteTerminationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.IterationsOrDurationStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.RpsClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TerminateByDuration;
import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadClockConfiguration;
import com.griddynamics.jagger.user.TestConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * @author asokol
 *         created 10/14/16
 */
public class JTestBuilder {

    private String id;
    private String comment;
    private WorkloadClockConfiguration clockConfiguration;
    private TerminateStrategyConfiguration terminateStrategyConfiguration;
    private int number;
    private String testGroupName;
    private long startDelay;
    private List<Provider<TestListener>> listeners;
    private JTestDescriptionBuilder testDescription;
    private LimitSet limits;

    public JTestBuilder() {
        this.listeners = Collections.EMPTY_LIST;
        startDelay = -1;
        limits = null;
    }


    public JTestBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public JTestBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public JTestBuilder withTestDescription(JTestDescriptionBuilder description) {
        this.testDescription = description;
        return this;
    }

    public JTestBuilder withLoad(WorkloadClockConfiguration load) {
        this.clockConfiguration = load;
        return this;
    }

    /**
     * This type of load imitates an exact number of requests per second. Where request is invoke from Jagger.
     * By using attribute 'requestsPerSecond', you can configure a number of requests. Attribute 'maxLoadThreads'
     * says what is the maximum number of threads Jagger engine is allowed to create, to provide the requested load.
     * By default it equals 4000. You can change this value in property file. If attribute 'warmUpTimeInSeconds' is set,
     * load will increase from 0 to the value for this time.
     *
     * @return
     */
    // TODO: GD 10/14/16  Default value of max load threads is 50, not 4000.
    public JTestBuilder withLoadRps(double requestsPerSecond, int maxLoadThreads, long warmUpTimeInSeconds) {
        RpsClockConfiguration rpsConfiguration = new RpsClockConfiguration();
        rpsConfiguration.setWarmUpTime(warmUpTimeInSeconds);
        rpsConfiguration.setMaxThreadNumber(maxLoadThreads);
        rpsConfiguration.setValue(requestsPerSecond);
        this.clockConfiguration = rpsConfiguration;
        return this;
    }

    public JTestBuilder withLoadRps(long requestsPerSecond, int maxLoadThreads) {
        RpsClockConfiguration rpsConfiguration = new RpsClockConfiguration();
        rpsConfiguration.setMaxThreadNumber(maxLoadThreads);
        rpsConfiguration.setValue(requestsPerSecond);
        this.clockConfiguration = rpsConfiguration;
        return this;
    }

    public JTestBuilder withTermination(TerminateStrategyConfiguration termination) {
        this.terminateStrategyConfiguration = termination;
        return this;
    }


    /**
     * Termination describes how long load will be executed. Termination can be configured by element termination. There are some types of termination -
     * <p>
     * termination-duration Use this termination, when you would like to execute your load within a certain time.
     * <p>
     * <p>
     * termination-iterations This termination strategy is helpful, when you would like to execute an exact number of iterations. Attribute 'iterations' say how much iterations you would like to execute. With attribute 'maxDuration' you can configure maximum execution time of test. By default it equals 2 hours.
     * <p>
     * termination-background Test with such termination strategy will wait another tests in test-group to be stopped.
     */
    public JTestBuilder withTerminationDuration(int durationInSeconds) {
        TerminateByDuration configuration = new TerminateByDuration();
        configuration.setSeconds(durationInSeconds);
        this.terminateStrategyConfiguration = configuration;
        return this;
    }

    public JTestBuilder withTerminationIterations(int iterationCount, String maxDurationInSeconds) {
        IterationsOrDurationStrategyConfiguration configuration = new IterationsOrDurationStrategyConfiguration();
        configuration.setIterations(iterationCount);
        configuration.setDuration(maxDurationInSeconds);
        this.terminateStrategyConfiguration = configuration;
        return this;
    }

    public JTestBuilder withTerminationBackgroud() {
        InfiniteTerminationStrategyConfiguration configuration = new InfiniteTerminationStrategyConfiguration();
        this.terminateStrategyConfiguration = configuration;
        return this;
    }

    public TestConfiguration build() {
        TestConfiguration testConfiguration = new TestConfiguration();
        testConfiguration.setId(id);
        testConfiguration.setLoad(clockConfiguration);
        testConfiguration.setTerminateStrategy(terminateStrategyConfiguration);
        testConfiguration.setListeners(listeners);
        testConfiguration.setNumber(number);
        testConfiguration.setTestGroupName(testGroupName);
        testConfiguration.setStartDelay(startDelay);
        testConfiguration.setTestDescription(testDescription.build());
        testConfiguration.setLimits(limits);
        return testConfiguration;
    }


}
