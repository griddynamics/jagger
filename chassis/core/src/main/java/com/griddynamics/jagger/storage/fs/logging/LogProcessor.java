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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.TimeInvocationStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.TimeLatencyPercentile;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessDescriptiveStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.util.statistics.StatisticsCalculator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexey Kiselyov
 *         Date: 25.07.11
 */
public class LogProcessor extends HibernateDaoSupport {

    private static final Logger log = LoggerFactory.getLogger(LogProcessor.class);

    private List<Double> timeWindowPercentilesKeys;
    private List<Double> globalPercentilesKeys;


    protected TaskData getTaskData(final String taskId, final String sessionId) {
        return getHibernateTemplate().execute(new HibernateCallback<TaskData>() {
            @Override
            public TaskData doInHibernate(Session session) throws HibernateException, SQLException {
                return (TaskData) session.createQuery("select t from TaskData t where sessionId=? and taskId=?")
                        .setParameter(0, sessionId)
                        .setParameter(1, taskId)
                        .uniqueResult();
            }
        });
    }

    protected TimeInvocationStatistics assembleInvocationStatistics(long time, StatisticsCalculator calculator,
                                                                    Double throughput, TaskData taskData) {
        TimeInvocationStatistics statistics = new TimeInvocationStatistics(
                time,
                calculator.getMean() / 1000,
                calculator.getStandardDeviation() / 1000,
                throughput,
                taskData
        );

        List<TimeLatencyPercentile> percentiles = new ArrayList<TimeLatencyPercentile>();
        for (double percentileKey : getTimeWindowPercentilesKeys()) {
            double percentileValue = calculator.getPercentile(percentileKey);
            TimeLatencyPercentile percentile = new TimeLatencyPercentile(percentileKey, percentileValue);
            percentile.setTimeInvocationStatistics(statistics);
            percentiles.add(percentile);
        }
        if (!percentiles.isEmpty()) {
            statistics.setPercentiles(percentiles);
        }

        return statistics;
    }

    protected WorkloadProcessDescriptiveStatistics assembleDescriptiveScenarioStatistics(StatisticsCalculator calculator, TaskData taskData) {
        WorkloadProcessDescriptiveStatistics statistics = new WorkloadProcessDescriptiveStatistics();
        statistics.setTaskData(taskData);

        List<WorkloadProcessLatencyPercentile> percentiles = Lists.newArrayList();
        for (double percentileKey : getGlobalPercentilesKeys()) {
            double percentileValue = calculator.getPercentile(percentileKey);
            WorkloadProcessLatencyPercentile percentile = new WorkloadProcessLatencyPercentile(percentileKey, percentileValue);
            percentile.setWorkloadProcessDescriptiveStatistics(statistics);
            if (Double.isNaN(percentileKey) || Double.isNaN(percentileValue))
                log.error("Percentile has NaN values : key={}, value={}", percentileKey, percentileValue);
            percentiles.add(percentile);
        }
        if (!percentiles.isEmpty()) {
            statistics.setPercentiles(percentiles);
        }
        return statistics;
    }

    @Required
    public void setTimeWindowPercentilesKeys(List<Double> timeWindowPercentilesKeys) {
        this.timeWindowPercentilesKeys = timeWindowPercentilesKeys;
    }

    @Required
    public void setGlobalPercentilesKeys(List<Double> globalPercentilesKeys) {
        this.globalPercentilesKeys = globalPercentilesKeys;
    }


    public List<Double> getTimeWindowPercentilesKeys() {
        return timeWindowPercentilesKeys;
    }

    public List<Double> getGlobalPercentilesKeys() {
        return globalPercentilesKeys;
    }
}
