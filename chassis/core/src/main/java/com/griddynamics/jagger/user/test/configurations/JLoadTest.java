package com.griddynamics.jagger.user.test.configurations;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @brief Describes the instance of a load test performed by Jagger
 * @n
 * @par Details:
 * @details JLoadTest describes an instance of the load test, build with JTestDefinition. JLoadTest sets following parameters:
 * @li load strategy - how load will be applied
 * @li termination criteria - when load should be terminated
 *
 * See @ref section_writing_test_load_scenario for more details @n
 * @n
 * More information on the parameter of the test definition, you can find in the Builder documentation @n
 * @n
 * Code example:
 * @dontinclude  SimpleJLoadScenarioProvider.java
 * @skip  begin: following section is used for docu generation - Load test scenario configuration
 * @until end: following section is used for docu generation - Load test scenario configuration
 */
public class JLoadTest {

    private final String id;
    private final JLoadProfile load;
    private final JTestDefinition testDescription;
    private final JTerminationCriteria termination;
    private final List<JLimit> limits;
    private final List<Provider<TestListener>> listeners;

    private JLoadTest(Builder builder) {
        this.id = builder.id.value();
        this.testDescription = builder.jTestDefinition;
        this.load = builder.load;
        this.termination = builder.termination;
        this.limits = builder.limits;
        this.listeners = builder.listeners;
    }

    /**
     * Builder of the JLoadTest
     * @param id          - Unique id of the test definition
     * @param definition  - Definition of the load test (sources of the test data and used protocol)
     * @param load        - Load strategy for this load test (Virtual users, Requests per seconds, etc)
     * @param termination - Termination criteria for this load test (when load test should be finished)
     * @n
     * @details Constructor parameters are mandatory for the JLoadTest. JLoadTest parameters, set by setters are optional
     * @n
     */
    public static Builder builder(Id id, JTestDefinition definition, JLoadProfile load, JTerminationCriteria termination) {
        return new Builder(id, definition, load, termination);
    }

    public static class Builder {
        private final Id id;
        private final JTestDefinition jTestDefinition;
        private final JLoadProfile load;
        private final JTerminationCriteria termination;
        private List<JLimit> limits;

        private List<Provider<TestListener>> listeners = Lists.newArrayList();
    
        private Builder(Id id, JTestDefinition jTestDefinition, JLoadProfile load, JTerminationCriteria termination) {
            this.id = id;
            this.jTestDefinition = jTestDefinition;
            this.load = load;
            this.termination = termination;
            this.limits = new LinkedList<>();
        }
    
        /**
         * Optional: Adds instances of subtypes of {@link com.griddynamics.jagger.engine.e1.Provider<TestListener>}
         * Listeners will be executed before, after and periodically during a test
         * @n
         * Example:
         * @code
         *      addListeners(Arrays.asList(new CollectThreadsTestListener()))
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener for example
         */
        public Builder addListeners(List<Provider<TestListener>> listeners) {
            this.listeners.addAll(listeners);
            return this;
        }
    
        /**
         * Optional: Adds a subtype instance of {@link com.griddynamics.jagger.engine.e1.Provider<TestListener>}
         * Listener will be executed before, after and periodically during a test
         * @n
         * Example:
         * @code
         *      addListener(new CollectThreadsTestListener())
         * @endcode
         * @see com.griddynamics.jagger.engine.e1.collector.CollectThreadsTestListener for example
         */
        public Builder addListener(Provider<TestListener> listener) {
            this.listeners.add(listener);
            return this;
        }

        /**
         * Optional: Set limits for a test.
         *
         * @param limits array of {@link JLimit}.
         */
        public Builder withLimits(JLimit... limits) {
            Objects.requireNonNull(limits);
            this.limits = Arrays.asList(limits);
            return this;
        }

        /**
         * Optional: Set limits for a test.
         *
         * @param limits list of {@link JLimit}.
         */
        public Builder withLimits(List<JLimit> limits) {
            Objects.requireNonNull(limits);
            this.limits = limits;
            return this;
        }

        /**
         * Optional: Add limits for a test.
         *
         * @param limits array of {@link JLimit}.
         */
        public Builder addLimits(JLimit... limits){
            Objects.requireNonNull(limits);
            this.limits.addAll(Arrays.asList(limits));
            return this;
        }

        /**
         * Optional: Add limits for a test.
         *
         * @param limits list of {@link JLimit}.
         */
        public Builder addLimits(List<JLimit> limits){
            Objects.requireNonNull(limits);
            this.limits.addAll(limits);
            return this;
        }

        /**
         * Create the object of JLoadTest with custom parameters.
         *
         * @return JLoadTest object.
         */
        public JLoadTest build() {
            return new JLoadTest(this);
        }
    }

    public String getId() {
        return id;
    }

    public JTestDefinition getTestDescription() {
        return testDescription;
    }

    public JLoadProfile getLoad() {
        return load;
    }

    public JTerminationCriteria getTermination() {
        return termination;
    }

    public List<JLimit> getLimits() {
        return limits;
    }
    
    public List<Provider<TestListener>> getListeners() {
        return listeners;
    }
}
