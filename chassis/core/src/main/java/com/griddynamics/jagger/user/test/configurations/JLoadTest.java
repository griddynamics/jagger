package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.aux.Id;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;

/**
 * Describes the instance of a load test performed by Jagger.
 * Termination strategy, load configuration etc. are configurable.
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

    public static Builder builder(Id id, JTestDefinition description, JLoadProfile load, JTerminationCriteria termination) {
        return new Builder(id, description, load, termination);
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
         * As one may expect, create the object of {@link JLoadTest} with custom parameters.
         *
         * @return {@link JLoadTest} object.
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
