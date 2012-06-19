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

package com.griddynamics.jagger.engine.e1.reporting;


import com.google.common.collect.Lists;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import com.griddynamics.jagger.reporting.chart.ChartHelper;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class WorkloadScalabilityPlotsReporter extends AbstractReportProvider {
    public static class ScenarioPlotDTO {
        private String scenarioName;
        private JCommonDrawableRenderer throughputPlot;
        private JCommonDrawableRenderer latencyPlot;

        public String getScenarioName() {
            return scenarioName;
        }

        public void setScenarioName(String scenarioName) {
            this.scenarioName = scenarioName;
        }

        public JCommonDrawableRenderer getThroughputPlot() {
            return throughputPlot;
        }

        public void setThroughputPlot(JCommonDrawableRenderer throughputPlot) {
            this.throughputPlot = throughputPlot;
        }

        public JCommonDrawableRenderer getLatencyPlot() {
            return latencyPlot;
        }

        public void setLatencyPlot(JCommonDrawableRenderer latencyPlot) {
            this.latencyPlot = latencyPlot;
        }
    }

    @Override
    public JRDataSource getDataSource() {

        List<ScenarioPlotDTO> plots = Lists.newArrayList();

        String sessionId = getSessionIdProvider().getSessionId();
        @SuppressWarnings("unchecked")
        List<WorkloadData> scenarios = getHibernateTemplate().find("from WorkloadData d where d.sessionId=? order by d.number asc, d.scenario.name asc", sessionId);

        plots.addAll(getScenarioPlots(scenarios));

        return new JRBeanCollectionDataSource(plots);
    }

    private Collection<ScenarioPlotDTO> getScenarioPlots(List<WorkloadData> scenarios) {
        HashMap<String, ScenarioPlotDTO> throughputPlots = new HashMap<String, ScenarioPlotDTO>();
        for (WorkloadData scenario : scenarios) {
            String scenarioName = scenario.getScenario().getName();

            if (!throughputPlots.containsKey(scenarioName)) {
                XYDataset latencyData = getLatencyData(scenarioName);
                XYDataset throughputData = getThroughputData(scenarioName);

                JFreeChart chartThroughput = ChartHelper.createXYChart(null, throughputData, "Thread Count",
                        "Throughput (TPS)", 6, 3, ChartHelper.ColorTheme.LIGHT);

                JFreeChart chartLatency = ChartHelper.createXYChart(null, latencyData, "Thread Count",
                        "Latency (sec)", 6, 3, ChartHelper.ColorTheme.LIGHT);

                ScenarioPlotDTO plotDTO = new ScenarioPlotDTO();
                plotDTO.setScenarioName(scenarioName);
                plotDTO.setThroughputPlot(new JCommonDrawableRenderer(chartThroughput));
                plotDTO.setLatencyPlot(new JCommonDrawableRenderer(chartLatency));

                throughputPlots.put(scenarioName, plotDTO);
            }
        }
        return throughputPlots.values();
    }

    private XYDataset getThroughputData(String scenarioName) {
        List<WorkloadTaskData> all = getResultData(scenarioName);

        XYSeries throughput = new XYSeries("Througput");
        throughput.add(0, 0);
        for (WorkloadTaskData workloadTaskData : all) {
            throughput.add(workloadTaskData.getClockValue(), workloadTaskData.getThroughput());
        }
        return new XYSeriesCollection(throughput);
    }

    private XYDataset getLatencyData(String scenarioName) {
        List<WorkloadTaskData> all = getResultData(scenarioName);

        XYSeries meanLatency = new XYSeries("Mean");
        XYSeries stdDevLatency = new XYSeries("StdDev");
        meanLatency.add(0, 0);
        stdDevLatency.add(0, 0);
        for (WorkloadTaskData workloadTaskData : all) {
            meanLatency.add(workloadTaskData.getClockValue(), workloadTaskData.getAvgLatency());
            stdDevLatency.add(workloadTaskData.getClockValue(), workloadTaskData.getStdDevLatency());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(meanLatency);
        dataset.addSeries(stdDevLatency);
        return dataset;
    }

    private List<WorkloadTaskData> getResultData(String scenarioName) {
        String sessionId = getSessionIdProvider().getSessionId();
        @SuppressWarnings("unchecked")
        List<WorkloadTaskData> all = getHibernateTemplate().find("from WorkloadTaskData d where d.scenario.name=? and d.sessionId=?", scenarioName, sessionId);
        return all;
    }
}
