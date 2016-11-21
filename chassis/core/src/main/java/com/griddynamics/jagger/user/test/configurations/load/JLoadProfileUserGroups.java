package com.griddynamics.jagger.user.test.configurations.load;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;

public class JLoadProfileUserGroups {


    private final List<JLoadProfileUsers> userGroups;

    private int delayBetweenInvocationsInSeconds;

    private int tickInterval;

    public JLoadProfileUserGroups(JLoadProfileUsers userGroup) {
        Objects.nonNull(userGroup);
        this.userGroups = singletonList(userGroup);
        this.tickInterval = 1000;
    }

    public JLoadProfileUserGroups(List<JLoadProfileUsers> userGroups) {
        this.userGroups = userGroups;
    }

    public List<JLoadProfileUsers> getUserGroups() {
        return userGroups;
    }

    public int getDelayBetweenInvocationsInSeconds() {
        return delayBetweenInvocationsInSeconds;
    }

    public JLoadProfileUserGroups withDelayBetweenInvocationsInSeconds(int delayBetweenInvocationsInSeconds) {
        this.delayBetweenInvocationsInSeconds = delayBetweenInvocationsInSeconds;
        return this;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public JLoadProfileUserGroups withTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
        return this;
    }
}
