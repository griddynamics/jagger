package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ExactInvocationsClock implements WorkloadClock {

    private static final Logger log = LoggerFactory.getLogger(ExactInvocationsClock.class);

    private static final int SAMPLES_COUNT_SPLITTING_FACTOR = 4;

    private int threadCount;

    private int samplesCount;

    private int samplesSubmitted;

    private int totalSamples;

    private int delay;

    private int tickCount = 0;

    private Map<NodeId, WorkloadConfiguration> submittedConfigurations = new HashMap<NodeId, WorkloadConfiguration>();

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

        tickCount ++;
        totalSamples = status.getTotalSamples();
        int avgSamplesPerTick = totalSamples / tickCount;

        int samplesLeft = samplesCount - samplesSubmitted;
        if (samplesLeft <= 0) {
            return;
        }

        if (status.getTotalSamples() < samplesSubmitted / 2) {
            return;
        }

        Set<NodeId> nodes = status.getNodes();
        int threads =  threadCount / nodes.size();
        int residue = threadCount % nodes.size();
        int samplesToAdd = (samplesLeft <= SAMPLES_COUNT_SPLITTING_FACTOR || samplesLeft < avgSamplesPerTick * 1.5) ?
                samplesLeft : samplesLeft / SAMPLES_COUNT_SPLITTING_FACTOR;
        int s = 0;
        for (NodeId node : nodes) {
            int submittedSamplesCount = submittedConfigurations.get(node) != null ? submittedConfigurations.get(node).getSamples() : 0;
            int curThreads = threads;
            if (residue > 0) {
                threads += residue--;
            }
            int samples = submittedSamplesCount;
            if (samplesToAdd < nodes.size()) {
                samples += samplesToAdd;
                samplesToAdd = 0;
            } else {
                samples += Math.round((double) samplesToAdd * curThreads / threadCount);
            }

            WorkloadConfiguration workloadConfiguration = WorkloadConfiguration.with(curThreads, delay, samples);
            adjuster.adjustConfiguration(node, workloadConfiguration);
            s += samples;
            submittedConfigurations.put(node, workloadConfiguration);
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
