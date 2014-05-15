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

package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;

/** Class is used to describe setup for @ref LimitSet */
public class LimitSetConfig {
    /** What decision should be taken when limit is specified, but no metric in the test matches metricName of this Limit */
    private Decision decisionWhenNoMetricForLimit;

    /** What decision should be taken when baseline value can't be fetched for some metric (f.e. test or metric doesn't exist in baseline session) */
    private Decision decisionWhenNoBaselineForMetric;

    public Decision getDecisionWhenNoMetricForLimit() {
        return decisionWhenNoMetricForLimit;
    }

    public void setDecisionWhenNoMetricForLimit(Decision decisionWhenNoMetricForLimit) {
        this.decisionWhenNoMetricForLimit = decisionWhenNoMetricForLimit;
    }

    public Decision getDecisionWhenNoBaselineForMetric() {
        return decisionWhenNoBaselineForMetric;
    }

    public void setDecisionWhenNoBaselineForMetric(Decision decisionWhenNoBaselineForMetric) {
        this.decisionWhenNoBaselineForMetric = decisionWhenNoBaselineForMetric;
    }
}

