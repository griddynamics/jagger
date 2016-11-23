package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** @brief  Describes step in the JLoadScenario execution sequence
 * @n
 * @par Details:
 * @details Parallel test group is a step in the JLoadScenario execution sequence. It can contain one ore multiple JLoadTest. All JLoadTest inside group will be executed in parallel. @n
 * See @ref section_writing_test_load_scenario for more details @n
 * @n
 * More information on the parameter of the test definition, you can find in the Builder documentation @n
 * @n
 * Code example:
 * @dontinclude  ExampleSimpleJLoadScenarioProvider.java
 * @skip  begin: following section is used for docu generation - Load test scenario configuration
 * @until end: following section is used for docu generation - Load test scenario configuration
 */
public class JParallelTestsGroup {
    private final String id;
    private final List<JLoadTest> tests;

    /** Builder of the JParallelTestsGroup
     * @n
     * @details Constructor parameters are mandatory for the JParallelTestsGroup. All parameters, set by setters are optional
     * @n
     * @param id - Unique id of the parallel test group
     * @param tests - List of JLoadTest that should run in parallel. Can contain single or multiple elements
     */
    public static Builder builder(Id id, List<JLoadTest> tests) {
        return new Builder(id, tests);
    }

    /** Builder of the JParallelTestsGroup
     * @n
     * @details Constructor parameters are mandatory for the JParallelTestsGroup. All parameters, set by setters are optional
     * @n
     * @param id - Unique id of the parallel test group
     * @param test - Test group should contain at least on JLoadTest
     * @param tests - List of JLoadTest that should run in parallel. Can contain single or multiple elements
     */
    public static Builder builder(Id id, JLoadTest test, JLoadTest... tests) {
        
        List<JLoadTest> testList = new ArrayList<>();
        testList.add(test);
        Collections.addAll(testList, tests);
        
        return new Builder(id, testList);
    }

    private JParallelTestsGroup(Builder builder) {
        this.id = builder.id.value();
        this.tests = builder.tests;
    }

    public static class Builder {
        private final Id id;

        private final List<JLoadTest> tests;
    
        public Builder(Id id, List<JLoadTest> tests) {
            this.id = id;
            this.tests = tests;
        }

        /**
         * Creates the object of JParallelTestsGroup type with custom parameters
         *
         * @return JParallelTestsGroup object.
         */
        public JParallelTestsGroup build() {
            return new JParallelTestsGroup(this);
        }

    }

    public List<JLoadTest> getTests() {
        return tests;
    }

    public String getId() {
        return id;
    }
}
