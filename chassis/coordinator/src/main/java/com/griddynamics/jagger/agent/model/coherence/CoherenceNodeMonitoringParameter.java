package com.griddynamics.jagger.agent.model.coherence;

/**
 * @author Vladimir Kondrashchenko
 */
public class CoherenceNodeMonitoringParameter extends AbstractCoherenceMonitoringParameter {

    @Override
    public String getDescription() {
        return new StringBuilder("node").append(coherenceNode.getId()).append(":").append(metricAttributeName).toString();
    }

    @Override
    public String getReportingGroupKey() {
        return "Coherence::" + metricAttributeName;

    }


}
