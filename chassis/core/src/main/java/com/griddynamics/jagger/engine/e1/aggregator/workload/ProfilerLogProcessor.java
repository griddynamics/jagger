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
package com.griddynamics.jagger.engine.e1.aggregator.workload;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.dbapi.entity.ProfilingSuT;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.diagnostics.thread.sampling.ProfileDTO;
import com.griddynamics.jagger.diagnostics.thread.sampling.RuntimeGraph;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.monitoring.MonitorProcess;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.fs.logging.LogProcessor;
import com.griddynamics.jagger.storage.fs.logging.LogReader;
import com.griddynamics.jagger.util.SerializationUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ProfilerLogProcessor extends LogProcessor implements DistributionListener {
    private static Logger log = LoggerFactory.getLogger(ProfilerLogProcessor.class);
    private FileStorage fileStorage;
    private LogReader logReader;

    @Required
    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        // do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        if (task instanceof CompositeTask) {
            log.debug("Going to aggregate profiler details for task id {}", taskId);
            aggregateLogs(sessionId, taskId);
            log.debug("Profiler  details aggregation completed for task id {}", taskId);
        }
    }

    private void aggregateLogs(String sessionId, String taskId) {
        try {
            saveProfiles(sessionId, taskId);
        } catch (Exception e) {
            log.error("Error during log processing", e);
        }
    }

    private void saveProfiles(final String sessionId, final String taskId) throws IOException {
        String dir;
        dir = sessionId + "/" + taskId + "/" + MonitorProcess.PROFILER_MARKER;

        Set<String> fileNameList = fileStorage.getFileNameList(dir);
        if (fileNameList.isEmpty()) {
            log.debug("Directory {} is empty.", dir);
            return;
        }

        final TaskData taskData = getTaskData(taskId, sessionId);
        if (taskData == null) {
            log.error("TaskData not found by sessionId: {} and taskId: {}", sessionId, taskId);
            return;
        }

        for (final String fileName : fileNameList) {
            LogReader.FileReader reader;
            try {
                reader = logReader.read(fileName, Object.class);
            } catch (IllegalArgumentException e) {
                log.warn(e.getMessage(), e);
                return;
            }
            final ProfileDTO profileDTO = SerializationUtils.fromString(reader.iterator().next().toString());

            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    String prefix = "Agent on (" + profileDTO.getHostAddress() + ") : ";
                    for (Map.Entry<String, RuntimeGraph> runtimeGraphEntry : profileDTO.getRuntimeGraphs().entrySet()) {
                        String context = SerializationUtils.toString(runtimeGraphEntry.getValue());
                        session.persist(new ProfilingSuT(prefix + runtimeGraphEntry.getKey(), sessionId,
                                taskData, context));
                    }
                    session.flush();
                    return null;
                }
            });
        }
    }
}
