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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.griddynamics.jagger.coordinator.NodeId;

import java.util.Map;
import java.util.Set;

public class WorkloadExecutionStatusBuilder {
    private final Map<NodeId, Integer> threads = Maps.newConcurrentMap();
    private final Map<NodeId, Integer> startedSamples = Maps.newConcurrentMap();
    private final Map<NodeId, Integer> finishedSamples = Maps.newConcurrentMap();
    private final Map<NodeId, Integer> delays = Maps.newConcurrentMap();
    private final Map<NodeId, Long> pollTime = Maps.newConcurrentMap();
    private final Map<NodeId, Long> durationTime = Maps.newConcurrentMap();
    private WorkloadTask task;

    public WorkloadExecutionStatusBuilder(WorkloadTask task) {
        this.task = task;
    }

    public WorkloadExecutionStatusBuilder addNodeInfo(NodeId id, int threads, int startedSamples, int finishedSamples, Integer delay, long pollTime, long durationTime) {
        this.threads.put(id, threads);
        this.startedSamples.put(id, startedSamples);
        this.finishedSamples.put(id, finishedSamples);
        this.delays.put(id, delay);
        this.pollTime.put(id, pollTime);
        this.durationTime.put(id, durationTime);
        return this;
    }

    public WorkloadExecutionStatus build() {
        return new DefaultWorkloadExecutionStatus(threads, startedSamples, finishedSamples, delays, pollTime, durationTime, task);
    }

    private class DefaultWorkloadExecutionStatus implements WorkloadExecutionStatus {
        private final Set<NodeId> nodes;
        private final Map<NodeId, Integer> threads;
        private final Map<NodeId, Integer> startedSamples;
        private final Map<NodeId, Integer> finishedSamples;
        private final Map<NodeId, Integer> delays;
        private final Map<NodeId, Long> pollTime;
        private final Map<NodeId, Long> durationTime;
        private WorkloadTask task;

        private DefaultWorkloadExecutionStatus(Map<NodeId, Integer> threads, Map<NodeId, Integer> startedSamples,
                                               Map<NodeId, Integer> finishedSamples,
                                               Map<NodeId, Integer> delays, Map<NodeId, Long> pollTime, Map<NodeId, Long> durationTime, WorkloadTask task) {
            boolean nodesAreEqual = threads.keySet().equals(startedSamples.keySet()) && startedSamples.keySet().equals(pollTime.keySet());

            Preconditions.checkArgument(nodesAreEqual);
            this.nodes = threads.keySet();

            this.task = task;
            this.threads = threads;
            this.startedSamples = startedSamples;
            this.finishedSamples = finishedSamples;
            this.delays = delays;
            this.pollTime = pollTime;
            this.durationTime = durationTime;
        }


        @Override
        public Set<NodeId> getNodes() {
            return nodes;
        }

        @Override
        public Integer getThreads(NodeId id) {
            return threads.get(id);
        }

        @Override
        public Integer getStartedSamples(NodeId id) {
            return startedSamples.get(id);
        }

        @Override
        public Integer getSamples(NodeId id) {
            return finishedSamples.get(id);
        }

        @Override
        public Integer getDelay(NodeId id) {
            return delays.get(id);
        }

        @Override
        public Long getPollTime(NodeId id) {
            return pollTime.get(id);
        }

        @Override
        public int getTotalStartedSamples() {
            int result = 0;
            for (Integer sample : startedSamples.values()) {
                result += sample;
            }
            return result;
        }

        @Override
        public int getTotalSamples() {
            int result = 0;
            for (Integer sample : finishedSamples.values()) {
                result += sample;
            }
            return result;
        }

        @Override
        public int getTotalThreads() {
            int result = 0;
            for (Integer threads : this.threads.values()) {
                result += threads;
            }
            return result;
        }

        @Override
        public String toString() {
            String line = "---------------------------------------------------------------------------------------------------------------------------------------------------\n";
            String format = "|%1$-40s|%2$-20s|%3$-20s|%4$-20s|%5$-20s|%6$-20s|\n";
            String report = String.format(this.task.getTaskName() + '\n' +
                    line + format + line, "IDENTIFIER", "THREADS", "STARTED", "SAMPLES", "DELAYS", "DURATION,s");
            Set<NodeId> nodes = Sets.newHashSet(this.threads.keySet());
            nodes.addAll(this.startedSamples.keySet());
            nodes.addAll(this.delays.keySet());

            for (NodeId node : nodes) {
                report += String.format(format,
                        node.getIdentifier(), this.threads.get(node),
                        this.startedSamples.get(node),
                        this.finishedSamples.get(node), this.delays.get(node), this.durationTime.get(node)/1000);
            }
            return report + line;
        }
    }
}