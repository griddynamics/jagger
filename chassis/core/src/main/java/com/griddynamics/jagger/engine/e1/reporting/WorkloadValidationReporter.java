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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.ValidationResultEntity;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class WorkloadValidationReporter extends AbstractMappedReportProvider<String> {
    private static final Logger log = LoggerFactory.getLogger(WorkloadValidationReporter.class);

    public static class ValidationResult {
        private String validator;
        private int total;
        private int failed;
        private BigDecimal successPercent;

        public String getValidator() {
            return validator;
        }

        public void setValidator(String validator) {
            this.validator = validator;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
            this.failed = failed;
        }

        public BigDecimal getSuccessPercent() {
            return successPercent;
        }

        public void setSuccessPercent(BigDecimal successPercent) {
            this.successPercent = successPercent;
        }
    }

    @Override
    public JRDataSource getDataSource(String key) {
        String sessionId = getSessionIdProvider().getSessionId();
        @SuppressWarnings("unchecked")
        List<ValidationResultEntity> validationResults = getHibernateTemplate().find(
                "select v from ValidationResultEntity v where v.workloadData.taskId=? and v.workloadData.sessionId=?",
                key, sessionId);

        if (validationResults == null || validationResults.isEmpty()) {
            log.info("Validation info for task id " + key + "] not found");
            return null;
        }

        List<ValidationResult> result = Lists.newLinkedList();
        for (ValidationResultEntity entity : validationResults) {
            result.add(convert(entity));
        }
        return new JRBeanCollectionDataSource(result);
    }

    private ValidationResult convert(ValidationResultEntity entity) {
        Integer total = entity.getTotal();
        Integer failed = entity.getFailed();

        ValidationResult result = new ValidationResult();
        result.setValidator(entity.getValidator());
        result.setTotal(total);
        result.setFailed(failed);

        BigDecimal percentage = BigDecimal.ZERO;
        if (total == 0) {
            log.warn("No invocations for task with id {}", entity.getWorkloadData().getTaskId());
        } else {
            percentage = new BigDecimal(total - failed)
                    .divide(new BigDecimal(total), 3, BigDecimal.ROUND_HALF_UP);
        }

        result.setSuccessPercent(percentage);

        return result;
    }
}
