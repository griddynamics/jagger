package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExactInvocationsClock implements WorkloadClock {

    private static final Logger log = LoggerFactory.getLogger(ExactInvocationsClock.class);

    private int threadCount;

    private int samplesCount;

    private int samplesSubmitted;

    private int delay;

    private int tickInterval;

    public ExactInvocationsClock(int samplesCount, int threadCount, int delay, int tickInterval) {
        this.samplesCount = samplesCount;
        this.threadCount  = threadCount;
        this.delay        = delay;
        this.tickInterval = tickInterval;
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

        int samplesLeft = samplesCount - samplesSubmitted;
        if (samplesLeft <= 0) {
            return;
        }

        Set<NodeId> nodes = status.getNodes();
        int nodesSize = nodes.size();
        int threadsForOneNode =  threadCount / nodesSize;
        int threadsResidue = threadCount % nodesSize;

        int denominator = (threadsForOneNode == 0) ?  threadsResidue :  threadsForOneNode;
        int samplesForOneThread = samplesLeft / denominator;

        int samplesResidueByThreads = samplesLeft % denominator;
        int additionalSamplesForOneNode = samplesResidueByThreads / nodesSize;
        int samplesResidue = samplesResidueByThreads % nodesSize;

        int s = 0;
        for (NodeId node : nodes) {
            int curSamples = 0;
            int curThreads = threadsForOneNode;
            if (threadsResidue > 0) {
                curThreads ++;
                threadsResidue --;
            }

            curSamples += samplesForOneThread * curThreads + additionalSamplesForOneNode;
            if (samplesResidue > 0) {
                curSamples ++;
                samplesResidue --;
            }
            WorkloadConfiguration workloadConfiguration = WorkloadConfiguration.with(curThreads, delay, curSamples);
            adjuster.adjustConfiguration(node, workloadConfiguration);
            s += curSamples;
        }
        samplesSubmitted = s;
    }

    @Override
    public int getTickInterval() {
        return tickInterval;
    }

    @Override
    public int getValue() {
        return samplesCount;
    }

    @Override
    public String toString() {
        return threadCount + " virtual users";
    }
}
