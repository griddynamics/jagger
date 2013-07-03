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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.util.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static com.griddynamics.jagger.util.DecimalUtil.areEqual;
import static com.griddynamics.jagger.util.DecimalUtil.compare;

public class DefaultTpsRouter implements TpsRouter {
    private static final Logger log = LoggerFactory.getLogger(DefaultTpsRouter.class);

    private final DesiredTps desiredTps;
    private final MaxTpsCalculator maxTpsCalculator;
    private final SystemClock clock;

    private final Map<NodeId, BigDecimal> desiredTpsPerNode;

    public DefaultTpsRouter(DesiredTps desiredTps, MaxTpsCalculator maxTpsCalculator, SystemClock clock) {
        this.desiredTps = desiredTps;
        this.maxTpsCalculator = maxTpsCalculator;
        this.clock = clock;

        this.desiredTpsPerNode = Maps.newHashMap();
    }

    public Map<NodeId, BigDecimal> getDesiredTpsPerNode(Map<NodeId, ? extends NodeTpsStatistics> tpsStat) {
        // todo maybe pass interval?
        BigDecimal tps = desiredTps.get(clock.currentTimeMillis());

        initialize(tpsStat.keySet());

        final Map<NodeId, BigDecimal> maxTpsPerNode = Maps.newHashMap();

        Set<NodeId> nodes = desiredTpsPerNode.keySet();

        for (NodeId node : nodes) {

            log.debug("Going to detect max tps for node {}", node);
            BigDecimal maxTps = maxTpsCalculator.getMaxTps(tpsStat.get(node));

            if (maxTps == null) {
                log.debug("Max tps for node {} cannot be detected. Using desired tps as max", node);
                maxTps = tps;
            }

            log.debug("Max tps for node {} is {}", node, maxTps);

            maxTpsPerNode.put(node, maxTps);
        }

        List<NodeId> nodeList = Lists.newLinkedList(nodes);
        Collections.sort(nodeList, new Comparator<NodeId>() {
            @Override
            public int compare(NodeId first, NodeId second) {
                return maxTpsPerNode.get(first).compareTo(maxTpsPerNode.get(second));
            }
        });

        log.debug("Recalculating desired tps per node...");
        BigDecimal missingTps = BigDecimal.ZERO;

        for (int i = 0; i < nodeList.size(); i++) {
            NodeId node = nodeList.get(i);

            BigDecimal desiredTpsForNode = desiredTpsPerNode.get(node);
            BigDecimal maxTpsForNode = maxTpsPerNode.get(node);

            log.debug("For node {} desired tps {} max tps {}", new Object[]{node, desiredTpsForNode, maxTpsForNode});

            if (compare(desiredTpsForNode, maxTpsForNode) > 0) {
                log.debug("Cannot increase tps at node {} to {}", node, desiredTpsForNode);
                BigDecimal delta = desiredTpsForNode.subtract(maxTpsForNode);
                log.debug("{} tps should be produced by other nodes", delta);
                missingTps = missingTps.add(delta);
                log.debug("Total missing tps is {}", missingTps);
                desiredTpsPerNode.put(node, maxTpsForNode);
                continue;
            }

            BigDecimal newTpsForNode = desiredTpsForNode.add(missingTps.divide(new BigDecimal(nodes.size() - i)));

            if (areEqual(desiredTpsForNode, newTpsForNode)) {
                log.debug("Node {} tps should not be changed", node);
                continue;
            }
            log.debug("Node {} is capable to produce more tps", node);
            log.debug("New desired tps for node is {}", newTpsForNode);

            if (compare(newTpsForNode, maxTpsForNode) > 0) {
                log.debug("Cannot increase tps at node {} to {}", node, newTpsForNode);
                log.debug("Increasing tps on node to it's max {}", maxTpsForNode);
                desiredTpsPerNode.put(node, maxTpsForNode);
                missingTps = missingTps.subtract(maxTpsForNode);
                log.debug("Total missing tps is {}", missingTps);
                continue;
            }

            desiredTpsPerNode.put(node, newTpsForNode);
            log.debug("Tps on node {} will be changed to {}", node, newTpsForNode);
            missingTps = missingTps.subtract(newTpsForNode);
            log.debug("Total missing tps {}", tps);

        }

        return desiredTpsPerNode;
    }

    @Override
    public BigDecimal getDesiredTps() {
        return desiredTps.getDesiredTps();
    }

    private void initialize(Set<NodeId> nodes) {
        BigDecimal avgTps = desiredTps.get(clock.currentTimeMillis()).divide(new BigDecimal(nodes.size()), 3, BigDecimal.ROUND_HALF_UP);

        for (NodeId node : nodes) {
            desiredTpsPerNode.put(node, avgTps);
        }

    }
}
