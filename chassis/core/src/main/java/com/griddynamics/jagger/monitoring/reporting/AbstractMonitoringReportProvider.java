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
package com.griddynamics.jagger.monitoring.reporting;

import com.griddynamics.jagger.dbapi.entity.WorkloadData;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;

import java.util.List;

public abstract class AbstractMonitoringReportProvider<T> extends AbstractMappedReportProvider<T> {

    public void clearCache() {
    }

    protected String parentOf(String workloadTaskId) {
        String sessionId = getSessionIdProvider().getSessionId();
        List<WorkloadData> list = (List<WorkloadData>) getHibernateTemplate().find("select wd from WorkloadData wd where wd.sessionId =? and wd.taskId =? and wd.parentId is not null", sessionId, workloadTaskId);

        if (list.size() == 1) {
            return list.get(0).getParentId();
        }

        return null;
    }
}
