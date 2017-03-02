package com.griddynamics.jagger.invoker.scenario;

import static com.google.common.collect.Lists.newArrayList;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import com.griddynamics.jagger.invoker.v2.SpringBasedHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This invoker is able to execute user scenarios ({@link JHttpUserScenario}) as one invocation.
 */
public class JHttpUserScenarioInvoker implements Invoker<Void, JHttpUserScenarioInvocationResult, JHttpUserScenario> {

    private final SpringBasedHttpClient httpClient = new SpringBasedHttpClient();

    private static Logger log = LoggerFactory.getLogger(JHttpUserScenarioInvoker.class);

    @Override
    public JHttpUserScenarioInvocationResult invoke(Void nothing, JHttpUserScenario scenario) throws InvocationException {
        Boolean scenarioSucceeded = true;
        JHttpUserScenarioStep previousStep = null;
        List<JHttpUserScenarioStepInvocationResult> stepInvocationResults = new ArrayList<>();

        JHttpScenarioGlobalContext localScenarioContext = scenario.getScenarioGlobalContext().copy();

        for (JHttpUserScenarioStep userScenarioStep : scenario.getUserScenarioSteps()) {

            // Pre process step: internal setup. Can be later overridden by the user
            // Basic auth
            if (localScenarioContext.getPassword() != null && localScenarioContext.getUserName() != null) {
                String value = Base64.getEncoder().encodeToString((localScenarioContext.getUserName() + ":" + localScenarioContext.getPassword()).getBytes());
                userScenarioStep.getQuery().header("Authorization", "Basic " + value);
            }

            // Pre process step: user actions executed before request
            userScenarioStep.preProcessGlobalContext(previousStep, localScenarioContext);
            userScenarioStep.preProcess(previousStep);

            // check endpoint for null
            if (localScenarioContext.getGlobalEndpoint() == null && userScenarioStep.getEndpoint() == null)
                throw new IllegalArgumentException("Endpoint must not be null! Please, set global endpoint or set endpoint for every step.");
            // use global endpoint for step if step has none
            if (userScenarioStep.getEndpoint() == null)
                userScenarioStep.setEndpoint(localScenarioContext.getGlobalEndpoint());
            // copy global headers and cookies to current step if query is present
            if (localScenarioContext.getGlobalHeaders() != null && userScenarioStep.getQuery() != null) {
                localScenarioContext.getGlobalHeaders().forEach((header, values) -> {
                    if (userScenarioStep.getQuery().getHeaders() != null && userScenarioStep.getQuery().getHeaders().containsKey(header)) {
                        List<String> newValues = newArrayList(userScenarioStep.getQuery().getHeaders().get(header));
                        newValues.addAll(values);
                        userScenarioStep.getQuery().getHeaders().put(header, newValues.stream().distinct().collect(
                                Collectors.toList()));
                    } else {
                        userScenarioStep.getQuery().header(header, values);
                    }
                });
            }

            log.info("Step {}: {}", userScenarioStep.getStepNumber(), userScenarioStep.getStepId());
            log.info("Endpoint: {}", userScenarioStep.getEndpoint());
            log.info("Query: {}", userScenarioStep.getQuery());

            // Request execution step
            long requestStartTime = System.nanoTime();
            JHttpResponse response = httpClient.execute(userScenarioStep.getEndpoint(), userScenarioStep.getQuery());
            Double requestTimeInMilliseconds = (System.nanoTime() - requestStartTime) / 1_000_000.0;

            //TODO: JFG-1121
            log.info("Response: {}", response);

            // Wait after execution if needed
            userScenarioStep.waitAfterExecution();

            // Post process step: executed after request. If returned false, scenario invocation stops.
            Boolean succeeded = userScenarioStep.postProcess(response);
            stepInvocationResults.add(new JHttpUserScenarioStepInvocationResult(userScenarioStep.getStepId(), userScenarioStep.getStepDisplayName(),
                    requestTimeInMilliseconds, succeeded));
            previousStep = userScenarioStep;

            if (!succeeded) {
                scenarioSucceeded = false;
                log.error("Step {} '{}' post process returned false! Stopping scenario (next steps won't be executed).",
                        userScenarioStep.getStepId(), userScenarioStep.getStepDisplayName());
                break;
            }
        }

        return new JHttpUserScenarioInvocationResult(stepInvocationResults, scenario.getScenarioId(), scenario.getScenarioDisplayName(), scenarioSucceeded);
    }
}
