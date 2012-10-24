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
package com.griddynamics.jagger.agent.model.coherence;

import java.util.List;

/**
 * @author Vladimir Kondrashchenko
 */
public class CoherenceCacheMonitoringParameter extends AbstractCoherenceMonitoringParameter {

    private String domainName;

    private List<String> cacheNames;

    private CoherenceCacheMetricSource metricSource;

    private boolean isAverageValue = false;

    public CoherenceCacheMetricSource getMetricSource() {
        return metricSource;
    }

    public void setMetricSource(CoherenceCacheMetricSource metricSource) {
        this.metricSource = metricSource;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setCacheNames(List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public boolean isAverageValue() {
        return isAverageValue;
    }

    public void setAverageValue(boolean averageValue) {
        isAverageValue = averageValue;
    }

    @Override
    public String getDescription() {
        return new StringBuilder("node").append(coherenceNode.getId()).append(":").append(domainName).append(":").
                append(metricAttributeName).toString();
    }

    @Override
    public String getReportingGroupKey() {
        return domainName + "::" + metricAttributeName;
    }

    public String getDomainName() {
        return domainName;
    }

    public List<String> getCacheNames() {
        return cacheNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoherenceCacheMonitoringParameter that = (CoherenceCacheMonitoringParameter) o;

        if (!coherenceNode.equals(that.coherenceNode)) return false;
        if (!domainName.equals(that.domainName)) return false;
        if (!metricAttributeName.equals(that.metricAttributeName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = domainName.hashCode();
        result = 31 * result + metricAttributeName.hashCode();
        result = 31 * result + coherenceNode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CoherenceCacheMonitoringParameter{" +
                "cacheNames=" + cacheNames +
                ", domainName='" + domainName + '\'' +
                ", isAverageValue=" + isAverageValue +
                ", metricSource=" + metricSource +
                ", metricAttributeName='" + metricAttributeName + '\'' +
                ", coherenceNode=" + coherenceNode +
                ", isCumulative=" + isCumulative +
                '}';
    }
}
