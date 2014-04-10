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
package com.griddynamics.jagger.monitoring.reporting;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.dbapi.entity.PerformedMonitoring;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.List;

public class MonitoringReporter extends AbstractReportProvider {

    @Override
    public JRDataSource getDataSource() {
        String sessionId = getSessionIdProvider().getSessionId();
        @SuppressWarnings("unchecked")
        List<PerformedMonitoring> source = getHibernateTemplate().find("select pm from PerformedMonitoring pm " +
                "where pm.sessionId=? and pm.parentId is null", sessionId);

        List<MonitoringInfo> result = Lists.newLinkedList();

        for (PerformedMonitoring performedMonitoring : source) {
            MonitoringInfo info = new MonitoringInfo();
            info.setTaskId(performedMonitoring.getMonitoringId());
            info.setTitle(performedMonitoring.getName() + ", " + performedMonitoring.getTermination());
            result.add(info);
        }

        return new JRBeanCollectionDataSource(result);
    }

    public static class MonitoringInfo {
        private String title;
        private String taskId;

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MonitoringInfo that = (MonitoringInfo) o;

            if (taskId != null ? !taskId.equals(that.taskId) : that.taskId != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return taskId != null ? taskId.hashCode() : 0;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
