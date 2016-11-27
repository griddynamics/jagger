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

/**
 * @author Nikolay Musienko
 *         Date: 05.07.13
 */

public class CpuData {

    double cpuStateSys = 0;
    double cpuStateUser = 0;
    double cpuStateWait = 0;
    double cpuStateIdle = 0;
    double cpuStateCombined = 0;

    public double getCpuStateSys() {
        return cpuStateSys;
    }

    public void setCpuStateSys(double cpuStateSys) {
        this.cpuStateSys = cpuStateSys;
    }

    public double getCpuStateUser() {
        return cpuStateUser;
    }

    public void setCpuStateUser(double cpuStateUser) {
        this.cpuStateUser = cpuStateUser;
    }

    public double getCpuStateWait() {
        return cpuStateWait;
    }

    public void setCpuStateWait(double cpuStateWait) {
        this.cpuStateWait = cpuStateWait;
    }

    public double getCpuStateIdle() {
        return cpuStateIdle;
    }

    public void setCpuStateIdle(double cpuStateIdle) {
        this.cpuStateIdle = cpuStateIdle;
    }

    public double getCpuStateCombined() {
        return cpuStateCombined;
    }

    public void setCpuStateCombined(double cpuStateCombined) {
        this.cpuStateCombined = cpuStateCombined;
    }

    @Override
    public String toString() {
        return "CpuData{" +
                "cpuStateSys=" + cpuStateSys +
                ", cpuStateUser=" + cpuStateUser +
                ", cpuStateWait=" + cpuStateWait +
                ", cpuStateIdle=" + cpuStateIdle +
                ", cpuStateCombined=" + cpuStateCombined +
                '}';
    }
}
