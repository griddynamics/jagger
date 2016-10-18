package com.griddynamics.jagger.user.builders;

import com.griddynamics.jagger.user.TestConfiguration;
import com.griddynamics.jagger.user.TestGroupConfiguration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author asokol
 *         created 10/14/16
 */
public class JTestGroupBuilder {

    private String id;
    private String comment;
    private List<JTestBuilder> testConfiguration;

    public JTestGroupBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public JTestGroupBuilder withTests(List<JTestBuilder> newTests) {
        this.testConfiguration = newTests;
        return this;
    }

    public JTestGroupBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public TestGroupConfiguration build() {
        TestGroupConfiguration testGroupConfiguration = new TestGroupConfiguration();
        testGroupConfiguration.setId(id);
        List<TestConfiguration> tests = testConfiguration.stream()
                .map(JTestBuilder::build)
                .collect(Collectors.toList());
        testGroupConfiguration.setTests(tests);
        return new TestGroupConfiguration();
    }

}
