package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: amikryukov
 * Date: 6/11/13
 */
public class FixedRateClock implements WorkloadClock {

    private static final Logger log = LoggerFactory.getLogger(FixedRateClock.class);

    private final int rate;
    private final int tickInterval;
    private final int threadCount;
    private final int delay;
    private int samplesSubmitted = 0;
    private Map<NodeId, WorkloadConfiguration> submittedConfigurations = new HashMap<NodeId, WorkloadConfiguration>();

    public FixedRateClock(int tickInterval, int rate, int threadCount, int delay) {
        this.tickInterval = tickInterval;
        this.rate = rate;
        this.threadCount = threadCount;
        this.delay = delay;
    }

    @Override
    public Map<NodeId, Integer> getPoolSizes(Set<NodeId> nodes) {
        int max = threadCount / nodes.size() + 1;
        Map<NodeId, Integer> result = Maps.newHashMap();
        for (NodeId node : nodes) {
            result.put(node, max);
        }
        return result;
    }

    @Override
    public void tick(WorkloadExecutionStatus status, WorkloadAdjuster adjuster) {

        log.debug("Going to perform tick with status {}", status);

        if (status.getTotalSamples() < samplesSubmitted / 2) {
            return;
        }

        Map<NodeId, Double>  factors = calculateFactors(status, submittedConfigurations);
        int avgThreads = threadCount / status.getNodes().size();
        int residueThreads = threadCount % status.getNodes().size();

        for (NodeId nodeId : status.getNodes()) {

            int curThreads = residueThreads > 0 ? avgThreads + 1 : avgThreads;
            int curSamples = status.getSamples(nodeId);
            curSamples += Math.nextUp(rate * factors.get(nodeId));

            WorkloadConfiguration conf = WorkloadConfiguration.with(curThreads, delay, curSamples);
            adjuster.adjustConfiguration(nodeId, conf);
            submittedConfigurations.put(nodeId, conf);
        }
    }


    private Map<NodeId, Double> calculateFactors(WorkloadExecutionStatus status, Map<NodeId, WorkloadConfiguration> configurations) {
        Map<NodeId, Double> result = new HashMap<NodeId, Double>();

        Map<NodeId, Double> scores = new HashMap<NodeId, Double>();
        int nodesCount = status.getNodes().size();
        double scoreSum = 0;
        for (NodeId nodeId: status.getNodes()) {
            double totalSamplesRate = (status.getTotalSamples() == 0) ?
                    (1d / nodesCount) :
                    (double) status.getSamples(nodeId) / status.getTotalSamples();

            double score = totalSamplesRate;
            scores.put(nodeId, score);
            scoreSum += score;
        }

        for (NodeId nodeId: status.getNodes()) {
            result.put(nodeId, scores.get(nodeId) / scoreSum);
        }
        return result;
    }

    @Override
    public int getTickInterval() {
        return tickInterval;
    }

    @Override
    public int getValue() {
        return (int) (((double)rate * 1000) / tickInterval );
    }

    @Override
    public String toString() {
        return "fixed-rate clock with rate " + rate + " with tick interval " + tickInterval;
    }
}
