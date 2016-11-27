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

package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import com.griddynamics.jagger.reporting.chart.ChartHelper;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkloadScalabilityPlotsReporter extends AbstractReportProvider {
    
    public static final Comparator<ScenarioPlotDTO> BY_NAME = Comparator.comparing(ScenarioPlotDTO::getScenarioName);
    
    private HashMap<String, String> clockDictionary;

    public HashMap<String, String> getClockDictionary() {
        return clockDictionary;
    }

    public void setClockDictionary(HashMap<String, String> clockDictionary) {
        this.clockDictionary = clockDictionary;
    }

    @Override
    public JRDataSource getDataSource(String sessionId) {

        List<ScenarioPlotDTO> plots = Lists.newArrayList();
    
        Map<TestEntity, Map<String, Double>> dataForScalabilityPlots =
                getContext().getSummaryReporter().getDataForScalabilityPlots(sessionId);
        plots.addAll(getScenarioPlots(dataForScalabilityPlots));
    
        plots.sort(BY_NAME);
        return new JRBeanCollectionDataSource(plots);
    }

    private Collection<ScenarioPlotDTO> getScenarioPlots(Map<TestEntity,Map<String,Double>> dataForScalabilityPlots) {

        HashMap<String, ScenarioPlotDTO> throughputPlots = new HashMap<String, ScenarioPlotDTO>();
        for (TestEntity testEntity : dataForScalabilityPlots.keySet()) {
            String scenarioName = testEntity.getName();

            if (!throughputPlots.containsKey(scenarioName)) {

                // Back compatibility: collect all tests with equal name
                Map<TestEntity,Map<String,Double>> resultData = new HashMap<TestEntity, Map<String, Double>>();
                for (Map.Entry<TestEntity,Map<String,Double>> mapEntry : dataForScalabilityPlots.entrySet()) {
                    if (mapEntry.getKey().getName().equals(scenarioName)) {
                        resultData.put(testEntity, mapEntry.getValue());
                    }
                }

                XYDataset latencyData = getLatencyData(resultData);
                XYDataset throughputData = getThroughputData(resultData);
                String clockForPlot = getClockForPlot(resultData);

                JFreeChart chartThroughput = ChartHelper.createXYChart(null, throughputData, clockForPlot,
                        "Throughput (TPS)", 6, 3, ChartHelper.ColorTheme.LIGHT);

                JFreeChart chartLatency = ChartHelper.createXYChart(null, latencyData, clockForPlot,
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

    private String getClockForPlot(Map<TestEntity,Map<String,Double>> resultData) {
        String clock = "-";
        if (!resultData.isEmpty()) {
            clock = resultData.entrySet().iterator().next().getKey().getLoad();
            if (clockDictionary != null) {
                for (String key : clockDictionary.keySet()) {
                    if (clock.contains(key)) {
                        return clockDictionary.get(key);
                    }
                }
            }
        }
        return clock;
    }

    private XYDataset getThroughputData(Map<TestEntity,Map<String,Double>> resultData) {

        XYSeries throughput = new XYSeries("Throughput");
        throughput.add(0, 0);
        for (Map.Entry<TestEntity,Map<String,Double>> mapEntry : resultData.entrySet()) {
            throughput.add(mapEntry.getKey().getClockValue().doubleValue(), mapEntry.getValue().get(StandardMetricsNamesUtil.THROUGHPUT_ID).doubleValue());
        }
        return new XYSeriesCollection(throughput);
    }

    private XYDataset getLatencyData(Map<TestEntity,Map<String,Double>> resultData) {

        XYSeries meanLatency = new XYSeries("Mean");
        XYSeries stdDevLatency = new XYSeries("StdDev");
        meanLatency.add(0, 0);
        stdDevLatency.add(0, 0);
        for (Map.Entry<TestEntity,Map<String,Double>> mapEntry : resultData.entrySet()) {
            meanLatency.add(mapEntry.getKey().getClockValue().doubleValue(), mapEntry.getValue().get(StandardMetricsNamesUtil.LATENCY_ID).doubleValue());
            stdDevLatency.add(mapEntry.getKey().getClockValue().doubleValue(), mapEntry.getValue().get(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID).doubleValue());
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(meanLatency);
        dataset.addSeries(stdDevLatency);
        return dataset;
    }

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
}
