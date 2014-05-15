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

/** Class is used to describe individual limits for some metric. Limits are used for decision making
 *
 * @details
 * Metric comparison will be provided by @ref BasicTGDecisionMakerListener decision maker or @n
 * by custom implementation of @ref TestGroupDecisionMakerListener @n
 * Metric value will be compared with some reference: ref, where ref is: @n
 * @li value from baseline when refValue = null @n
 * @li refValue in all other cases @n
 *
 * Result pass when value in range (LWT*ref .. UWT*ref) @n
 * Result warning when value in range (LET*ref .. LWT*ref) OR (UWT*ref .. UET*ref) @n
 * Result error when value is less than LET*ref OR is greater than UET*ref @n
 */
public class Limit {
    /** Metric name (aka metric Id) - metric we are going to compare */
    private String metricName = null;

    /** Description of this limit */
    private String limitDescription;

    /** Reference value - absolute value used as reference for comparison. When refValue=null we will compare to baseline session value */
    private Double refValue = null;

    /** Lower warning threshold - LWT. Relative value */
    private Double lowerWarningThreshold = 0D;

    /** Upper warning threshold - UWT. Relative value */
    private Double upperWarningThreshold = 0D;

    /** Lower error threshold - LET. Relative value */
    private Double lowerErrorThreshold = 0D;

    /** Upper error threshold - UET. Relative value */
    private Double upperErrorThreshold = 0D;

    public Limit() {}

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getLimitDescription() {
        return limitDescription;
    }

    public void setLimitDescription(String limitDescription) {
        this.limitDescription = limitDescription;
    }

    public Double getRefValue() {
        return refValue;
    }

    public void setRefValue(Double refValue) {
        this.refValue = refValue;
    }

    public Double getLowerWarningThreshold() {
        return lowerWarningThreshold;
    }

    public void setLowerWarningThreshold(Double lowerWarningThreshold) {
        this.lowerWarningThreshold = lowerWarningThreshold;
    }

    public Double getUpperWarningThreshold() {
        return upperWarningThreshold;
    }

    public void setUpperWarningThreshold(Double upperWarningThreshold) {
        this.upperWarningThreshold = upperWarningThreshold;
    }

    public Double getLowerErrorThreshold() {
        return lowerErrorThreshold;
    }

    public void setLowerErrorThreshold(Double lowerErrorThreshold) {
        this.lowerErrorThreshold = lowerErrorThreshold;
    }

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

