/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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
    private final int startCount;
    private final long startInTime;
    private long startByTime = -1;

    public UserGroup(UserClock clock, int id, ProcessingConfig.Test.Task.User config, long time) {
        this(   clock,
                id,
                Parser.parseInt(config.count, clock.getRandom()),
                Parser.parseInt(config.startCount, clock.getRandom()),
                time + Parser.parseTime(config.startIn, clock.getRandom()),
                Parser.parseTime(config.startBy, clock.getRandom()),
                Parser.parseTime(config.life, clock.getRandom())
        );
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
        for (User user : users) {
            user.tick(time, workloadConfigurations);
        }

        if (users.size() < count) {
            if (users.isEmpty()) {
                if (time >= startInTime) {
                    spawnUsers(startCount, time, workloadConfigurations);
                }
            } else {
                if (time >= startByTime) {
                    spawnUsers(startCount, time, workloadConfigurations);
                }
            }
        }
    }

    public void spawnUsers(int userCount, long time, LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations) {
        for (int i = 0; i < userCount; ++i) {
            if (users.size() < count) {
                new User(clock, this, time, findNodeWithMinThreadCount(workloadConfigurations), workloadConfigurations);
            }
        }

        startByTime = time + startBy;
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
}
