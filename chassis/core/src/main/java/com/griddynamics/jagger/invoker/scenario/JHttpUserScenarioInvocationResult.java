package com.griddynamics.jagger.invoker.scenario;

import java.util.List;

/**
 * Result of user scenario invocation provided by {@link JHttpUserScenarioInvoker}
 *
 * @ingroup Main_Http_User_Scenario_group
 */
public class JHttpUserScenarioInvocationResult {
    private final List<JHttpUserScenarioStepInvocationResult> stepInvocationResults;
    private final String scenarioId;
    private final String scenarioDisplayName;
    private final Boolean succeeded;

    public JHttpUserScenarioInvocationResult(List<JHttpUserScenarioStepInvocationResult> stepInvocationResults, String scenarioId, String scenarioDisplayName, Boolean succeeded) {
        this.stepInvocationResults = stepInvocationResults;
        this.scenarioId = scenarioId;
        this.scenarioDisplayName = scenarioDisplayName;
        this.succeeded = succeeded;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getScenarioDisplayName() {
        return scenarioDisplayName;
    }

    public List<JHttpUserScenarioStepInvocationResult> getStepInvocationResults() {
        return stepInvocationResults;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }
}
