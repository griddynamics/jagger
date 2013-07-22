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

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.util.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * @author Nikolay Musienko
 *         Date: 28.06.13
 */

abstract class AbstractRateClock implements WorkloadClock {
    private static final Logger log = LoggerFactory.getLogger(AbstractRateClock.class);

    private final int tickInterval;
    private final TpsRouter tpsRouter;
    private final WorkloadSuggestionMaker workloadSuggestionMaker;

    private final SystemClock systemClock;
    private final Map<NodeId, NodeTpsRecorder> tpsStat;
    private final int maxThreads;


    public AbstractRateClock(int tickInterval, TpsRouter tpsRouter, WorkloadSuggestionMaker workloadSuggestionMaker, SystemClock systemClock, int maxThreads) {
        this.tickInterval = tickInterval;
        this.tpsRouter = tpsRouter;
        this.workloadSuggestionMaker = workloadSuggestionMaker;
        this.systemClock = systemClock;
        this.maxThreads = maxThreads;

        tpsStat = Maps.newHashMap();
    }

    @Override
    public Map<NodeId, Integer> getPoolSizes(Set<NodeId> nodes) {
        Map<NodeId, Integer> result = Maps.newHashMap();

        for (NodeId node : nodes) {
            result.put(node, maxThreads);
        }

        return result;
    }

    @Override
    public void tick(WorkloadExecutionStatus status, WorkloadAdjuster adjuster) {
        log.debug("Going to perform tick with status {}", status);

        Map<NodeId, WorkloadConfiguration> configUpdate = suggestConfigurationUpdate(status);

        for (Map.Entry<NodeId, WorkloadConfiguration> entry : configUpdate.entrySet()) {
            NodeId node = entry.getKey();
            WorkloadConfiguration configuration = entry.getValue();

            log.debug("Going to change configuration on node {} to {}", node, configuration);
            adjuster.adjustConfiguration(node, configuration);
        }

    }

    private Map<NodeId, WorkloadConfiguration> suggestConfigurationUpdate(WorkloadExecutionStatus status) {
        log.debug("Recording current status");
        recordStatus(status);
        log.debug("Current status recorded");

        log.debug("Going to recalculate desired tps per node");
        Map<NodeId, BigDecimal> desiredTpsPerNode = tpsRouter.getDesiredTpsPerNode(tpsStat);
        log.debug("Desired tps per node recalculated");
        log.debug("Desired tps per node is {}", desiredTpsPerNode);

        log.debug("Going to suggest new workload configurations for kernels");
        Map<NodeId, WorkloadConfiguration> result = Maps.newHashMap();
        for (NodeId node : status.getNodes()) {
            WorkloadConfiguration suggestion = workloadSuggestionMaker.suggest(desiredTpsPerNode.get(node), tpsStat.get(node), maxThreads);
            log.debug("Suggested {} for node {}", suggestion, node);
            result.put(node, suggestion);
        }

        return result;
    }

    private void recordStatus(WorkloadExecutionStatus status) {
        if (tpsStat.isEmpty()) {
            for (NodeId node : status.getNodes()) {
                NodeTpsRecorder recorder = new NodeTpsRecorder(20);
                recorder.recordStatus(0, 0, 0, systemClock.currentTimeMillis());
                tpsStat.put(node, recorder);
            }
            return;
        }

        for (NodeId node : status.getNodes()) {
            NodeTpsRecorder nodeTpsRecorder = tpsStat.get(node);

            Integer delay = status.getDelay(node);
            Integer threads = status.getThreads(node);
            Integer samples = getSamples(status, node);
            Long pollTime = status.getPollTime(node);


            nodeTpsRecorder.recordStatus(threads, delay, samples, pollTime);
        }
    }


    protected abstract Integer getSamples(WorkloadExecutionStatus status, NodeId node);
    //Need to have 2 implements (Rps and Tps)

    @Override
    public int getTickInterval() {
        return tickInterval;
    }

    @Override
    public int getValue() {
        return tpsRouter.getDesiredTps().intValue();
    }
}
