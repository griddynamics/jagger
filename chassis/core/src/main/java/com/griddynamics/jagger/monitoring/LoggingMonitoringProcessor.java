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

package com.griddynamics.jagger.monitoring;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.griddynamics.jagger.dbapi.parameter.MonitoringParameter;
import com.griddynamics.jagger.agent.model.SystemInfo;
import com.griddynamics.jagger.agent.model.SystemUnderTestInfo;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.CumulativeMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.services.DefaultMetricService;
import com.griddynamics.jagger.engine.e1.services.MetricService;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link MonitoringProcessor}
 */
public class LoggingMonitoringProcessor implements MonitoringProcessor {
    private Logger log = LoggerFactory.getLogger(LoggingMonitoringProcessor.class);
    private Map<String,MetricDescription> monitoringMetricsDescription;

    public static final String MONITORING_MARKER = "MONITORING";

    private Map<String, MetricService> metricServiceMap = new HashMap<String, MetricService>();
    private Table<String, String, Boolean> createdMetrics = HashBasedTable.create();

    @Override
    public void process(String sessionId, String taskId, NodeId agentId, NodeContext nodeContext, SystemInfo systemInfo) {
        String serviceId = getKey(sessionId, taskId);
        if (!metricServiceMap.containsKey(serviceId)){
            metricServiceMap.put(serviceId, new DefaultMetricService(sessionId, taskId, nodeContext));
        }

        long timeStamp = systemInfo.getTime();

        // save sigar metrics
        saveMonitoringValues(serviceId, agentId.getIdentifier(), systemInfo.getSysInfo(), timeStamp);

        // save jmx metrics
        if (systemInfo.getSysUnderTest() != null){
            for (Map.Entry<String, SystemUnderTestInfo> entry : systemInfo.getSysUnderTest().entrySet()){
                saveMonitoringValues(serviceId, entry.getKey(), entry.getValue().getSysUTInfo(), timeStamp);
            }
        }

        log.trace("System info {} received from agent {} and has been written to FileStorage", systemInfo, agentId);
    }

    private void saveMonitoringValues(String serviceId, String agentId, Map<MonitoringParameter, Double> values, long time){
        if (values == null){
            return;
        }

        MetricService service = metricServiceMap.get(serviceId);

        for (Map.Entry<MonitoringParameter, Double> entry : values.entrySet()){
            MonitoringParameter monitoringParameter = entry.getKey();
            Double value = entry.getValue();

            // save value
            String metricId = getMetricId(serviceId, monitoringParameter, agentId);
            if (metricId != null) {
                service.saveValue(metricId, value, time);
            }
        }
    }

    // return metric id for current monitoring parameter
    // return null if we are not planing to save this metric
    private String getMetricId(String serviceId, MonitoringParameter monitoringParameter, String agentId){
        String metricId = MonitoringIdUtils.getMonitoringMetricId(monitoringParameter.getId(), agentId);

        // register metric
        if (!createdMetrics.contains(serviceId, metricId)){
            MetricDescription metricDescription;

            if (monitoringMetricsDescription.containsKey(monitoringParameter.getId())) {
                // we will create monitoring metrics with overriding default setup
                metricDescription = monitoringMetricsDescription.get(monitoringParameter.getId());

                // we will not save this metric
                if ((!metricDescription.getShowSummary()) && (!metricDescription.getPlotData())) {
                    return null;
                }

                metricDescription.setMetricId(metricId);
            }
            else {
                // we will create monitoring metrics with default setup
                metricDescription = new MetricDescription(metricId);

                if (!monitoringParameter.isCumulativeCounter()){
                    metricDescription.addAggregator(new AvgMetricAggregatorProvider());
                }else{
                    metricDescription.addAggregator(new CumulativeMetricAggregatorProvider());
                }
                metricDescription.setPlotData(true);
                metricDescription.setShowSummary(false);
                metricDescription.setDisplayName(monitoringParameter.getDescription());
            }

            metricServiceMap.get(serviceId).createMetric(metricDescription);
            createdMetrics.put(serviceId, metricId, true);
        }

        return metricId;
    }

    private String getKey(String sessionId, String taskId){
        return sessionId+taskId;
    }

    @Required
    public void setMonitoringMetricsDescription(Map<String, MetricDescription> monitoringMetricsDescription) {
        this.monitoringMetricsDescription = monitoringMetricsDescription;
    }

}
