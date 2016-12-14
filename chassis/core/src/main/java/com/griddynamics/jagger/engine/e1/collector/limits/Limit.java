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

/** Class is used to describe individual limits for some metric. Limits are used for decision making
 *
 * @details
 * Metric comparison will be provided by @ref BasicTGDecisionMakerListener decision maker or @n
 * by custom implementation of @ref com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener "TestGroupDecisionMakerListener" @n
 * Metric value will be compared with some reference: ref, where ref is: @n
 * @li value from baseline when refValue = null @n
 * @li refValue in all other cases @n
 *
 * Result OK when value in range (LWT*ref .. UWT*ref) @n
 * Result WARNING when value in range (LET*ref .. LWT*ref) OR (UWT*ref .. UET*ref) @n
 * Result FATAL when value is less than LET*ref OR is greater than UET*ref @n
 */
public class Limit {
    private String metricName = null;
    private String limitDescription;
    private Double refValue = null;
    private Double lowerWarningThreshold = 0D;
    private Double upperWarningThreshold = 0D;
    private Double lowerErrorThreshold = 0D;
    private Double upperErrorThreshold = 0D;

    public Limit() {}

    /** Returns metric name (aka metric Id) - metric we are going to compare */
    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /** Returns description of this limit */
    public String getLimitDescription() {
        return limitDescription;
    }

    public void setLimitDescription(String limitDescription) {
        this.limitDescription = limitDescription;
    }

    /** Returns reference value - absolute value used as reference for comparison. When refValue=null we will compare to baseline session value */
    public Double getRefValue() {
        return refValue;
    }

    public void setRefValue(Double refValue) {
        this.refValue = refValue;
    }

    /** Returns lower warning threshold - LWT. Relative value */
    public Double getLowerWarningThreshold() {
        return lowerWarningThreshold;
    }

    public void setLowerWarningThreshold(Double lowerWarningThreshold) {
        this.lowerWarningThreshold = lowerWarningThreshold;
    }

    /** Returns upper warning threshold - UWT. Relative value */
    public Double getUpperWarningThreshold() {
        return upperWarningThreshold;
    }

    public void setUpperWarningThreshold(Double upperWarningThreshold) {
        this.upperWarningThreshold = upperWarningThreshold;
    }

    /** Returns lower error threshold - LET. Relative value */
    public Double getLowerErrorThreshold() {
        return lowerErrorThreshold;
    }

    public void setLowerErrorThreshold(Double lowerErrorThreshold) {
        this.lowerErrorThreshold = lowerErrorThreshold;
    }

    /** Returns upper error threshold - UET. Relative value */
    public Double getUpperErrorThreshold() {
        return upperErrorThreshold;
    }

    public void setUpperErrorThreshold(Double upperErrorThreshold) {
        this.upperErrorThreshold = upperErrorThreshold;
    }

    @Override
    public String toString() {
        return "Limit{" +
                "metricName='" + metricName + '\'' +
                ", limitDescription='" + limitDescription + '\'' +
                ", refValue=" + refValue +
                ", lowerWarningThreshold=" + lowerWarningThreshold +
                ", upperWarningThreshold=" + upperWarningThreshold +
                ", lowerErrorThreshold=" + lowerErrorThreshold +
                ", upperErrorThreshold=" + upperErrorThreshold +
                '}';
    }
}

