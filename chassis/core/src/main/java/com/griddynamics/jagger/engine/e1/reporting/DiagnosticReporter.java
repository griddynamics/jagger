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

import com.griddynamics.jagger.dbapi.entity.DiagnosticResultEntity;
import com.griddynamics.jagger.dbapi.entity.MetricSummaryEntity;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DiagnosticReporter extends AbstractMappedReportProvider<String> {
    private static final Logger log = LoggerFactory.getLogger(DiagnosticReporter.class);

    public static class DiagnosticResult {
        private double total;
        private String name;

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @Override
    public JRDataSource getDataSource(String key) {
        String sessionId = getSessionIdProvider().getSessionId();


        // check new model
        List<MetricSummaryEntity> metricSummaryEntities = getHibernateTemplate().find(
                "select d from MetricSummaryEntity d where d.metricDescription.taskData.taskId=? and d.metricDescription.taskData.sessionId=?",
                key, sessionId);

        if (metricSummaryEntities != null && !metricSummaryEntities.isEmpty()) {


            TreeSet<DiagnosticResult> treeSet = new TreeSet<DiagnosticResult>(new Comparator<DiagnosticResult>() {
                @Override
                public int compare(DiagnosticResult o1, DiagnosticResult o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (MetricSummaryEntity mse : metricSummaryEntities) {
                treeSet.add(convert(mse));
            }

            return new JRBeanCollectionDataSource(treeSet);
        } else {

            // check old model
            List<DiagnosticResultEntity> diagnosticResults = getHibernateTemplate().find(
                    "select v from DiagnosticResultEntity v where v.workloadData.taskId=? and v.workloadData.sessionId=?",
                    key, sessionId);


            if (diagnosticResults == null || diagnosticResults.isEmpty()) {
                log.info("Diagnostic info for task id " + key + "] not found");
                return null;
            }

            TreeSet<DiagnosticResult> treeSet = new TreeSet<DiagnosticResult>(new Comparator<DiagnosticResult>() {
                @Override
                public int compare(DiagnosticResult o1, DiagnosticResult o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (DiagnosticResultEntity entity : diagnosticResults) {
                treeSet.add(convert(entity));
            }

            return new JRBeanCollectionDataSource(treeSet);
        }
    }

    private DiagnosticResult convert(DiagnosticResultEntity entity) {
        String name  = entity.getName();
        Double total = entity.getTotal();

        DiagnosticResult result = new DiagnosticResult();
        result.setTotal(total);
        result.setName(name);

        return result;
    }

    private DiagnosticResult convert(MetricSummaryEntity entity) {
        String name  = entity.getDisplay();
        Double total = entity.getTotal();

        DiagnosticResult result = new DiagnosticResult();
        result.setTotal(total);
        result.setName(name);

        return result;
    }
}
