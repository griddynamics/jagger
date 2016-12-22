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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: dkotlyarov
 */
@Deprecated
// TODO: Should be removed with xml configuration JFG-906
public class UserClock implements WorkloadClock {
    private static final Logger log = LoggerFactory.getLogger(UserClock.class);

    private final Random random;
    private final long startTime = System.currentTimeMillis();
    private final UserWorkload workload;
    private final int totalUserCount;
    private final int tickInterval;
    private final AtomicBoolean shutdown;
    private boolean userStarted = false;

    public UserClock(List<ProcessingConfig.Test.Task.User> users, int delay, int tickInterval, AtomicBoolean shutdown) {
        this.random = new Random(0);
        this.workload = new UserWorkload(true, this, users, delay, startTime);
        this.totalUserCount = workload.getTotalUserCount();
        this.tickInterval = tickInterval;
        this.shutdown = shutdown;
    }

    public Random getRandom() {
        return random;
    }

    public long getStartTime() {
        return startTime;
    }

    public UserWorkload getWorkload() {
        return workload;
    }

    public int getTotalUserCount() {
        return totalUserCount;
    }

    public AtomicBoolean getShutdown() {
        return shutdown;
    }

    public boolean isUserStarted() {
        return userStarted;
    }

    public void setUserStarted(boolean userStarted) {
        this.userStarted = userStarted;
    }

    @Override
    public Map<NodeId, Integer> getPoolSizes(Set<NodeId> nodes) {
        int max = totalUserCount / nodes.size() + 1;
        Map<NodeId, Integer> result = Maps.newHashMap();
        for (NodeId node : nodes) {
            result.put(node, max);
        }
        return result;
    }

    @Override
    public void tick(WorkloadExecutionStatus status, WorkloadAdjuster adjuster) {
        log.debug("Going to perform tick with status {}", status);

        Set<NodeId> nodes = status.getNodes();
        LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations = new LinkedHashMap<NodeId, WorkloadConfiguration>(nodes.size());
        for (NodeId node : nodes) {
            WorkloadConfiguration workloadConfiguration = WorkloadConfiguration.with(status.getThreads(node), status.getDelay(node));
            workloadConfigurations.put(node, workloadConfiguration);
        }

        long time = System.currentTimeMillis();

        workload.tick(time, workloadConfigurations);

        for (Map.Entry<NodeId, WorkloadConfiguration> workloadConfigurationEntry : workloadConfigurations.entrySet()) {
            NodeId nodeId = workloadConfigurationEntry.getKey();
            WorkloadConfiguration workloadConfiguration = workloadConfigurationEntry.getValue();
            if ((workloadConfiguration.getThreads() != status.getThreads(nodeId)) || (workloadConfiguration.getDelay() != status.getDelay(nodeId))) {
                adjuster.adjustConfiguration(nodeId, workloadConfiguration);
                log.debug("Adjust configuration {} for node {}", workloadConfiguration, nodeId);
            }
        }

        if (userStarted && (workload.getStartedUserCount()==workload.getTotalUserCount())
                && (workload.getActiveUserCount() == 0)) {
            shutdown.set(true);
        }
    }

    @Override
    public int getTickInterval() {
        return tickInterval;
    }

    @Override
    public int getValue() {
        return totalUserCount;
    }

    @Override
    public String toString() {
        return totalUserCount + " virtual user with " + workload.getDelay() + " delay";
    }
}
