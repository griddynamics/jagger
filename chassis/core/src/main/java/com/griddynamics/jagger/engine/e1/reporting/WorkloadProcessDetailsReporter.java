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

package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessDescriptiveStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WorkloadProcessDetailsReporter extends AbstractMappedReportProvider<String> {

    private static final Logger log = LoggerFactory.getLogger(WorkloadProcessDetailsReporter.class);

    public static class WorkloadProcessDetailsDTO {
        private String key;
        private String value;

        public WorkloadProcessDetailsDTO(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public JRDataSource getDataSource(String key) {
        @SuppressWarnings("unchecked")
        List<WorkloadProcessDescriptiveStatistics> statistics = getHibernateTemplate().find(
                "select s from WorkloadProcessDescriptiveStatistics s where s.taskData.taskId=? and s.taskData.sessionId=?",
                key, getSessionIdProvider().getSessionId());

        if(statistics == null || statistics.isEmpty()) {
            log.error("Data for process [" + key + "] not found");
            return null;
        }
        if(statistics.size() > 1) {
            log.warn("More than one statistics was found for process [{}]. Will take the first one.", key);
        }

        List<WorkloadProcessDetailsDTO> result = new ArrayList<WorkloadProcessDetailsDTO>();
        for(WorkloadProcessLatencyPercentile percentile : statistics.get(0).getPercentiles()) {
            WorkloadProcessDetailsDTO dto = new WorkloadProcessDetailsDTO(
                    String.format("%.0f", percentile.getPercentileKey()) + "% -",
                    String.format("%.3fs", percentile.getPercentileValue() / 1000));
            result.add(dto);
        }

        return new JRBeanCollectionDataSource(result);
    }
}
