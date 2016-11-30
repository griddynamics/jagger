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

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.VoidResult;

import java.util.List;

/**
 * @author Alexey Kiselyov
 *         Date: 23.08.11
 */
public class ManageCollectionProfileFromSuT implements Command<VoidResult> {

    public enum ManageHotSpotMethodsFromSuT {
        START_POLLING,
        STOP_POLLING
    }

    private long profilerPollingInterval;
    private List<String> includePatterns;
    private List<String> excludePatterns;
    private ManageHotSpotMethodsFromSuT action;

    private String sessionId;

    public ManageHotSpotMethodsFromSuT getAction() {
        return this.action;
    }

    public void setAction(ManageHotSpotMethodsFromSuT action) {
        this.action = action;
    }

    public ManageCollectionProfileFromSuT(String sessionId, ManageHotSpotMethodsFromSuT action, long profilerPollingInterval) {
        this.sessionId = sessionId;
        this.action = action;
        this.profilerPollingInterval = profilerPollingInterval;
    }

    public ManageCollectionProfileFromSuT() {
    }

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getProfilerPollingInterval() {
        return this.profilerPollingInterval;
    }

    public void setProfilerPollingInterval(long profilerPollingInterval) {
        this.profilerPollingInterval = profilerPollingInterval;
    }

    public List<String> getIncludePatterns() {
        return this.includePatterns;
    }

    public void setIncludePatterns(List<String> includePatterns) {
        this.includePatterns = includePatterns;
    }

    public List<String> getExcludePatterns() {
        return this.excludePatterns;
    }

    public void setExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns = excludePatterns;
    }

    @Override
    public String toString() {
        return "ManageCollectionProfileFromSuT{" +
                "profilerPollingInterval=" + profilerPollingInterval +
                ", includePatterns=" + includePatterns +
                ", excludePatterns=" + excludePatterns +
                ", action=" + action +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
