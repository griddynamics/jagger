package com.griddynamics.jagger.user.test.configurations;

import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;

/**
 * Test configuration holder.
 * Here a user can set termination strategy, load configuration and other parameters of a test.
 */
public class JTest {


    private String id;
    private JLoad load;
    private JTestDescription testDescription;
    private JTermination termination;


    private JTest(Builder builder) {
        this.id = builder.id;
        this.testDescription = builder.jTestDescription;
        this.load = builder.load;
        this.termination = builder.termination;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private JTestDescription jTestDescription;
        private JLoad load;
        private JTermination termination;


        private Builder() {

        }

        /**
         * Set {@code id} for a test.
         *
         * @param id for a test.
         */
        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set description for a test.
         *
         * @param jTestDescription the test description.
         */
        public Builder withJTestDescription(JTestDescription jTestDescription) {
            this.jTestDescription = jTestDescription;
            return this;
        }


        /**
         * Sets {@link JLoad} for a test.
         *
         * @param load load.
         */
        public Builder withLoad(JLoad load) {
            this.load = load;
            return this;
        }

        /**
         * Set {@link JTermination} for a test.
         *
         * @param termination termination.
         */
        public Builder withTermination(JTermination termination) {
            this.termination = termination;
            return this;
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
