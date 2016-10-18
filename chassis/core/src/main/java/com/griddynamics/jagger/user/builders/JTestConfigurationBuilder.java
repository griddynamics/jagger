package com.griddynamics.jagger.user.builders;

import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.Task;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author asokol
 *         created 10/14/16
 */
public class JTestConfigurationBuilder {

    private List<JTestGroupBuilder> testGroups;
    private String id;
    private String comment;


    public JTestConfigurationBuilder withTestGroups(List<JTestGroupBuilder> testGroups) {
        this.testGroups = testGroups;
        return this;
    }
    public JTestConfigurationBuilder withId(String id) {
        this.id = id;
        return this;
    }
    public JTestConfigurationBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }


    public Configuration build() {
        Configuration configuration = new Configuration();
        List<Task> tasks = testGroups.stream()
                .map(testGroup -> testGroup.build().generate())
                .collect(Collectors.toList());
        configuration.setTasks(tasks);
        return configuration;
    }
}
