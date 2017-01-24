/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.scenario;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.util.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: dkotlyarov
 */
public class UserGroup {
    private static final Logger log = LoggerFactory.getLogger(UserGroup.class);

    private final int id;
    private final long life;
    private final int count;
    private final long startBy;
    private final UserClock clock;
    final ArrayList<User> users;
    int activeUserCount = 0;
    private int startCount;
    private final long startInTime;
    private long startByTime = -1;
    int startedUserCount = 0;
    private double extraUser;
    private double extraUserStep;

    public UserGroup(UserClock clock, int id, ProcessingConfig.Test.Task.User config, long time) {
        this(clock,
                id,
                Parser.parseInt(config.getCount(), clock.getRandom()),
                (int) config.getStartCount(),
                time + Parser.parseTime(config.getStartIn(), clock.getRandom()),
                Parser.parseTime(config.getStartBy(), clock.getRandom()),
                Parser.parseTime(config.getLife(), clock.getRandom())
        );
        double floor = (Double.compare(config.getStartCount(), 1.0) < 0) ?
                +0.0 :
                +Math.floor(config.getStartCount());
        double extraUserStep = config.getStartCount() - floor;
        this.extraUserStep = extraUserStep;
        this.extraUser = extraUserStep;
    }

    public UserGroup(UserClock clock, int id, int count, int startCount, long startInTime, long startBy, long life) {
        this.clock = clock;
        this.id = id;
        this.count = count;
        this.users = new ArrayList<User>(count);
        this.startCount = startCount;
        this.life = life;
        this.startInTime = startInTime;
        this.startBy = startBy;

        log.info(String.format("User group %d is created", id));

    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public long getLife() {
        return life;
    }

    public int getActiveUserCount() {
        return activeUserCount;
    }

    public int getStartCount() {
        return startCount;
    }

    public long getStartInTime() {
        return startInTime;
    }

    public long getStartByTime() {
        return startByTime;
    }

    public void tick(long time, LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations) {
        // to allow user set floating value of users
        extraUser += extraUserStep;

        for (User user : users) {
            user.tick(time, workloadConfigurations);
        }

        if (users.size() < count) {
            if (users.isEmpty()) {
                if (time >= startInTime) {
                    spawnUsers(time, workloadConfigurations);
                    startByTime = time + startBy;
                }
            } else {
                while (time >= startByTime) {
                    spawnUsers(time, workloadConfigurations);
                }
            }
        }
    }

    public void spawnUsers(long time, LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations) {
        // if true at this tick should appear one additional user
        if (extraUser >= 1.0) {
            startCount++;
        }
        for (int i = 0; i < startCount; ++i) {
            if (users.size() < count) {
                new User(clock, this, time, findNodeWithMinThreadCount(workloadConfigurations), workloadConfigurations);
            }
        }
        // decrement the number of additional users
        if (extraUser >= 1.0) {
            startCount--;
            extraUser -= 1.0;
        }
        startByTime += startBy;
    }

    public static NodeId findNodeWithMinThreadCount(LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations) {
        Iterator<Map.Entry<NodeId, WorkloadConfiguration>> it = workloadConfigurations.entrySet().iterator();
        Map.Entry<NodeId, WorkloadConfiguration> minNode = it.next();
        while (it.hasNext()) {
            Map.Entry<NodeId, WorkloadConfiguration> node = it.next();
            if (node.getValue().getThreads() < minNode.getValue().getThreads()) {
                minNode = node;
            }
        }
        return minNode.getKey();
    }

    public int getStartedUserCount() {
        return startedUserCount;
    }
}
