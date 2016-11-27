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

package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.dbapi.entity.WorkloadTaskData;
import com.griddynamics.jagger.util.Decision;
import org.apache.commons.math.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class DefaultSessionStatusDecisionMaker implements SessionStatusDecisionMaker {
    private static final Logger log = LoggerFactory.getLogger(DefaultSessionStatusDecisionMaker.class);
    private final double EPSILON = 0.0000001;
    private double successRateThreshold = 1.0;
    // key to avoid additional verification of success rate to set status of test
    // recommended way to use limits based decision making for this purpose
    private boolean doNotUseDeprecatedMethodToSetTestStatus = false;

    private String description;

    @Override
    public Decision decideOnTest(WorkloadTaskData workloadTaskData) {
        if (doNotUseDeprecatedMethodToSetTestStatus) {
            // ignore this decision maker
            return Decision.OK;
        }
        if (MathUtils.compareTo(workloadTaskData.getSuccessRate().doubleValue(), successRateThreshold, EPSILON) >= 0) {
            return Decision.OK;
        }
        return Decision.FATAL;
    }

    @Override
    public Decision decideOnSession(List<WorkloadTaskData> workloadTaskData) {
        Decision worstResult = Decision.OK;
        for (WorkloadTaskData scenario : workloadTaskData) {
            Decision decision = decideOnTest(scenario);
            if (decision.ordinal() > worstResult.ordinal()) {
                worstResult = decision;
            }
        }

        return worstResult;
    }

    public String getDescription() {
        return description;
    }

    @Required
    public void setDescription(String description) {
        this.description = description;
    }

    public double getSuccessRateThreshold() {
        return successRateThreshold;
    }

    @Required
    public void setSuccessRateThreshold(double successRateThreshold) {
        log.info("setting successRateThreshold= {}", successRateThreshold);
        this.successRateThreshold = successRateThreshold;
    }

    @Required
    public void setDoNotUseDeprecatedMethodToSetTestStatus(boolean doNotUseDeprecatedMethodToSetTestStatus) {
        this.doNotUseDeprecatedMethodToSetTestStatus = doNotUseDeprecatedMethodToSetTestStatus;
    }

}

