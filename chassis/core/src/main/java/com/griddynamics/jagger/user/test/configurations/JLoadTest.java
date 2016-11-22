package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;

/** @brief  Describes the instance of a load test performed by Jagger
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
 * @dontinclude  ExampleSimpleJLoadScenarioProvider.java
 * @skip  begin: following section is used for docu generation - Load test scenario configuration
 * @until end: following section is used for docu generation - Load test scenario configuration
 */
public class JLoadTest {

    private final String id;
    private final JLoadProfile load;
    private final JTestDefinition testDescription;
    private final JTerminationCriteria termination;

    private JLoadTest(Builder builder) {
        this.id = builder.id.value();
        this.testDescription = builder.jTestDefinition;
        this.load = builder.load;
        this.termination = builder.termination;
    }

    /** Builder of the JLoadTest
     * @n
     * @details Constructor parameters are mandatory for the JLoadTest. JLoadTest parameters, set by setters are optional
     * @n
     * @param id - Unique id of the test definition
     * @param definition - Definition of the load test (sources of the test data and used protocol)
     * @param load - Load strategy for this load test (Virtual users, Requests per seconds, etc)
     * @param termination - Termination criteria for this load test (when load test should be finished)
     */
    public static Builder builder(Id id, JTestDefinition definition, JLoadProfile load, JTerminationCriteria termination) {
        return new Builder(id, definition, load, termination);
    }

    public static class Builder {
        private final Id id;
        private final JTestDefinition jTestDefinition;
        private final JLoadProfile load;
        private final JTerminationCriteria termination;
    
        private Builder(Id id, JTestDefinition jTestDefinition, JLoadProfile load, JTerminationCriteria termination) {
            this.id = id;
            this.jTestDefinition = jTestDefinition;
            this.load = load;
            this.termination = termination;
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
}
