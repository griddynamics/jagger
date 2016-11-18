package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;

/**
 * Describes the instance of a test performed by Jagger.
 * Termination strategy, load configuration etc. are configurable.
 */
public class JTest {

    private final String id;
    private final JLoad load;
    private final JTestDescription testDescription;
    private final JTermination termination;

    private JTest(Builder builder) {
        this.id = builder.id.value();
        this.testDescription = builder.jTestDescription;
        this.load = builder.load;
        this.termination = builder.termination;
    }

    public static Builder builder(Id id, JTestDescription description, JLoad load, JTermination termination) {
        return new Builder(id, description, load, termination);
    }

    public static class Builder {
        private final Id id;
        private final JTestDescription jTestDescription;
        private final JLoad load;
        private final JTermination termination;
    
        private Builder(Id id, JTestDescription jTestDescription, JLoad load, JTermination termination) {
            this.id = id;
            this.jTestDescription = jTestDescription;
            this.load = load;
            this.termination = termination;
        }

        /**
         * As one may expect, create the object of {@link JTest} with custom parameters.
         *
         * @return {@link JTest} object.
         */
        public JTest build() {
            return new JTest(this);
        }
    }

    public String getId() {
        return id;
    }

    public JTestDescription getTestDescription() {
        return testDescription;
    }

    public JLoad getLoad() {
        return load;
    }

    public JTermination getTermination() {
        return termination;
    }
}
