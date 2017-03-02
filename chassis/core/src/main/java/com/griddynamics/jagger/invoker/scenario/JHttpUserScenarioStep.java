package com.griddynamics.jagger.invoker.scenario;

import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Represents a single step (http request) in the user scenario ({@link JHttpUserScenario})
 *
 * @ingroup Main_Http_User_Scenario_group
 */
public class JHttpUserScenarioStep {
    private int stepNumber;
    private final String stepId; // mandatory parameter. required for metrics saving
    private JHttpEndpoint endpoint;
    private JHttpQuery query;
    private JHttpResponse response;
    private final long waitAfterExecutionInSeconds;
    private final String stepDisplayName; // should be equal to stepId if not defined
    private final BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer;
    private final BiConsumer<JHttpUserScenarioStep, JHttpScenarioGlobalContext> previousStepAndContextConsumer;
    private final Function<JHttpResponse, Boolean> responseFunction;

    /**
     * Can work with results from the previous step and update global scenario context.
     * @param previousStep previous execution step
     */
    public void preProcessGlobalContext(JHttpUserScenarioStep previousStep, JHttpScenarioGlobalContext scenarioContext) {
        if (previousStepAndContextConsumer != null)
            previousStepAndContextConsumer.accept(previousStep, scenarioContext);
    }

    /**
     * Can work with results from the previous step and set proper values for endpoint & query.
     * @param previousStep previous execution step
     */
    public void preProcess(JHttpUserScenarioStep previousStep) {
        if (previousAndCurrentStepConsumer != null) {
            previousAndCurrentStepConsumer.accept(previousStep, this);
        }
    }

    /** Can work with response.
     * @param response result of execution of request
     * @return result of responseFunction or true if responseFunction is null
     */
    public Boolean postProcess(JHttpResponse response) {
        this.response = response;
        if (responseFunction != null)
            return responseFunction.apply(JHttpResponse.copyOf(response));
        return true;
    }

    public void waitAfterExecution() {
        if (waitAfterExecutionInSeconds > 0) {
            try {
                TimeUnit.SECONDS.sleep(waitAfterExecutionInSeconds);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error occurred while waiting after execution", e);
            }
        }
    }

    private JHttpUserScenarioStep(Builder builder) {
        this.stepId = builder.stepId;
        this.endpoint = builder.endpoint;
        this.query = builder.query;
        this.waitAfterExecutionInSeconds = builder.waitAfterExecutionInSeconds;
        this.stepDisplayName = (builder.stepDisplayName == null) ? builder.stepId : builder.stepDisplayName;
        this.previousAndCurrentStepConsumer = builder.previousAndCurrentStepConsumer;
        this.responseFunction = builder.responseFunction;
        this.previousStepAndContextConsumer = builder.previousStepAndContextConsumer;
    }

    public static Builder builder(String id, JHttpEndpoint endpoint) {
        return new Builder(id, endpoint);
    }

    /** Use this method only if you set global endpoint!!!
     * @param id step stepId
     */
    public static Builder builder(String id) {
        return new Builder(id, null);
    }

    public static class Builder {
        private final String stepId;
        private final JHttpEndpoint endpoint;
        private JHttpQuery query;
        private long waitAfterExecutionInSeconds;
        private String stepDisplayName;
        private BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer;
        private BiConsumer<JHttpUserScenarioStep, JHttpScenarioGlobalContext> previousStepAndContextConsumer;
        private Function<JHttpResponse, Boolean> responseFunction;

        private Builder(String stepId, JHttpEndpoint endpoint) {
            this.stepId = stepId;
            this.endpoint = endpoint;
        }

        public Builder withQuery(JHttpQuery query) {
            this.query = query;
            return this;
        }

        public Builder withWaitAfterExecutionInSeconds(long waitAfterExecutionInSeconds) {
            this.waitAfterExecutionInSeconds = waitAfterExecutionInSeconds;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.stepDisplayName = displayName;
            return this;
        }

        public Builder withPreProcessGlobalContextFunction(BiConsumer<JHttpUserScenarioStep, JHttpScenarioGlobalContext> previousStepAndContextConsumer) {
            this.previousStepAndContextConsumer = previousStepAndContextConsumer;
            return this;
        }

        public Builder withPreProcessFunction(BiConsumer<JHttpUserScenarioStep, JHttpUserScenarioStep> previousAndCurrentStepConsumer) {
            this.previousAndCurrentStepConsumer = previousAndCurrentStepConsumer;
            return this;
        }

        public Builder withPostProcessFunction(Function<JHttpResponse, Boolean> responseFunction) {
            this.responseFunction = responseFunction;
            return this;
        }

        public JHttpUserScenarioStep build() {
            return new JHttpUserScenarioStep(this);
        }
    }

    public long getWaitAfterExecutionInSeconds() {
        return waitAfterExecutionInSeconds;
    }

    public JHttpEndpoint getEndpoint() {
        return endpoint;
    }

    /**
     * DON'T USE IT FOR PREVIOUS STEP IN preProcess()
     */
    public void setEndpoint(JHttpEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public JHttpQuery getQuery() {
        return query;
    }

    /**
     * DON'T USE IT FOR PREVIOUS STEP IN preProcess()
     */
    public void setQuery(JHttpQuery query) {
        this.query = query;
    }

    public JHttpResponse getResponse() {
        return JHttpResponse.copyOf(response);
    }

    public String getStepDisplayName() {
        return stepDisplayName;
    }

    public String getStepId() {
        return stepId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }
}
