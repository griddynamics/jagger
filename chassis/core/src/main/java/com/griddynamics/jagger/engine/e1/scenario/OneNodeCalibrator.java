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

import com.griddynamics.jagger.coordinator.Coordination;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.engine.e1.process.PerformCalibration;
import com.griddynamics.jagger.invoker.ScenarioFactory;
import com.griddynamics.jagger.util.TimeoutsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Collects calibration info on the single node.
 *
 * @author Mairbek Khadikov
 */
public class OneNodeCalibrator implements Calibrator {
    private final static Logger log = LoggerFactory.getLogger(OneNodeCalibrator.class);

    @Override
    public void calibrate(String sessionId, String taskId, ScenarioFactory<Object, Object, Object> scenarioFactory, Map<NodeId, RemoteExecutor> remotes, long timeout) {
        checkArgument(!remotes.isEmpty());

        log.debug("Going to perform calibration sessionId {} taskId {}", sessionId, taskId);

        Map.Entry<NodeId, RemoteExecutor> entry = remotes.entrySet().iterator().next();

        NodeId node = entry.getKey();
        RemoteExecutor remote = entry.getValue();


        Boolean result = remote.runSyncWithTimeout(PerformCalibration.create(sessionId, taskId, scenarioFactory), Coordination.<PerformCalibration>doNothing(), timeout);

        if (result) {
            log.info("Calibration info collected");
        } else {
            log.warn("Failed to collect calibration info");
        }
        log.debug("Calibration will perform on node {}", node);
    }

    @Override
    public String toString() {
        return "Perform calibration on single node";
    }
}
