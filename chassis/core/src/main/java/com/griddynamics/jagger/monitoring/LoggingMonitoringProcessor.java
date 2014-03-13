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

import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.agent.model.SystemInfo;
import com.griddynamics.jagger.agent.model.SystemUnderTestInfo;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.engine.e1.services.DefaultMetricService;
import com.griddynamics.jagger.engine.e1.services.MetricService;
import com.griddynamics.jagger.util.AgentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link MonitoringProcessor}
 */
public class LoggingMonitoringProcessor implements MonitoringProcessor {
    private Logger log = LoggerFactory.getLogger(LoggingMonitoringProcessor.class);

    public static final String MONITORING_MARKER = "MONITORING";

    private Map<String, MetricService> metricServiceMap = new HashMap<String, MetricService>();
    private Map<String, Map<String, Boolean>> createdMetrics = new HashMap<String, Map<String, Boolean>>();

    @Override
    public void process(String sessionId, String taskId, NodeId agentId, NodeContext nodeContext, SystemInfo systemInfo) {
        String serviceId = getKey(sessionId, taskId);
        if (metricServiceMap.get(serviceId) == null){
            metricServiceMap.put(serviceId, new DefaultMetricService(sessionId, taskId, nodeContext));
            createdMetrics.put(serviceId, new HashMap<String, Boolean>());
        }

        // save sigar metrics
        saveMonitoringValues(serviceId, agentId.getIdentifier(), systemInfo.getSysInfo());

        // save jmx metrics
        for (Map.Entry<String, SystemUnderTestInfo> entry : systemInfo.getSysUnderTest().entrySet()){
            saveMonitoringValues(serviceId, entry.getKey(), entry.getValue().getSysUTInfo());
        }

        log.trace("System info {} received from agent {} and has been written to FileStorage", systemInfo, agentId);
    }

    private void saveMonitoringValues(String serviceId, String agentId, Map<MonitoringParameter, Double> values){
        MetricService service = metricServiceMap.get(serviceId);

        for (Map.Entry<MonitoringParameter, Double> entry : values.entrySet()){
            MonitoringParameter monitoringParameter = entry.getKey();
            Double value = entry.getValue();

            // save value
            String metricId = getMetricId(serviceId, monitoringParameter, agentId);
            service.saveValue(metricId, value);
        }
    }

    // return metric id for current monitoring parameter
    private String getMetricId(String serviceId, MonitoringParameter monitoringParameter, String agentId){
        String metricId = AgentUtils.getMonitoringMetricId(monitoringParameter.getDescription(), agentId);

        // register metric aggregators
        if (createdMetrics.get(serviceId).get(metricId) == null){
            MetricAggregatorProvider aggregator;
            if (!monitoringParameter.isCumulativeCounter()){
                aggregator = new AvgMetricAggregatorProvider();
            }else{
                // todo : set correct aggregator
                aggregator = new SumMetricAggregatorProvider();
            }

            metricServiceMap.get(serviceId).createMetric(new MetricDescription(metricId)
                                                                .displayName(monitoringParameter.getDescription())
                                                                .showSummary(false)
                                                                .plotData(true).addAggregator(aggregator));

            createdMetrics.get(serviceId).put(metricId, true);
        }

        return metricId;
    }

    private String getKey(String sessionId, String taskId){
        return sessionId+taskId;
    }
}
