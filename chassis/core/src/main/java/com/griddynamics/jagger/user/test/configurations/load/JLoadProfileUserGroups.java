package com.griddynamics.jagger.user.test.configurations.load;

import java.util.ArrayList;
import java.util.Collections;
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

    public JLoadProfileUserGroups(JLoadProfileUsers userGroup, JLoadProfileUsers... userGroups) {
        Objects.nonNull(userGroup);
        ArrayList<JLoadProfileUsers> groups = new ArrayList<>();
        groups.add(userGroup);
        Collections.addAll(groups, userGroups);
        this.userGroups = groups;
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
