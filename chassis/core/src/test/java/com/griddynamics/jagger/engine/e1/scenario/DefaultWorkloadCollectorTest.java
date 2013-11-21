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
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.util.TimeoutsConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

public class DefaultWorkloadCollectorTest {
    private DefaultWorkloadController controller;
    private String sessionId;
    private String taskId;
    private WorkloadTask task;
    private Map<NodeId, RemoteExecutor> remotes;
    private Long startTime;

    @BeforeMethod
    public void setUp() throws Exception {
        sessionId = "testSession";
        taskId = "testWorkload";
        task = new WorkloadTask();
        remotes = Maps.newHashMap();
        startTime = System.currentTimeMillis();
        controller = new DefaultWorkloadController(sessionId, taskId, task, remotes, TimeoutsConfiguration.getDefaultTimeouts(), startTime);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldFailWhenGetStatusCalledBeforeProcessStart() throws Exception {
        controller.getStatus();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldFailWhenWorkloadIsStartedSeveralTimes() throws Exception {
        controller.startWorkload(Maps.<NodeId, Integer>newHashMap());
        controller.startWorkload(Maps.<NodeId, Integer>newHashMap());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldFailWhenWorkloadIsStoppedWithoutBeingStarted() throws Exception {
        controller.stopWorkload();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void shouldFailToAdjustWorkloadTaskNumberBeforeTestsIsStarted() throws Exception {
        controller.adjustConfiguration(NodeId.kernelNode("test"), WorkloadConfiguration.with(1, 0));
    }

}
