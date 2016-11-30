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

package com.griddynamics.jagger.engine.e1.process;

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadConfiguration;

public class ChangeWorkloadConfiguration implements Command<Boolean> {
    private String sessionId;
    private String processId;
    private WorkloadConfiguration configuration;

    public static ChangeWorkloadConfiguration create(String sessionId, String processId, WorkloadConfiguration configuration) {
        ChangeWorkloadConfiguration result = new ChangeWorkloadConfiguration();
        result.setSessionId(sessionId);
        result.setProcessId(processId);
        result.setConfiguration(configuration);
        return result;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setConfiguration(WorkloadConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public WorkloadConfiguration getConfiguration() {
        return configuration;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
