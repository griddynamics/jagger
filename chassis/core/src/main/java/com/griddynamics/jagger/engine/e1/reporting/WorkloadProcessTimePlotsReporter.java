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

import com.google.common.collect.Maps;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.Percentile;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.TimeInvocationStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.TimeLatencyPercentile;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import com.griddynamics.jagger.reporting.chart.ChartHelper;
import com.griddynamics.jagger.util.Pair;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;

public class WorkloadProcessTimePlotsReporter extends AbstractMappedReportProvider<String> {

    // testId -> plots
    private Map<String, TaskPlotDTO> taskPlots;

    public static class TaskPlotDTO {
        private JCommonDrawableRenderer throughputPlot;
        private JCommonDrawableRenderer latencyPlot;

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
    public JRDataSource getDataSource(String testId) {
        if (taskPlots == null) {
            taskPlots = createTaskPlots();
        }

        return new JRBeanCollectionDataSource(Collections.singleton(taskPlots.get(testId)));
    }

    public Map<String, TaskPlotDTO> createTaskPlots() {
        String sessionId = getSessionIdProvider().getSessionId();

        @SuppressWarnings("unchecked")
        List<TimeInvocationStatistics> statistics = getHibernateTemplate().find(
                "select t from TimeInvocationStatistics t where t.taskData.sessionId=?", sessionId);
        Map<String, List<TimeInvocationStatistics>> aggregatedByTasks = Maps.newLinkedHashMap();

        for (TimeInvocationStatistics stat : statistics) {
            List<TimeInvocationStatistics> taskData = aggregatedByTasks.get(stat.getTaskData().getTaskId());
            if (taskData == null) {
                taskData = new ArrayList<TimeInvocationStatistics>();
                aggregatedByTasks.put(stat.getTaskData().getTaskId(), taskData);
            }
            taskData.add(stat);
        }

        Map<String, TaskPlotDTO> taskPlots = Maps.newHashMap();
        for (String taskId : aggregatedByTasks.keySet()) {
            TaskPlotDTO taskPlot = new TaskPlotDTO();
            List<TimeInvocationStatistics> taskStats = aggregatedByTasks.get(taskId);

            XYSeries throughput = new XYSeries("Throughput");
            XYSeries latency = new XYSeries("Latency avg", true, false);
            XYSeries latencyStdDev = new XYSeries("Latency StdDev", true, false);

            SortedMap<Integer, XYSeries> percentileSeries = new TreeMap<Integer, XYSeries>();

            String taskName = null;
            for (TimeInvocationStatistics stat : taskStats) {
                if (taskName == null) {
                    taskName = stat.getTaskData().getTaskName();
                }

                throughput.add(stat.getTime(), stat.getThroughput());
                latency.add(stat.getTime(), stat.getLatency());
                latencyStdDev.add(stat.getTime(), stat.getLatencyStdDev());

                List<TimeLatencyPercentile> percentiles = stat.getPercentiles();
                if (percentiles != null && !percentiles.isEmpty()) {
                    Collections.sort(percentiles, new Comparator<Percentile>() {
                        @Override
                        public int compare(Percentile p1, Percentile p2) {
                            return Double.compare(p1.getPercentileKey(), p2.getPercentileKey());
                        }
                    });

                    for (int i = 0; i < percentiles.size(); i++) {
                        Percentile currentPercentile = percentiles.get(i);
                        double previousPercentile = i > 0 ? percentiles.get(i - 1).getPercentileValue() : 0;
                        double additivePercentileValue = (currentPercentile.getPercentileValue() - previousPercentile) / 1000;

                        addPercentile(percentileSeries, currentPercentile.getPercentileKey(), additivePercentileValue, stat.getTime());
                    }
                } else {
                    for (XYSeries series : percentileSeries.values()) {
                        series.add(stat.getTime(), 0);
                    }
                }
            }
            XYSeriesCollection throughputCollection = new XYSeriesCollection();
            throughputCollection.addSeries(throughput);

            Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(throughputCollection, null);

            throughputCollection = pair.getSecond();

            JFreeChart chartThroughput = ChartHelper.createXYChart(null, throughputCollection, "Time (" + pair.getFirst() + ")",
                    "Throughput (TPS)", 2, 2, ChartHelper.ColorTheme.LIGHT);
            taskPlot.setThroughputPlot(new JCommonDrawableRenderer(chartThroughput));

            XYSeriesCollection latencyCollection = new XYSeriesCollection();
            latencyCollection.addSeries(latency);
            latencyCollection.addSeries(latencyStdDev);

            XYSeriesCollection percentilesCollection = new XYSeriesCollection();
            for (XYSeries series : percentileSeries.values()) {
                percentilesCollection.addSeries(series);
            }

            Pair<String, XYSeriesCollection> percentilesPair = ChartHelper.adjustTime(percentilesCollection, null);
            Pair<String, XYSeriesCollection> latencyPair = ChartHelper.adjustTime(latencyCollection, null);

            if (!latencyPair.getFirst().equals(percentilesPair.getFirst())) {
                throw new IllegalStateException("Time dimension for percentiles and latency is not equal");
            }

            JFreeChart chartLatencyPercentiles = ChartHelper.createStackedAreaChart(null, percentilesPair.getSecond(),
                    latencyPair.getSecond(), "Time (" + latencyPair.getFirst() + ")", "Latency (sec)", ChartHelper.ColorTheme.LIGHT);
            taskPlot.setLatencyPlot(new JCommonDrawableRenderer(chartLatencyPercentiles));

            taskPlots.put(taskId, taskPlot);
        }
        return taskPlots;
    }

    private static void addPercentile(Map<Integer, XYSeries> percentileSeries, double percentileKey, double additivePercentileValue, long time) {
        String stringPercentileKey = String.format("%.2f", percentileKey);
        int roundedKey = (int) (percentileKey * 100);

        XYSeries series = percentileSeries.get(roundedKey);
        if (series == null) {
            series = new XYSeries(stringPercentileKey, false, false);
            percentileSeries.put(roundedKey, series);
        }
        series.add(time, additivePercentileValue);
    }
}
