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

package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.sessioncomparation.BaselineSessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LimitSet {
    private static final Logger log = LoggerFactory.getLogger(LimitSet.class);

    private List<Limit> limits = Collections.emptyList();
    private String id;
    private BaselineSessionProvider baselineSessionProvider;
    private LimitSetConfig limitSetConfig;

    public void setLimits(List<Limit> limits) {

        removeDuplicates(limits);
        checkThresholdsRelation(limits);

        this.limits = limits;
    }

    public List<Limit> getLimits() {
        return limits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaselineId(String sessionId) {
        return baselineSessionProvider.getBaselineSession(sessionId);
    }

    public void setBaselineSessionProvider(BaselineSessionProvider baselineSessionProvider) {
        this.baselineSessionProvider = baselineSessionProvider;
    }

    public LimitSetConfig getLimitSetConfig() {
        return limitSetConfig;
    }

    public void setLimitSetConfig(LimitSetConfig limitSetConfig) {
        this.limitSetConfig = limitSetConfig;
    }

    // relation single metric - single limit is important
    // due to storage in database and displaying in UI
    private void removeDuplicates(List<Limit> inputList) {
        Set<String> params = new HashSet<String>();
        String param;
        List<Limit> duplicates = new ArrayList<Limit>();

        for(Limit limit : inputList) {
            param = limit.getMetricName();

            if(params.contains(param)) {
                duplicates.add(limit);
                log.error("Limit with metricName '" + param + "' already exists. New limit with the same name will be ignored");
            }
            params.add(param);
        }

        inputList.removeAll(duplicates);
    }

    private void checkThresholdsRelation(List<Limit> inputList) {
        List<Limit> limitsWithErrors = new ArrayList<Limit>();

        for(Limit limit : inputList) {
            if (limit.getLowerErrorThreshold() > limit.getLowerWarningThreshold()) {
                limitsWithErrors.add(limit);
                log.error("Limit with metricName '" + limit.getMetricName() +
                        "' has wrong relation of thresholds. LowerErrorThreshold "+ limit.getLowerErrorThreshold() +
                        " should be less than LowerWarningThreshold " + limit.getLowerWarningThreshold());
                continue;
            }
            if (limit.getLowerWarningThreshold() > limit.getUpperWarningThreshold()) {
                limitsWithErrors.add(limit);
                log.error("Limit with metricName '" + limit.getMetricName() +
                        "' has wrong relation of thresholds. LowerWarningThreshold " + limit.getLowerWarningThreshold() +
                        " should be less than UpperWarningThreshold " + limit.getUpperWarningThreshold());
                continue;
            }
            if (limit.getUpperWarningThreshold() > limit.getUpperErrorThreshold()) {
                limitsWithErrors.add(limit);
                log.error("Limit with metricName '" + limit.getMetricName() +
                        "' has wrong relation of thresholds. UpperWarningThreshold " + limit.getUpperWarningThreshold() +
                        " should be less than UpperErrorThreshold " + limit.getUpperErrorThreshold());
                continue;
            }
        }

        inputList.removeAll(limitsWithErrors);
    }

}

