package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.ExampleLoadScenarioListener;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioListener;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @brief  Describes the execution sequence of the load tests during single run
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
    private final List<Double> percentileValues;
    private final List<Provider<LoadScenarioListener>> listeners;

    private JLoadScenario(Builder builder) {
        this.id = builder.id.value();
        this.testGroups = builder.testGroups;
        this.percentileValues = builder.percentileValues;
        this.listeners = builder.listeners;
    }

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

    public static class Builder {
        private final Id id;
        private final List<JParallelTestsGroup> testGroups;
        private List<Double> percentileValues;
        private List<Provider<LoadScenarioListener>> listeners = Lists.newArrayList();
        
        public Builder(Id id, List<JParallelTestsGroup> testGroups) {
            this.id = id;
            this.testGroups = testGroups;
        }

        /**
         * Optional: Sets list of latency percentile values
         *
         * @param percentileValues latency percentile values
         */
        public Builder withLatencyPercentiles(List<Double> percentileValues) {
            Objects.requireNonNull(percentileValues);
            percentileValues.stream().filter(percentile -> percentile <= 0.0 || percentile >= 100.0)
                    .findAny()
                    .ifPresent(badPercentile -> {
                        throw new IllegalArgumentException("Percentile value must be > 0 and < 100. Provided value is " + badPercentile);
                    });

            this.percentileValues = percentileValues;
            return this;
        }
    
        /**
         * Optional: Adds instances of subtypes of {@link com.griddynamics.jagger.engine.e1.Provider< LoadScenarioListener >}
         * These listeners are executed before and after test suite.
         * Example:
         * @code
         *      addListener(new ExampleLoadScenarioListener())
         * @endcode
         * @see ExampleLoadScenarioListener for example
         */
        public Builder addListener(Provider<LoadScenarioListener> listener) {
            this.listeners.add(listener);
            return this;
        }
    
        /**
         * Optional: Adds instances of subtypes of {@link com.griddynamics.jagger.engine.e1.Provider< LoadScenarioListener >}
         * These listeners are executed before and after test suite.
         * Example:
         * @code
         *      addListeners(Arrays.asList(new ExampleLoadScenarioListener()))
         * @endcode
         * @see ExampleLoadScenarioListener for example
         */
        public Builder addListeners(List<Provider<LoadScenarioListener>> listeners) {
            this.listeners.addAll(listeners);
            return this;
        }

        /**
         * Creates the object of JLoadScenario type with custom parameters.
         *
         * @return JLoadScenario object.
         */
        public JLoadScenario build() {
    
            if (testGroups.stream().map(JParallelTestsGroup::getId).collect(Collectors.toSet()).size() < testGroups.size()) {
                throw new IllegalStateException("JParallelTestsGroup ids inside one JLoadScenario are not all unique");
            }
            
            return new JLoadScenario(this);
        }

    }

    public List<JParallelTestsGroup> getTestGroups() {
        return testGroups;
    }

    public String getId() {
        return id;
    }

    public List<Double> getPercentileValues() {
        return percentileValues;
    }
    
    public List<Provider<LoadScenarioListener>> getListeners() {
        return listeners;
    }
}
