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
package com.griddynamics.jagger.engine.e1.aggregator.workload.model;

import com.griddynamics.jagger.util.GeneralNodeInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class NodeInfoEntity {

    @Id
    @GeneratedValue
    @Column
    private Long id;

    @Column
    private String sessionId;
    @Column
    private String nodeId;
    @Column
    private long systemTime;
    @Column
    private String osName;
    @Column
    private String osVersion;
    @Column
    private String javaVersion;
    @Column
    private String cpuModel;
    @Column
    private int cpuMHz;
    @Column
    private int cpuTotalCores;
    @Column
    private int cpuTotalSockets;
    @Column
    private long systemRAM;

    public NodeInfoEntity(String sessionId, GeneralNodeInfo generalNodeInfo) {
        this.sessionId          = sessionId;
        this.nodeId             = generalNodeInfo.getNodeId();
        this.systemTime         = generalNodeInfo.getSystemTime();
        this.osName             = generalNodeInfo.getOsName();
        this.osVersion          = generalNodeInfo.getOsVersion();
        this.javaVersion        = generalNodeInfo.getJavaVersion();
        this.cpuModel           = generalNodeInfo.getCpuModel();
        this.cpuMHz             = generalNodeInfo.getCpuMHz();
        this.cpuTotalCores      = generalNodeInfo.getCpuTotalCores();
        this.cpuTotalSockets    = generalNodeInfo.getCpuTotalSockets();
        this.systemRAM          = generalNodeInfo.getSystemRAM();
    }

    public NodeInfoEntity() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getSystemRAM() {
        return systemRAM;
    }

    public void setSystemRAM(long systemRAM) {
        this.systemRAM = systemRAM;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public int getCpuTotalSockets() {
        return cpuTotalSockets;
    }

    public void setCpuTotalSockets(int cpuTotalSockets) {
        this.cpuTotalSockets = cpuTotalSockets;
    }

    public String getCpuModel() {
        return cpuModel;
    }

    public void setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
    }

    public int getCpuMHz() {
        return cpuMHz;
    }

    public void setCpuMHz(int cpuMHz) {
        this.cpuMHz = cpuMHz;
    }

    public int getCpuTotalCores() {
        return cpuTotalCores;
    }

    public void setCpuTotalCores(int cpuTotalCores) {
        this.cpuTotalCores = cpuTotalCores;
    }
}
