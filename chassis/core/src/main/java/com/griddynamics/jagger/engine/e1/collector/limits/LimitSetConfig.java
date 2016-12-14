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

import com.griddynamics.jagger.util.Decision;
import org.springframework.beans.factory.annotation.Required;

/** Class is used to describe setup for @ref LimitSet */
public class LimitSetConfig {
    /** What decision should be taken when limit is specified, but no metric in the test matches metricName of this Limit */
    private Decision decisionWhenNoMetricForLimit;

    /** What decision should be taken when baseline value can't be fetched for some metric (f.e. test or metric doesn't exist in baseline session) */
    private Decision decisionWhenNoBaselineForMetric;

    /** What decision should be taken when several limits match same metric (f.e. 'mon_cpu' & 'mon_cpu_user' will match 'mon_cpu_user|agent_007 [127.0.1.1]|-avg') */
    private Decision decisionWhenSeveralLimitsMatchSingleMetric;

    public Decision getDecisionWhenNoMetricForLimit() {
        return decisionWhenNoMetricForLimit;
    }

    @Required
    public void setDecisionWhenNoMetricForLimit(Decision decisionWhenNoMetricForLimit) {
        this.decisionWhenNoMetricForLimit = decisionWhenNoMetricForLimit;
    }

    public Decision getDecisionWhenNoBaselineForMetric() {
        return decisionWhenNoBaselineForMetric;
    }

    @Required
    public void setDecisionWhenNoBaselineForMetric(Decision decisionWhenNoBaselineForMetric) {
        this.decisionWhenNoBaselineForMetric = decisionWhenNoBaselineForMetric;
    }

    public Decision getDecisionWhenSeveralLimitsMatchSingleMetric() {
        return decisionWhenSeveralLimitsMatchSingleMetric;
    }

    @Required
    public void setDecisionWhenSeveralLimitsMatchSingleMetric(Decision decisionWhenSeveralLimitsMatchSingleMetric) {
        this.decisionWhenSeveralLimitsMatchSingleMetric = decisionWhenSeveralLimitsMatchSingleMetric;
    }

}

