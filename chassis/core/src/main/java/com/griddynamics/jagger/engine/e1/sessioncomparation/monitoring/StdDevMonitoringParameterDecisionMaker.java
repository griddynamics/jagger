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

package com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring;

import com.griddynamics.jagger.engine.e1.sessioncomparation.ComparisonUtil;
import com.griddynamics.jagger.util.Decision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;

@Deprecated
public class StdDevMonitoringParameterDecisionMaker implements MonitoringParameterDecisionMaker {
    private static final Logger log = LoggerFactory.getLogger(StdDevMonitoringParameterDecisionMaker.class);

    private double warningDeviationThreshold;
    private double fatalDeviationThreshold;


    @Override
    @Deprecated
    public Decision makeDecision(String name, MonitoringParameterComparison comparison) {

        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(comparison);

        MonitoringStats first = comparison.getFirst();
        MonitoringStats second = comparison.getSecond();

        log.debug("Going to make decision based on\n name = '{}', first = '{}', second = '{}'", new Object[]{name, first, second});

        double firstStdDev = first.getSttDev();
        double secondStdDev = second.getSttDev();

        log.debug("first std dev {}", first);
        log.debug("second std dev {}", second);

        double result = ComparisonUtil.calculateDeviation(firstStdDev, secondStdDev);
        log.debug("Deviation {}", result);

        if (Math.abs(result) > fatalDeviationThreshold) {
            return Decision.FATAL;
        } else if (Math.abs(result) > warningDeviationThreshold) {
            return Decision.WARNING;
        }

        return Decision.OK;
    }

    @Required
    public void setWarningDeviationThreshold(double warningDeviationThreshold) {
        this.warningDeviationThreshold = warningDeviationThreshold;
    }

    @Required
    public void setFatalDeviationThreshold(double fatalDeviationThreshold) {
        this.fatalDeviationThreshold = fatalDeviationThreshold;
    }

    public double getWarningDeviationThreshold() {
        return warningDeviationThreshold;
    }

    public double getFatalDeviationThreshold() {
        return fatalDeviationThreshold;
    }
}
