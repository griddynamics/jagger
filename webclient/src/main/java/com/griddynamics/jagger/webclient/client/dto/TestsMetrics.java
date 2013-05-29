package com.griddynamics.jagger.webclient.client.dto;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.05.13
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */

public class TestsMetrics{

    private String testName;
    private Set<String> metrics;
    private Set<String> trends;

    public TestsMetrics(String testName, Set<String> metrics, Set<String> trends){
        this.testName = testName;
        this.metrics = metrics;
        this.trends = trends;
    }

    public String getTestName() {
        return testName;
    }

    public Set<String> getMetrics() {
        return metrics;
    }

    public Set<String> getTrends() {
        return trends;
    }

    public void setTrends(Set<String> trends) {
        this.trends = trends;
    }
}
