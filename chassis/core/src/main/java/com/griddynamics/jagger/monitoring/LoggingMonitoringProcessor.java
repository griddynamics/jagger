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

package com.griddynamics.jagger.monitoring;

import com.griddynamics.jagger.agent.model.SystemInfo;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MonitoringLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;

/**
 * Default implementation of {@link MonitoringProcessor}
 */
public class LoggingMonitoringProcessor implements MonitoringProcessor {
    private Logger log = LoggerFactory.getLogger(LoggingMonitoringProcessor.class);
    private LogWriter logWriter;

    public static final String MONITORING_MARKER = "MONITORING";

    @Override
    public void process(String sessionId, String taskId, NodeId agentId, SystemInfo systemInfo) {
        logWriter.log(sessionId, taskId + File.separatorChar + MONITORING_MARKER,
                agentId.getIdentifier(), new MonitoringLogEntry(systemInfo));
        log.trace("System info {} received from agent {} and has been written to FileStorage", systemInfo, agentId);
    }

    @Required
    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }
}
