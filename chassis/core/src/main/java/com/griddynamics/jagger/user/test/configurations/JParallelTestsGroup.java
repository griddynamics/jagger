package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupListener;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @brief  Describes step in the JLoadScenario execution sequence
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
    private final List<Provider<TestGroupListener>> listeners;
    private final List<Provider<TestGroupDecisionMakerListener>> decisionMakerListeners;

    /**
     * Builder of the JParallelTestsGroup
     * @n
     * @details Constructor parameters are mandatory for the JParallelTestsGroup. All parameters, set by setters are optional
     * @n
     * @param id - Unique id of the parallel test group
     * @param tests - List of JLoadTest that should run in parallel. Can contain single or multiple elements
     */
    public static Builder builder(Id id, List<JLoadTest> tests) {
        return new Builder(id, tests);
    }

    /**
     * Builder of the JParallelTestsGroup
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
        this.listeners = builder.listeners;
        this.decisionMakerListeners = builder.decisionMakerListeners;
    }

    public static class Builder {
        
        private final Id id;
        private final List<JLoadTest> tests;
        private List<Provider<TestGroupListener>> listeners = Lists.newArrayList();
        private List<Provider<TestGroupDecisionMakerListener>> decisionMakerListeners = Lists.newArrayList();
    
        public Builder(Id id, List<JLoadTest> tests) {
            this.id = id;
            this.tests = tests;
        }
    
        /**
         * Optional: Adds a subtype instance of {@link com.griddynamics.jagger.engine.e1.Provider<TestGroupListener>}
         * which give you an ability to execute some actions before and after test-group.
         * Example:
         * @code
         *      addListener(new ExampleTestGroupListener())
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.testgroup.ExampleTestGroupListener for example
         */
        public Builder addListener(Provider<TestGroupListener> listener) {
            this.listeners.add(listener);
            return this;
        }
    
        /**
         * Optional: Adds instances of subtypes of {@link com.griddynamics.jagger.engine.e1.Provider<TestGroupListener>}
         * which give you an ability to execute some actions before and after test-group.
         * Example:
         * @code
         *      addListeners(Arrays.asList(new ExampleTestGroupListener()))
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.testgroup.ExampleTestGroupListener for example
         */
        public Builder addListeners(List<Provider<TestGroupListener>> listeners) {
            this.listeners.addAll(listeners);
            return this;
        }
    
        /**
         * Optional: Adds a subtype instance of {@link com.griddynamics.jagger.engine.e1.Provider<TestGroupDecisionMakerListener>}
         * @n
         * This type of listener is intended to make decision about test group execution status.
         * Example:
         * @code
         *      addDecisionMakerListener(new com.griddynamics.jagger.engine.e1.BasicTGDecisionMakerListener())
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.BasicTGDecisionMakerListener for example
         */
        public Builder addDecisionMakerListener(Provider<TestGroupDecisionMakerListener> listener) {
            this.decisionMakerListeners.add(listener);
            return this;
        }
    
        /**
         * Optional: Adds instances of subtypes of {@link com.griddynamics.jagger.engine.e1.Provider<TestGroupDecisionMakerListener>}
         * @n
         * This type of listener is intended to make decision about test group execution status.
         * Example:
         * @code
         *      addDecisionMakerListeners(Arrays.asList(new com.griddynamics.jagger.engine.e1.BasicTGDecisionMakerListener()))
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.BasicTGDecisionMakerListener for example
         */
        public Builder addDecisionMakerListeners(List<Provider<TestGroupDecisionMakerListener>> listeners) {
            this.decisionMakerListeners.addAll(listeners);
            return this;
        }

        /**
         * Creates the object of JParallelTestsGroup type with custom parameters
         *
         * @return JParallelTestsGroup object.
         */
        public JParallelTestsGroup build() {
    
            if (tests.stream().map(JLoadTest::getId).collect(Collectors.toSet()).size() < tests.size()) {
                throw new IllegalStateException("JLoadTest ids inside one JParallelTestsGroup are not all unique");
            }
            
            return new JParallelTestsGroup(this);
        }

    }

    public List<JLoadTest> getTests() {
        return tests;
    }

    public String getId() {
        return id;
    }
    
    public List<Provider<TestGroupListener>> getListeners() {
        return listeners;
    }
    
    public List<Provider<TestGroupDecisionMakerListener>> getDecisionMakerListeners() {
        return decisionMakerListeners;
    }
}
