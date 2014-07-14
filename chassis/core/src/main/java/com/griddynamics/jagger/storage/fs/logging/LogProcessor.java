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

package com.griddynamics.jagger.storage.fs.logging;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Alexey Kiselyov
 *         Date: 25.07.11
 */
public class LogProcessor extends HibernateDaoSupport {

    protected DatabaseService databaseService;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    protected TaskData getTaskData(String taskId, String sessionId) {
        return databaseService.getTaskData(taskId, sessionId);
    }

    protected void persistAggregatedMetricValue(Number value, MetricDescriptionEntity md) {
        MetricSummaryEntity entity = new MetricSummaryEntity();
        entity.setTotal(value.doubleValue());
        entity.setMetricDescription(md);

        getHibernateTemplate().persist(entity);
    }

    protected MetricDescriptionEntity persistMetricDescription(String metricId, String displayName, TaskData taskData) {

        MetricDescriptionEntity metricDescription = new MetricDescriptionEntity();
        metricDescription.setMetricId(metricId);
        metricDescription.setDisplayName(displayName);
        metricDescription.setTaskData(taskData);
        getHibernateTemplate().persist(metricDescription);
        return metricDescription;
    }
}
