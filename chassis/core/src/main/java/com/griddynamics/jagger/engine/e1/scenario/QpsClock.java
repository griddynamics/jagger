package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * Workload Clock to control qps load
 */
public class QpsClock implements WorkloadClock {

    private final int tickInterval;
    private final int maxThreads;
    private final DesiredTps desiredQps;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public QpsClock(int tickInterval, int maxThreads, DesiredTps desiredTps) {
        this.tickInterval = tickInterval;
        this.maxThreads = maxThreads;
        this.desiredQps = desiredTps;
    }

    @Override
    public Map<NodeId, Integer> getPoolSizes(Set<NodeId> nodes) {
        Map<NodeId, Integer> result = Maps.newHashMap();

        for (NodeId node : nodes) {
            result.put(node, maxThreads);
        }

        return result;
    }

    private final BigDecimal ONE_TENTH = BigDecimal.valueOf(1D / 10D);

    @Override
    public void tick(WorkloadExecutionStatus status, WorkloadAdjuster adjuster) {

        // spare qps between Nodes
        Set<NodeId> nodes = status.getNodes();

        BigDecimal currentQps = desiredQps.get(System.currentTimeMillis());
        log.debug("current qps value to schedule = {} ", currentQps);
        if (currentQps.compareTo(ONE_TENTH) < 0) {
            currentQps = ONE_TENTH;
            log.debug("new calculated qps : " + currentQps);
        }
        // period per node in milliseconds
        long periodPerNode = BigDecimal.valueOf(1000L).divide(currentQps, BigDecimal.ROUND_DOWN).longValue()
                                * nodes.size();


        log.debug("period per node = {}", periodPerNode);
        for(NodeId node : status.getNodes()) {

            WorkloadConfiguration workloadConfiguration = WorkloadConfiguration.with(
                    periodPerNode,
                    maxThreads);

            log.debug("adjust workload configuration {} to node with identifier {}", workloadConfiguration, node.getIdentifier());

            adjuster.adjustConfiguration(node, workloadConfiguration);
        }
    }


    @Override
    public int getTickInterval() {
        return tickInterval;
    }

    @Override
    public int getValue() {
        return desiredQps.getDesiredTps().intValue();
    }

    @Override
    public String toString() {
        return getValue() + " rps";
    }
}
