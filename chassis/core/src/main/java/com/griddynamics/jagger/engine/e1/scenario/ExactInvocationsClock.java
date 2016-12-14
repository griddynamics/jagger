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

    private long period;

    public ExactInvocationsClock(int samplesCount, int threadCount, int delay, int tickInterval, long period) {
        this.samplesCount = samplesCount;
        this.threadCount  = threadCount;
        this.delay        = delay;
        this.tickInterval = tickInterval;
        this.period = period;
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

    private long startTime = 0;

    @Override
    public void tick(WorkloadExecutionStatus status, WorkloadAdjuster adjuster) {
        log.debug("Going to perform tick with status {}", status);

        if (isPeriodic()) {

            long currentTime = System.currentTimeMillis();
            if (startTime == 0) {
                startTime = currentTime - period;
            }
            long difference = currentTime - startTime;
            if(difference >= period) {
                if (status.getTotalSamples() < samplesSubmitted) {
                    log.warn("Can not create such load with {} invocations with {} ms period", samplesCount, period);
                }
                startTime = startTime + period;
                sendSamples(samplesCount, status, adjuster);
            }
        } else {

            int samplesLeft = samplesCount - samplesSubmitted;
            sendSamples(samplesLeft, status, adjuster);
        }
    }


    private void sendSamples(int samplesLeft, WorkloadExecutionStatus status, WorkloadAdjuster adjuster) {

        if (samplesLeft <= 0) {
            return;
        }

        Set<NodeId> nodes = status.getNodes();
        int nodesSize = nodes.size();
        int threadsForOneNode =  threadCount / nodesSize;                      // how many threads should be distributed for one node(kernel)
        int threadsResidue = threadCount % nodesSize;                          // residue of threads

        int samplesForOneThread = samplesLeft / threadCount;                   //how many samples should be distributed for ont thread

        int samplesResidueByThreads = samplesLeft % threadCount;               // residue samples of threads
        int additionalSamplesForOneNode = samplesResidueByThreads / nodesSize; // how many samples should be added for each node(kernel)
        int samplesResidue = samplesResidueByThreads % nodesSize;              // residue samples of nodes

        int s = 0;
        for (NodeId node : nodes) {
            int curSamples = status.getSamples(node);
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

    private boolean isPeriodic() {
        return period != -1;
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
