package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.user.test.configurations.JTestDefinition;

/**
 * Provides default response validators for {@link JTestDefinition}.
 */
public class ResponseValidatorsProvider {

    public JHttpResponseStatusValidator provideStatusValidator() {
        return new JHttpResponseStatusValidator(null, null, null);
    }

    public NotNullResponseValidator provideNotNullValidator() {
        return new NotNullResponseValidator(null, null, null);
    }
}
