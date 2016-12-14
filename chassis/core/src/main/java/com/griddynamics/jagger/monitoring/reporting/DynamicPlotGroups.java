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

package com.griddynamics.jagger.monitoring.reporting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.agent.model.JmxMetric;
import com.griddynamics.jagger.agent.model.JmxMetricGroup;
import com.griddynamics.jagger.dbapi.parameter.MonitoringParameter;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Musienko
 *         Date: 11.07.13
 */
public class DynamicPlotGroups {
    private Map<GroupKey, MonitoringParameter[]> plotGroups;
    private boolean inited = false;

    private ArrayList<JmxMetricGroup> jmxMetricGroups;
    private ArrayList<JmxMetric> jmxMetrics;

    public Map<GroupKey, MonitoringParameter[]> getPlotGroups() {
        if (!inited) {
            init();
        }
        return plotGroups;
    }

    public void setPlotGroups(Map<GroupKey, MonitoringParameter[]> plotGroups) {
        this.plotGroups = plotGroups;
    }

    public void setJmxMetricGroups(ArrayList<JmxMetricGroup> jmxMetricGroups) {
        this.jmxMetricGroups = jmxMetricGroups;
    }

    public ArrayList<JmxMetric> getJmxMetrics() {
        if (jmxMetrics != null) {
            return jmxMetrics;
        }
        jmxMetrics = Lists.newArrayList();
        if (jmxMetricGroups != null) {
            for (JmxMetricGroup metricGroup: jmxMetricGroups) {
                for (JmxMetric metric: metricGroup.getJmxMetrics()) {
                    jmxMetrics.add(metric);
                }
            }
        }
        return jmxMetrics;
    }

    private void init () {
        if (jmxMetricGroups != null) {
            Map<String, GroupKey> additionalGroups = Maps.newHashMap();
            Map<GroupKey, List<MonitoringParameter>> additionalParameters = Maps.newHashMap();
            for (JmxMetricGroup metricGroup: jmxMetricGroups) {

                GroupKey key = additionalGroups.get(metricGroup.getGroupName());

                if (key == null) {
                    key = new GroupKey(metricGroup.getGroupName(), "");
                    additionalGroups.put(metricGroup.getGroupName(), key);
                }

                List<MonitoringParameter> params = additionalParameters.get(key);
                if (params == null) {
                    params = Lists.newLinkedList();
                    additionalParameters.put(key, params);
                }

                for (JmxMetric metric: metricGroup.getJmxMetrics()) {
                    params.add(metric.getParameter());
                }
            }

            //adding to default monitoring parameters
            for (Map.Entry<GroupKey,List<MonitoringParameter>> paramsEntry: additionalParameters.entrySet()) {
                MonitoringParameter[] parameters = plotGroups.get(paramsEntry.getKey());
                if (parameters != null) {
                    for (MonitoringParameter parameter: parameters) {
                        paramsEntry.getValue().add(parameter);
                    }
                }
                parameters = paramsEntry.getValue().toArray(new MonitoringParameter[paramsEntry.getValue().size()]);
                plotGroups.put(paramsEntry.getKey(), parameters);
            }
        }
        inited = true;
    }
}
