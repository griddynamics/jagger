package com.griddynamics.jagger.invoker.scenario;

/**
 *  Result of user scenario step ({@link JHttpUserScenarioStep}) invocation
 *
 *  @ingroup Main_Http_User_Scenario_group
 */
public class JHttpUserScenarioStepInvocationResult {
    private final String stepId;
    private final String stepDisplayName;
    private final Number latency;
    private final Boolean succeeded;

    public JHttpUserScenarioStepInvocationResult(String stepId, String stepDisplayName, Number latency, Boolean succeeded) {
        this.stepId = stepId;
        this.stepDisplayName = stepDisplayName;
        this.latency = latency;
        this.succeeded = succeeded;
    }

    public String getStepId() {
        return stepId;
    }

    public String getStepDisplayName() {
        return stepDisplayName;
    }

    public Number getLatency() {
        return latency;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }
}
