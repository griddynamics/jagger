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

package com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring;

import com.griddynamics.jagger.dbapi.entity.MonitoringStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class DefaultMonitoringSummaryRetriever extends HibernateDaoSupport implements MonitoringSummaryRetriever {
    private static final Logger log = LoggerFactory.getLogger(DefaultMonitoringSummaryRetriever.class);

    @Override
    public MonitoringSummary load(String sessionId, String taskId) {
        log.debug("Loading stats for session '{}', task '{}' ");

        List<MonitoringStatistics> allStats = loadMonitoringStatistics(sessionId, taskId);

        log.debug("{} stats found", allStats.size());

        MonitoringSummary.Builder builder = MonitoringSummary.builder();

        for (MonitoringStatistics allStat : allStats) {
            String source = allStat.getBoxIdentifier();
            if (source == null) {
                source = allStat.getSystemUnderTestUrl();
            }
            String value = allStat.getParameterId().getDescription();
            long time = allStat.getTime();
            Double data = allStat.getAverageValue();
            builder.add(source, value, time, data);
        }

        return builder.build();
    }


    @SuppressWarnings("unchecked")
    private List<MonitoringStatistics> loadMonitoringStatistics(String sessionId, String taskId) {
        // todo [mairbek] review this query
        return getHibernateTemplate().find(
                "select ms from MonitoringStatistics ms where ms.sessionId =? and ms.taskData.taskId = ?", sessionId, taskId
        );
    }
}
