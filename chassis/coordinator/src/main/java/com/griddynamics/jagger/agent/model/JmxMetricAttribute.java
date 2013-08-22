package com.griddynamics.jagger.agent.model;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 8/20/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class JmxMetricAttribute {
    private String name;
    private boolean cumulative;
    private boolean rated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCumulative() {
        return cumulative;
    }

    public void setCumulative(boolean cumulative) {
        this.cumulative = cumulative;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }
}
