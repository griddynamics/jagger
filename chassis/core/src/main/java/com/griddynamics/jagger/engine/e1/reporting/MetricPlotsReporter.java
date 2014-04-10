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
import com.griddynamics.jagger.dbapi.entity.MetricDetails;
import com.griddynamics.jagger.dbapi.entity.MetricPointEntity;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import com.griddynamics.jagger.reporting.chart.ChartHelper;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import com.griddynamics.jagger.util.Pair;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.*;

/**
 * @author Nikolay Musienko
 *         Date: 19.03.13
 */

public class MetricPlotsReporter extends AbstractMappedReportProvider<String> {

    private Map<String, MetricPlotDTOs> plots;
    private String sessionId;

    public static class MetricPlotDTOs {
        private Collection<MetricPlotDTO> metricPlotDTOs;

        public MetricPlotDTOs() {
            metricPlotDTOs = new LinkedList<MetricPlotDTO>();
        }

        public Collection<MetricPlotDTO> getMetricPlotDTOs() {
            return metricPlotDTOs;
        }

        public void setPlot(Collection<MetricPlotDTO> metricPlotDTOs) {
            this.metricPlotDTOs = metricPlotDTOs;
        }

        public void sortingByMetricName() {
            Collections.sort((List<MetricPlotDTO>) metricPlotDTOs, new Comparator<MetricPlotDTO>() {
                @Override
                public int compare(MetricPlotDTO o1, MetricPlotDTO o2) {
                    return o1.getMetricName().compareTo(o2.getMetricName());
                }
            });
        }
    }

    public static class MetricPlotDTO {
        private JCommonDrawableRenderer metricPlot;
        private String metricName;
        private String title;

        public MetricPlotDTO(String metricName, String title, JCommonDrawableRenderer metricPlot) {
            this.metricPlot = metricPlot;
            this.metricName = metricName;
            this.title = title;
        }

        public MetricPlotDTO() {
        }

        public JCommonDrawableRenderer getMetricPlot() {
            return metricPlot;
        }

        public void setMetricPlot(JCommonDrawableRenderer metricPlot) {
            this.metricPlot = metricPlot;
        }

        public String getMetricName() {
            return metricName;
        }

        public void setMetricName(String metricName) {
            this.metricName = metricName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    @Override
    public JRDataSource getDataSource(String testId) {
        sessionId = getSessionIdProvider().getSessionId();
        String testIdParent = getParentId(testId);

        if (plots == null) {
            plots = createTaskPlots();
        }
        if (plots.size() == 0) {
            return null;
        }
        MetricPlotDTOs result = new MetricPlotDTOs();

        if (plots.containsKey(testId)) {
            if (plots.get(testId).getMetricPlotDTOs() != null) {
                plots.get(testId).sortingByMetricName();
                result.getMetricPlotDTOs().addAll(plots.get(testId).getMetricPlotDTOs());
            }
        }
        if (plots.containsKey(testIdParent)) {
            if (plots.get(testIdParent).getMetricPlotDTOs() != null) {
                plots.get(testIdParent).sortingByMetricName();
                result.getMetricPlotDTOs().addAll(plots.get(testIdParent).getMetricPlotDTOs());
            }
        }

        return new JRBeanCollectionDataSource(Collections.singleton(result));
    }

    private String getParentId(String testId) {
        return (String) getHibernateTemplate().find("select distinct w.parentId from WorkloadData w where w.taskId=? and w.sessionId=?", testId, sessionId).get(0);
    }

    private Map<String, MetricPlotDTOs> createTaskPlots() {
        // check new model
        List<MetricPointEntity> metricDetails = getHibernateTemplate().find(
                "select m from MetricPointEntity m where m.metricDescription.taskData.sessionId=?", sessionId);

        if (metricDetails == null || metricDetails.isEmpty()) {
            return oldWay();
        } else {
            return newWay(metricDetails);
        }
    }

    // create TaskPlots
    private Map<String, MetricPlotDTOs> newWay(List<MetricPointEntity> metricPoints) {
        Map<String, Map<String, List<MetricPointEntity>>> aggregatedByTasks = Maps.newLinkedHashMap();
        for (MetricPointEntity detail : metricPoints) {

            String taskId = detail.getMetricDescription().getTaskData().getTaskId();
            String metricId = detail.getMetricDescription().getMetricId();

            Map<String, List<MetricPointEntity>> byTaskId = aggregatedByTasks.get(taskId);
            if (byTaskId == null) {
                byTaskId = Maps.newLinkedHashMap();
                aggregatedByTasks.put(taskId, byTaskId);
            }
            List<MetricPointEntity> taskData = byTaskId.get(metricId);
            if (taskData == null) {
                taskData = new LinkedList<MetricPointEntity>();
                byTaskId.put(metricId, taskData);
            }
            taskData.add(detail);
        }

        Map<String, MetricPlotDTOs> taskPlots = Maps.newHashMap();
        for (String taskId : aggregatedByTasks.keySet()) {
            MetricPlotDTOs taskPlot = taskPlots.get(taskId);
            if (taskPlot == null) {
                taskPlot = new MetricPlotDTOs();
                taskPlots.put(taskId, taskPlot);
            }
            for (String metricName : aggregatedByTasks.get(taskId).keySet()) {
                List<MetricPointEntity> taskStats = aggregatedByTasks.get(taskId).get(metricName);
                String displayName = taskStats.get(0).getDisplay();
                String title = displayName;

                MonitoringIdUtils.MonitoringId monitoringId = MonitoringIdUtils.splitMonitoringMetricId(taskStats.get(0).getMetricDescription().getMetricId());
                if (monitoringId != null) {
                    title += " on " +  monitoringId.getAgentName();
                }

                XYSeries plotEntry = new XYSeries(displayName);
                for (MetricPointEntity stat : taskStats) {
                    plotEntry.add(stat.getTime(), stat.getValue());
                }
                XYSeriesCollection plotCollection = new XYSeriesCollection();
                plotCollection.addSeries(plotEntry);
                Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(plotCollection, null);
                plotCollection = pair.getSecond();
                JFreeChart chartMetric = ChartHelper.createXYChart(null, plotCollection,
                        "Time (" + pair.getFirst() + ")", displayName, 2, 2, ChartHelper.ColorTheme.LIGHT);
                taskPlot.getMetricPlotDTOs().add(new MetricPlotDTO(displayName, title, new JCommonDrawableRenderer(chartMetric)));
            }
        }
        return taskPlots;
    }

    // create TaskPlots as before 1.2.4
    private Map<String, MetricPlotDTOs> oldWay() {
        List<MetricDetails> metricDetails = getHibernateTemplate().find(
                "select m from MetricDetails m where m.taskData.sessionId=?", sessionId);
        Map<String, Map<String, List<MetricDetails>>> aggregatedByTasks = Maps.newLinkedHashMap();
        for (MetricDetails detail : metricDetails) {
            Map<String, List<MetricDetails>> byTaskId = aggregatedByTasks.get(detail.getTaskData().getTaskId());
            if (byTaskId == null) {
                byTaskId = Maps.newLinkedHashMap();
                aggregatedByTasks.put(detail.getTaskData().getTaskId(), byTaskId);
            }
            List<MetricDetails> taskData = byTaskId.get(detail.getMetric());
            if (taskData == null) {
                taskData = new LinkedList<MetricDetails>();
                byTaskId.put(detail.getMetric(), taskData);
            }
            taskData.add(detail);
        }
        Map<String, MetricPlotDTOs> taskPlots = Maps.newHashMap();
        for (String taskId : aggregatedByTasks.keySet()) {
            MetricPlotDTOs taskPlot = taskPlots.get(taskId);
            if (taskPlot == null) {
                taskPlot = new MetricPlotDTOs();
                taskPlots.put(taskId, taskPlot);
            }
            for (String metricName : aggregatedByTasks.get(taskId).keySet()) {
                List<MetricDetails> taskStats = aggregatedByTasks.get(taskId).get(metricName);
                XYSeries plotEntry = new XYSeries(metricName);
                for (MetricDetails stat : taskStats) {
                    plotEntry.add(stat.getTime(), stat.getValue());
                }
                XYSeriesCollection plotCollection = new XYSeriesCollection();
                plotCollection.addSeries(plotEntry);
                Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(plotCollection, null);
                plotCollection = pair.getSecond();
                JFreeChart chartMetric = ChartHelper.createXYChart(null, plotCollection,
                        "Time (" + pair.getFirst() + ")", metricName, 2, 2, ChartHelper.ColorTheme.LIGHT);
                taskPlot.getMetricPlotDTOs().add(new MetricPlotDTO(metricName, null, new JCommonDrawableRenderer(chartMetric)));
            }
        }
        return taskPlots;
    }
}
