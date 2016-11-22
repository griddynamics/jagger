package com.griddynamics.jagger.user.test.configurations.load;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;

/**
 * This load is a list of user groups {@link JLoadProfileUsers}. Every such user group imitates a group of threads. Threads will start sequentially.
 * Thus you are able to create load ramp-up and rump-down with this load type. You can configure a number of threads by attributes of user group.<p>
 * Available attributes:<p>
 *     - numberOfUsers - A goal number of threads.<p>
 *     - lifeTimeInSeconds - Describes how long threads will be alive. Default is 2 days.<p>
 *     - startDelayInSeconds - Delay before first thread will start. Default is 0.<p>
 *     - slewRateUsersPerSecond - Describes how many threads to start during every iteration. Default is numberOfUsers value.<p>
 * You can set optional attribute delayBetweenInvocationsInSeconds to specify delay in seconds between invocations (default value is 0s).
 */
public class JLoadProfileUserGroups implements JLoadProfile {

    /**
     * List of user groups {@link JLoadProfileUsers}.
     */
    private final List<JLoadProfileUsers> userGroups;

    /**
     * Delay between invocations in seconds. Default is 0 s.
     */
    private int delayBetweenInvocationsInSeconds;

    /**
     * Tick interval (in milliseconds). Default is 1000 ms.
     */
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
        this.tickInterval = 1000;
    }

    public static JLoadProfileUserGroups of(JLoadProfileUsers userGroup) {
        return new JLoadProfileUserGroups(userGroup);
    }

    public static JLoadProfileUserGroups of(JLoadProfileUsers userGroup, JLoadProfileUsers... userGroups) {
        return new JLoadProfileUserGroups(userGroup, userGroups);
    }

    public List<JLoadProfileUsers> getUserGroups() {
        return userGroups;
    }

    public int getDelayBetweenInvocationsInSeconds() {
        return delayBetweenInvocationsInSeconds;
    }

    /**
     * Delay between invocations in seconds. Default is 0 s.
     */
    public JLoadProfileUserGroups withDelayBetweenInvocationsInSeconds(int delayBetweenInvocationsInSeconds) {
        this.delayBetweenInvocationsInSeconds = delayBetweenInvocationsInSeconds;
        return this;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    /**
     * Tick interval (in ms). Default is 1000 ms.
     */
    public JLoadProfileUserGroups withTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
        return this;
    }
}
