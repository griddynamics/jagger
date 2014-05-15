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

import com.griddynamics.jagger.dbapi.entity.WorkloadTaskData;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;
import org.apache.commons.math.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class DefaultSessionStatusDecisionMaker implements SessionStatusDecisionMaker {
    private static final Logger log = LoggerFactory.getLogger(DefaultSessionStatusDecisionMaker.class);
    private static final double epsilon = 0.0000001;
    public static double successRateThreshold = 1.0;

    private String description;

    @Override
    public Decision decideOnTest(WorkloadTaskData workloadTaskData) {
        if (MathUtils.compareTo(workloadTaskData.getSuccessRate().doubleValue(), successRateThreshold, epsilon) >= 0) {
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

    public static Configurator getConfigurator() {
        return Configurator.configuratorInstance;
    }

    public static class Configurator {
        private static final Configurator configuratorInstance = new Configurator();

        private Configurator() {}

        public void setSuccessRateThreshold(double successRateThreshold) {
            log.info("setting successRateThreshold= {}", successRateThreshold);
            DefaultSessionStatusDecisionMaker.successRateThreshold = successRateThreshold;
        }
    }
}

