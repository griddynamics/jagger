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

package com.griddynamics.jagger.master;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author Alexey Kiselyov
 *         Date: 08.07.11
 */
public class Conditions {

    private boolean monitoringEnable;
    private int minAgentsCount;
    private int maxAgentsCount;

    private int minKernelsCount;
    private int maxKernelsCount;

    @Required
    public void setMonitoringEnable(boolean monitoringEnable) {
        this.monitoringEnable = monitoringEnable;
    }

    @Required
    public void setMinKernelsCount(int minKernelsCount) {
        this.minKernelsCount = minKernelsCount;
    }

    public boolean isMonitoringEnable() {
        return monitoringEnable;
    }

    public void setMinAgentsCount(int minAgentsCount) {
        this.minAgentsCount = minAgentsCount;
    }

    public int getMinAgentsCount() {
        return minAgentsCount;
    }

    public void setMaxAgentsCount(int maxAgentsCount) {
        this.maxAgentsCount = maxAgentsCount;
    }

    public int getMaxAgentsCount() {
        return maxAgentsCount;
    }

    public int getMinKernelsCount() {
        return minKernelsCount;
    }

    public int getMaxKernelsCount() {
        return maxKernelsCount;
    }

    public void setMaxKernelsCount(int maxKernelsCount) {
        this.maxKernelsCount = maxKernelsCount;
    }
}
