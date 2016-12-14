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

package com.griddynamics.jagger.engine.e1.collector;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.*;

/**
 * Saves generic information about e1 session execution. Such as task execution
 * start/end time, thread count, samples count, available kernels.
 *
 * @author Mairbek Khadikov
 */
public class MasterWorkloadCollector implements DistributionListener {
    private final static Logger log = LoggerFactory.getLogger(MasterWorkloadCollector.class);

    private KeyValueStorage keyValueStorage;

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        log.debug("Going to collect master info");
        if (task instanceof WorkloadTask) {
            putValues(sessionId, taskId, capableNodes, (WorkloadTask) task);
        } else {
            log.debug("task {} is not a workload task. ignored", task);
        }
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        if (task instanceof WorkloadTask) {
            keyValueStorage.put(Namespace.of(sessionId, taskId), END_TIME, System.currentTimeMillis());
        }
    }

    private void putValues(String sessionId, String taskId, Collection<NodeId> capableNodes, WorkloadTask workload) {
        Namespace sessionNamespace = Namespace.of(SESSION, sessionId);
        keyValueStorage.put(sessionNamespace, SCENARIOS, taskId);

        Namespace scenarioNamespace = Namespace.of(sessionId, taskId);
        Multimap<String, Object> objectsMap = HashMultimap.create();
        objectsMap.put(START_TIME, System.currentTimeMillis() + workload.getStartDelay());

        objectsMap.put(CLOCK, workload.getClock().toString());
        objectsMap.put(CLOCK_VALUE, workload.getClock().getValue());
        objectsMap.put(TERMINATION, workload.getTerminateStrategyConfiguration().toString());

        objectsMap.put(KERNEL_COUNT, capableNodes.size());
        for (NodeId nodeId : capableNodes) {
            String nodeStr = nodeId.toString();
            log.debug("kernels: {}", nodeStr);
            objectsMap.put(KERNELS, nodeStr);
        }
        keyValueStorage.putAll(scenarioNamespace, objectsMap);
    }

    public void setKeyValueStorage(KeyValueStorage keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
    }
}
