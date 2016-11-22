package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.aux.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @brief  Describes the execution sequence of the load tests during single run
 * @n
 * @par Details:
 * @details JLoadScenario describes the execution sequence of the load tests. Only single load scenario can be executed at a time @n
 * Project can contain multiple load scenarios @n
 * What load scenario to execute is defined by 'jagger.load.scenario.id.to.execute' property@n
 * You can set it via system properties or in the 'environment.properties' file @n
 * @n
 * See @ref section_writing_test_load_scenario for more details @n
 * @n
 * More information on the parameter of the test definition, you can find in the Builder documentation @n
 * @n
 * Code example:
 * @dontinclude  ExampleSimpleJLoadScenarioProvider.java
 * @skip  begin: following section is used for docu generation - Load test scenario configuration
 * @until end: following section is used for docu generation - Load test scenario configuration
 */
public class JLoadScenario {

    private final String id;
    private final List<JParallelTestsGroup> testGroups;

    /** Builder of the JLoadScenario
     * @n
     * @details Constructor parameters are mandatory for the JLoadScenario. All parameters, set by setters are optional
     * @n
     * @param id - Unique id of the load scenario
     * @param testGroups - List of JParallelTestsGroup to execute
     */
    public static Builder builder(Id id, List<JParallelTestsGroup> testGroups) {
        return new Builder(id, testGroups);
    }

    /** Builder of the JLoadScenario
     * @n
     * @details Constructor parameters are mandatory for the JLoadScenario. All parameters, set by setters are optional
     * @n
     * @param id - Unique id of the load scenario
     * @param testGroup - Load scenario should contain at least one test group
     * @param testGroups - List of JParallelTestsGroup to execute
     */
    public static Builder builder(Id id, JParallelTestsGroup testGroup, JParallelTestsGroup... testGroups) {
        
        List<JParallelTestsGroup> jParallelTestsGroupList = new ArrayList<>();
        jParallelTestsGroupList.add(testGroup);
        Collections.addAll(jParallelTestsGroupList, testGroups);
        
        return new Builder(id, jParallelTestsGroupList);
    }

    private JLoadScenario(Builder builder) {
        this.id = builder.id.value();
        this.testGroups = builder.testGroups;
    }

    public static class Builder {
        private final Id id;
        private final List<JParallelTestsGroup> testGroups;
    
        public Builder(Id id, List<JParallelTestsGroup> testGroups) {
            this.id = id;
            this.testGroups = testGroups;
        }

        /**
         * Creates the object of JLoadScenario type with custom parameters.
         *
         * @return JLoadScenario object.
         */
        public JLoadScenario build() {
            return new JLoadScenario(this);
        }

    }

    public List<JParallelTestsGroup> getTestGroups() {
        return testGroups;
    }

    public String getId() {
        return id;
    }
}
