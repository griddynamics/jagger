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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.agent.model.MonitoringParametersProvider;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author Vladimir Kondrashchenko
 */
public class CoherenceMonitoringParametersProvider implements MonitoringParametersProvider {

    private List<CoherenceDomainCaches> domainCaches;

    private List<String> cumulativeCacheMetricNames;
    private List<String> nonCumulativeCacheMetricNames;
    private List<String> averageCacheMetricNames;

    private List<String> cumulativeNodeMetricNames;
    private List<String> nonCumulativeNodeMetricNames;

    private List<String> cumulativeServiceMetricNames;
    private List<String> nonCumulativeServiceMetricNames;
    private List<String> averageServiceMetricNames;

    private Collection<CoherenceNode> coherenceNodes;

    @Required
    public void setDomainCaches(List<CoherenceDomainCaches> domainCaches) {
        this.domainCaches = domainCaches;
    }

    public void setCumulativeCacheMetricNames(List<String> cumulativeCacheMetricNames) {
        this.cumulativeCacheMetricNames = cumulativeCacheMetricNames;
    }

    public void setNonCumulativeCacheMetricNames(List<String> nonCumulativeCacheMetricNames) {
        this.nonCumulativeCacheMetricNames = nonCumulativeCacheMetricNames;
    }

    public void setCumulativeNodeMetricNames(List<String> cumulativeNodeMetricNames) {
        this.cumulativeNodeMetricNames = cumulativeNodeMetricNames;
    }

    public void setNonCumulativeNodeMetricNames(List<String> nonCumulativeNodeMetricNames) {
        this.nonCumulativeNodeMetricNames = nonCumulativeNodeMetricNames;
    }

    public void setCumulativeServiceMetricNames(List<String> cumulativeServiceMetricNames) {
        this.cumulativeServiceMetricNames = cumulativeServiceMetricNames;
    }

    public void setNonCumulativeServiceMetricNames(List<String> nonCumulativeServiceMetricNames) {
        this.nonCumulativeServiceMetricNames = nonCumulativeServiceMetricNames;
    }

    public void setAverageCacheMetricNames(List<String> averageCacheMetricNames) {
        this.averageCacheMetricNames = averageCacheMetricNames;
    }

    public void setAverageServiceMetricNames(List<String> averageServiceMetricNames) {
        this.averageServiceMetricNames = averageServiceMetricNames;
    }

    public void setCoherenceNodes(Collection<CoherenceNode> coherenceNodes) {
        this.coherenceNodes = coherenceNodes;
    }

    public Iterable<MonitoringParameter> generateMonitoringParameters() {
        if (CollectionUtils.isEmpty(coherenceNodes)) {
            throw new IllegalStateException("coherenceNodes collection is not initialized.");
        }
        List<MonitoringParameter> monitoringParameters = Lists.newArrayListWithCapacity(domainCaches.size() * cumulativeCacheMetricNames.size() * coherenceNodes.size());
        for (CoherenceNode coherenceNode : coherenceNodes) {
            for (CoherenceDomainCaches domain : domainCaches) {
                populateCacheMonitoringParameters(monitoringParameters, coherenceNode, domain);
                populateServiceMonitoringParameters(monitoringParameters, coherenceNode, domain);
            }
            populateNodeMonitoringParameters(monitoringParameters, coherenceNode);
        }

        return monitoringParameters;
    }

    private void populateNodeMonitoringParameters(List<MonitoringParameter> monitoringParameters, CoherenceNode coherenceNode) {
        if (cumulativeNodeMetricNames != null) {
            for (String metricName : cumulativeNodeMetricNames) {
                monitoringParameters.add(createNodeMonitoringParameter(metricName, coherenceNode, true));
            }
        }
        if (nonCumulativeNodeMetricNames != null) {
            for (String metricName : nonCumulativeNodeMetricNames) {
                monitoringParameters.add(createNodeMonitoringParameter(metricName, coherenceNode, false));
            }
        }
    }

    private void populateServiceMonitoringParameters(List<MonitoringParameter> monitoringParameters, CoherenceNode coherenceNode, CoherenceDomainCaches domain) {
        if (cumulativeServiceMetricNames != null) {
            for (String metricName : cumulativeServiceMetricNames) {
                monitoringParameters.add(createCacheMonitoringParameter(domain.getDomainName(), domain.getCacheNames(),
                        metricName, coherenceNode, true, false, CoherenceCacheMetricSource.SERVICE));
            }
        }
        if (nonCumulativeServiceMetricNames != null) {
            for (String metricName : nonCumulativeServiceMetricNames) {
                monitoringParameters.add(createCacheMonitoringParameter(domain.getDomainName(), domain.getCacheNames(),
                        metricName, coherenceNode, false, false, CoherenceCacheMetricSource.SERVICE));
            }
        }
        if (averageServiceMetricNames != null) {
            for (String metricName : averageServiceMetricNames) {
                monitoringParameters.add(createCacheMonitoringParameter(domain.getDomainName(), domain.getCacheNames(),
                        metricName, coherenceNode, false, true, CoherenceCacheMetricSource.SERVICE));
            }
        }
    }

    private void populateCacheMonitoringParameters(List<MonitoringParameter> monitoringParameters, CoherenceNode coherenceNode, CoherenceDomainCaches domain) {
        if (cumulativeCacheMetricNames != null) {
            for (String metricName : cumulativeCacheMetricNames) {
                monitoringParameters.add(createCacheMonitoringParameter(domain.getDomainName(), domain.getCacheNames(),
                        metricName, coherenceNode, true, false, CoherenceCacheMetricSource.CACHE));
            }
        }
        if (nonCumulativeCacheMetricNames != null) {
            for (String metricName : nonCumulativeCacheMetricNames) {
                monitoringParameters.add(createCacheMonitoringParameter(domain.getDomainName(), domain.getCacheNames(),
                        metricName, coherenceNode, false, false, CoherenceCacheMetricSource.CACHE));
            }
        }
        if (averageCacheMetricNames != null) {
            for (String metricName : averageCacheMetricNames) {
                monitoringParameters.add(createCacheMonitoringParameter(domain.getDomainName(), domain.getCacheNames(),
                        metricName, coherenceNode, false, true, CoherenceCacheMetricSource.CACHE));
            }
        }
    }

    private MonitoringParameter createCacheMonitoringParameter(String domainName, List<String> cacheNames,
                                                               String metricAttributeName, CoherenceNode coherenceNode,
                                                               boolean isCumulative, boolean isAverage,
                                                               CoherenceCacheMetricSource metricSource) {
        CoherenceCacheMonitoringParameter monitoringParameter = new CoherenceCacheMonitoringParameter();
        monitoringParameter.setCacheNames(cacheNames);
        monitoringParameter.setDomainName(domainName);
        monitoringParameter.setMetricAttributeName(metricAttributeName);
        monitoringParameter.setCoherenceNode(coherenceNode);
        monitoringParameter.setCumulative(isCumulative);
        monitoringParameter.setMetricSource(metricSource);
        monitoringParameter.setAverageValue(isAverage);
        return monitoringParameter;
    }

    private MonitoringParameter createNodeMonitoringParameter(String metricAttributeName, CoherenceNode coherenceNode,
                                                               boolean isCumulative) {
        CoherenceNodeMonitoringParameter monitoringParameter = new CoherenceNodeMonitoringParameter();
        monitoringParameter.setMetricAttributeName(metricAttributeName);
        monitoringParameter.setCoherenceNode(coherenceNode);
        monitoringParameter.setCumulative(isCumulative);
        return monitoringParameter;
    }

}
