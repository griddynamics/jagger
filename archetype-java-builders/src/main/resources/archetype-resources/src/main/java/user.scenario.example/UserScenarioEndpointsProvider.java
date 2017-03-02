package ${package}.user.scenario.example;


import com.griddynamics.jagger.invoker.scenario.JHttpScenarioGlobalContext;
import com.griddynamics.jagger.invoker.scenario.JHttpUserScenario;
import com.griddynamics.jagger.invoker.scenario.JHttpUserScenarioStep;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// begin: following section is used for docu generation - User scenario provider

/**
 * Example of user scenario provider
 * User scenario - sequence of the test steps executed by virtual user. (E.g. sequence of http requests for singned-in user)
 */
public class UserScenarioEndpointsProvider implements Iterable {
    public static final String SCENARIO_ID = "my-user-scenario";
    public static final String STEP_1_ID = "step321";
    public static final String STEP_2_ID = "step2";
    public static final String STEP_3_ID = "step3";

    private static Logger log = LoggerFactory.getLogger(UserScenarioEndpointsProvider.class);

    private List<JHttpUserScenario> userScenarios = new ArrayList<>();

    public UserScenarioEndpointsProvider() {

        // First scenario example
        JHttpUserScenario userScenario = new JHttpUserScenario(SCENARIO_ID, "My User Scenario");

        userScenario
                .withScenarioGlobalContext(new JHttpScenarioGlobalContext()
                        .withGlobalEndpoint(new JHttpEndpoint("https://httpbin.org/")))
                .addStep(JHttpUserScenarioStep.builder(STEP_1_ID)
                        .withDisplayName("Step #321")
                        .withWaitAfterExecutionInSeconds(3)
                        .build())
                .addStep(JHttpUserScenarioStep.builder(STEP_2_ID)
                        .withDisplayName("Step #2")
                        .withWaitAfterExecutionInSeconds(3)
                        .withQuery(new JHttpQuery().get().path("/get"))
                        // global context can be changed before or after step execution.
                        // E.g. we can add headers to all following requests in this scenario
                        .withPreProcessGlobalContextFunction((prevStep, context) -> {
                            context.withGlobalHeaders(prevStep.getResponse().getHeaders());
                        })
                        // you can decide if step was successful or not
                        .withPostProcessFunction(response -> {
                            if (response.getStatus().is2xxSuccessful()) {
                                log.info("Step 2 is successful!");
                                return true;
                            }
                            return false;
                        })
                        .build())
                .addStep(JHttpUserScenarioStep.builder(STEP_3_ID)
                        .withDisplayName("Step #3")
                        .withWaitAfterExecutionInSeconds(3)
                        .withQuery(new JHttpQuery().get().path("/response-headers?key=val"))
                        // You can modify this step, based of the results of the previous one
                        // Here we are setting endpoint and query values
                        .withPreProcessFunction((prevStep, currentStep) -> {
                            currentStep.setEndpoint(new JHttpEndpoint("http://www.scala-lang.org"));
                            currentStep.setQuery(new JHttpQuery().get().path("/"));
                        })
                        .build());

        // Scenario example with basic authentication
        JHttpUserScenario userScenarioBasicAuthAuto = new JHttpUserScenario("my-user-scenario-basic-auth",
                "Basic Auth User Scenario");

        userScenarioBasicAuthAuto
                // All requests in this scenario will use following basic authentication
                .withScenarioGlobalContext(new JHttpScenarioGlobalContext()
                        .withGlobalEndpoint(new JHttpEndpoint("https://httpbin.org/"))
                        .withBasicAuth("userName", "userPassword")
                )
                .addStep(JHttpUserScenarioStep.builder("basic_auto_1")
                        .withQuery(new JHttpQuery().get().path("/basic-auth/userName/userPassword"))
                        .withDisplayName("Auth pass")
                        .withWaitAfterExecutionInSeconds(3)
                        .build())
                .addStep(JHttpUserScenarioStep.builder("basic_auto_2")
                        .withQuery(new JHttpQuery().get().path("/basic-auth/userName/userPassword"))
                        .withDisplayName("Auth pass (validation)")
                        .withWaitAfterExecutionInSeconds(3)
                        .withPostProcessFunction(response -> {
                            if (response.getStatus().value() != 200) {
                                log.error("Unexpected status returned. Expected: 200. Returned: {}", response.getStatus().value());
                                return false;
                            }
                            return true;
                        })
                        .build())
                .addStep(JHttpUserScenarioStep.builder("basic_auto_3")
                        .withQuery(new JHttpQuery().get().path("/basic-auth/userName/userPassword"))
                        .withDisplayName("Auth fail (validation)")
                        .withWaitAfterExecutionInSeconds(3)
                        .withPreProcessFunction((prevStep, currentStep) -> {
                            // Reset all headers => auth will fail
                            currentStep.getQuery().headers(null);
                        })
                        .withPostProcessFunction(response -> {
                            if (response.getStatus().value() != 401) {
                                log.error("Unexpected status returned. Expected: 401. Returned: {}", response.getStatus().value());
                                return false;
                            }
                            return true;
                        })
                        .build());

        userScenarios.add(userScenario);
        userScenarios.add(userScenarioBasicAuthAuto);
    }

    @Override
    public Iterator<JHttpUserScenario> iterator() {
        return userScenarios.iterator();
    }
}
// end: following section is used for docu generation - User scenario provider
