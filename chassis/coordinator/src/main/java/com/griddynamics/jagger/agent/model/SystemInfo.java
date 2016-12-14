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

package com.griddynamics.jagger.agent.model;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.dbapi.parameter.MonitoringParameter;

import java.io.Serializable;
import java.util.Map;

/**
 * User: vshulga, akiselyov
 * Date: 7/5/11
 * Time: 5:17 PM
 */
public class SystemInfo implements Serializable {

    private NodeId nodeId;

    private Map<MonitoringParameter, Double> sysInfo;

    private Map<String, SystemUnderTestInfo> sysUnderTest;

    private long time;

    @Override
    public String toString() {
        return "SystemInfo {nodeId: " + this.nodeId + ", time: " + this.time +
                ", sysInfo: [" + this.sysInfo + "], " + ", sysUnderTest: [" + this.sysUnderTest + "]}";
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Map<MonitoringParameter, Double> getSysInfo() {
        return sysInfo;
    }

    public void setSysInfo(Map<MonitoringParameter, Double> sysInfo) {
        this.sysInfo = sysInfo;
    }

    public void setNodeId(NodeId nodeId) {
        this.nodeId = nodeId;
    }

    public NodeId getNodeId() {
        return nodeId;
    }

    public void setSysUnderTest(Map<String, SystemUnderTestInfo> sysUnderTest) {
        this.sysUnderTest = sysUnderTest;
    }

    public Map<String, SystemUnderTestInfo> getSysUnderTest() {
        return sysUnderTest;
    }
}
