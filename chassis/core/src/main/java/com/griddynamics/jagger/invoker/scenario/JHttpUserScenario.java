package com.griddynamics.jagger.invoker.scenario;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

/**
 * This class is a representation of user scenario - sequence of the related http requests
 * Can be invoked by {@link JHttpUserScenarioInvoker}.
 * User can create as many steps ({@link JHttpUserScenarioStep}) as needed and they will be invoked as one invocation.
 * If any step fails whole scenario invacation will be marked as failed.
 *
 * @see JHttpUserScenarioInvoker
 * @see JHttpUserScenarioInvocationListener
 *
 * @ingroup Main_Http_User_Scenario_group
 */
public class JHttpUserScenario {
    private Integer stepsCounter = 0;
    private final String scenarioId;
    private final String scenarioDisplayName;
    private List<JHttpUserScenarioStep> userScenarioSteps = new ArrayList<>();
    private JHttpScenarioGlobalContext scenarioGlobalContext;

    public JHttpUserScenario(String scenarioId, String scenarioDisplayName) {
        this.scenarioId = scenarioId;
        this.scenarioDisplayName = scenarioDisplayName;
    }

    public JHttpUserScenario withScenarioGlobalContext(JHttpScenarioGlobalContext scenarioGlobalContext) {
        this.scenarioGlobalContext = scenarioGlobalContext;
        return this;
    }

    public JHttpUserScenario addStep (JHttpUserScenarioStep userScenarioStep) {
        if (!isStepIdUnique(userScenarioStep.getStepId())) {
            throw new IllegalArgumentException(format("Step id '%s' is not unique!", userScenarioStep.getStepId()));
        }
        stepsCounter++;
        userScenarioStep.setStepNumber(stepsCounter);
        userScenarioSteps.add(userScenarioStep);
        return this;
    }

    private boolean isStepIdUnique(String id) {
        return userScenarioSteps.stream().map(JHttpUserScenarioStep::getStepId).noneMatch(stepId -> stepId.equals(id));
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getScenarioDisplayName() {
        return scenarioDisplayName;
    }

    public List<JHttpUserScenarioStep> getUserScenarioSteps() {
        return userScenarioSteps;
    }

    public JHttpUserScenarioStep getUserScenarioStep(int index) {
        return userScenarioSteps.get(index);
    }

    public JHttpScenarioGlobalContext getScenarioGlobalContext() {
        return scenarioGlobalContext;
    }
}
