package com.griddynamics.jagger.agent.model.coherence;

import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.agent.model.MonitoringParameterLevel;

import java.io.Serializable;

/**
 * @author Vladimir Kondrashchenko
 */
public abstract class AbstractCoherenceMonitoringParameter implements MonitoringParameter, Serializable {

    protected String metricAttributeName;

    protected CoherenceNode coherenceNode;

    protected boolean isCumulative;

    public void setMetricAttributeName(String metricAttributeName) {
        this.metricAttributeName = metricAttributeName;
    }

    public void setCoherenceNode(CoherenceNode coherenceNode) {
        this.coherenceNode = coherenceNode;
    }

    public void setCumulative(boolean cumulative) {
        isCumulative = cumulative;
    }

    public String getMetricAttributeName() {
        return metricAttributeName;
    }

    public CoherenceNode getCoherenceNode() {
        return coherenceNode;
    }

    @Override
    public boolean isCumulativeCounter() {
        return isCumulative;
    }

    @Override
    public MonitoringParameterLevel getLevel() {
        return MonitoringParameterLevel.SUT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCoherenceMonitoringParameter that = (AbstractCoherenceMonitoringParameter) o;

        if (coherenceNode != null ? !coherenceNode.equals(that.coherenceNode) : that.coherenceNode != null)
            return false;
        if (metricAttributeName != null ? !metricAttributeName.equals(that.metricAttributeName) : that.metricAttributeName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = metricAttributeName != null ? metricAttributeName.hashCode() : 0;
        result = 31 * result + (coherenceNode != null ? coherenceNode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AbstractCoherenceMonitoringParameter{" +
                "metricAttributeName='" + metricAttributeName + '\'' +
                ", coherenceNode=" + coherenceNode +
                ", isCumulative=" + isCumulative +
                '}';
    }
}
