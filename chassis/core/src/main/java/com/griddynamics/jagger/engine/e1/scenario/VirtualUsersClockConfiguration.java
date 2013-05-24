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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class VirtualUsersClockConfiguration implements WorkloadClockConfiguration {
    private Logger log = LoggerFactory.getLogger(VirtualUsersClockConfiguration.class);

    private int users;
    private int tickInterval;
    private InvocationDelayConfiguration delay = FixedDelay.noDelay();

    public void setUsers(int users) {
        this.users = users;
    }

    public void setCount(int users) {
        this.users = users;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    @Override
    public WorkloadClock getClock() {
        return new VirtualUsersClock();
    }

    public void setDelay(InvocationDelayConfiguration delay) {
        this.delay = delay;
    }

    private class VirtualUsersClock implements WorkloadClock {

        @Override
        public Map<NodeId, Integer> getPoolSizes(Set<NodeId> nodes) {
            float a = ((float) users) / nodes.size();
            int max = (int) Math.ceil(a);
            Map<NodeId, Integer> result = Maps.newHashMap();
            for (NodeId node : nodes) {
                result.put(node, max);
            }
            return result;
        }

        @Override
        public void tick(WorkloadExecutionStatus status, WorkloadAdjuster adjuster) {
            log.debug("Going to perform tick with status {}", status);

            int totalThreads = status.getTotalThreads();
            int diff = users - totalThreads;
            if (diff < 0) {
                throw new IllegalStateException("Required to execute " + users + " users but " + totalThreads + " is executed at this moment.");
            }

            Map<NodeId, Integer> threadNumbers = partition(diff, status.getNodes());

            for (Map.Entry<NodeId, Integer> entry : threadNumbers.entrySet()) {
                Integer adjustment = entry.getValue();
                NodeId node = entry.getKey();

                if (adjustment > 0) {
                    log.debug("Controller should adjust task number on node {} to {}", node, adjustment);
                    int delayInterval = delay.getInvocationDelay().getValue();
                    log.debug("Controller should adjust invocation delay on node {} to {}", node, adjustment);
                    adjuster.adjustConfiguration(node, WorkloadConfiguration.with(adjustment, delayInterval));
                }
            }
        }

        private Map<NodeId, Integer> partition(int threads, Set<NodeId> availableNodes) {
            Iterator<NodeId> nodes = availableNodes.iterator();

            Map<NodeId, Integer> threadNumbers = Maps.newHashMap();

            while (threads > 0) {
                if (!nodes.hasNext()) {
                    nodes = availableNodes.iterator();
                }

                NodeId node = nodes.next();
                Integer currentAdjustment = threadNumbers.get(node);
                if (currentAdjustment == null) {
                    currentAdjustment = 0;
                }

                int newAdjustment = currentAdjustment + 1;
                threadNumbers.put(node, newAdjustment);
                threads--;
            }
            return threadNumbers;
        }

        @Override
        public int getTickInterval() {
            return tickInterval;
        }

        @Override
        public int getValue() {
            return users;
        }

        @Override
        public String toString() {
            return users + " virtual user(s) with " + delay + " delay";
        }
    }

    @Override
    public String toString() {
        return users + " virtual users with " + delay + " delay";
    }
}
