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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.master.SessionIdProvider;
import com.griddynamics.jagger.reporting.ReportProvider;
import com.griddynamics.jagger.reporting.ReportingContext;
import com.griddynamics.jagger.reporting.chart.ChartHelper;

public class WorkloadScalabilityPlotsReporter extends HibernateDaoSupport implements ReportProvider {
	private SessionIdProvider sessionIdProvider;
	private ReportingContext context;

	private String template;

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

	public JRDataSource getDataSource() {

		List<ScenarioPlotDTO> plots = Lists.newArrayList();

		String sessionId = sessionIdProvider.getSessionId();
		@SuppressWarnings("unchecked")
		List<String> scenarios = getHibernateTemplate().find(
				"select distinct d.scenario.name from WorkloadData d where d.sessionId=? order by d.number asc, d.scenario.name asc", sessionId);
		
		plots.addAll(getScenarioPlots(scenarios));

		return new JRBeanCollectionDataSource(plots);
	}

	private List<ScenarioPlotDTO> getScenarioPlots(List<String> scenarios) {
		List<ScenarioPlotDTO> throughputPlots = Lists.newArrayList();
		for (String scenarioName : scenarios) {
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

			throughputPlots.add(plotDTO);
		}
		return throughputPlots;
	}

	@Override
	public JasperReport getReport() {
		return context.getReport(template);
	}

	@Override
	public void setContext(ReportingContext context) {
		this.context = context;
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
		String sessionId = sessionIdProvider.getSessionId();
		@SuppressWarnings("unchecked")
		List<WorkloadTaskData> all = getHibernateTemplate().find(
				"from WorkloadTaskData d where d.scenario.name=? and d.sessionId=?", scenarioName, sessionId);
		return all;
	}


	
	public void setSessionIdProvider(SessionIdProvider sessionIdProvider) {
		this.sessionIdProvider = sessionIdProvider;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
