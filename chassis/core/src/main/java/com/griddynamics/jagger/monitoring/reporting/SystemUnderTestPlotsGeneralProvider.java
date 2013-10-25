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

package com.griddynamics.jagger.monitoring.reporting;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.monitoring.MonitoringParameterBean;
import com.griddynamics.jagger.monitoring.model.MonitoringStatistics;
import com.griddynamics.jagger.reporting.chart.ChartHelper;
import com.griddynamics.jagger.util.Pair;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.awt.Font;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: dkotlyarov
 */
public class SystemUnderTestPlotsGeneralProvider extends AbstractMonitoringReportProvider<String> {
    private static final Logger log = LoggerFactory.getLogger(SystemUnderTestPlotsGeneralProvider.class);

    private DynamicPlotGroups plotGroups;
    private boolean showPlotsByGlobal;
    private boolean showPlotsByBox;
    private boolean showPlotsBySuT;
    private boolean showNumbers;

    private boolean enable;
    private Map<String, List<MonitoringReporterData>> taskPlots;

    private GeneralStatistics statistics = null;

    @Override
    public void clearCache() {
        super.clearCache();

        taskPlots = null;
    }

    @Override
    public JRDataSource getDataSource(String groupName) {
        log.debug("Report for param group with name {} requested", groupName);
        if (!this.enable) {
            return new JRBeanCollectionDataSource(Collections.emptySet());
        }

        if (taskPlots == null) {
            log.debug("Initing task plots");
            taskPlots = createTaskPlots();
        }

        loadMonitoringMap();

        log.debug("Loading data for task id {}", groupName);
        List<MonitoringReporterData> data = taskPlots.get(groupName);

        if (data == null) {
            log.debug("Data not found for task {}", groupName);
            String monitoringTaskId = relatedMonitoringTask(groupName);
            log.debug("Related task {}", monitoringTaskId);
            data = taskPlots.get(monitoringTaskId);
        }

        if (data == null) {
            log.warn("SuT plots not found for task with id {}", groupName);
            log.warn("Check that MonitoringAggregator is configured!!!");
        }

        return new JRBeanCollectionDataSource(data);
    }

    public Map<String, List<MonitoringReporterData>> createTaskPlots() {
        log.info("BEGIN: Create general task plots");

        Map<String, List<MonitoringReporterData>> taskPlots = new LinkedHashMap<String, List<MonitoringReporterData>>();
        GeneralStatistics generalStatistics = getStatistics();

        Set<String> taskIds = generalStatistics.findTaskIds();
        Set<String> boxIdentifiers = generalStatistics.findBoxIdentifiers();
        Set<String> sutUrls = generalStatistics.findSutUrls();
        for (GroupKey groupName : plotGroups.getPlotGroups().keySet()) {
            log.debug("    Create general task plots for group '{}'", groupName);

            if (showPlotsByGlobal) {
                log.debug("        Create general global task plots");

                List<MonitoringReporterData> plots = new LinkedList<MonitoringReporterData>();
                XYSeriesCollection chartsCollection = new XYSeriesCollection();
                LinkedHashMap<String, IntervalMarker> markers = new LinkedHashMap<String, IntervalMarker>();
                for (MonitoringParameter parameterId : plotGroups.getPlotGroups().get(groupName)) {
                    log.debug("            Create general global task plots for parameter '{}'", parameterId);

                    MonitoringParameterBean param = MonitoringParameterBean.copyOf(parameterId);
                    if (generalStatistics.hasGlobalStatistics(param)) {
                        XYSeries values = new XYSeries(param.getDescription());
                        long timeShift = 0;
                        int taskNum = 0;
                        for (String taskId : taskIds) {
                            log.debug("                Create general global task plots for task '{}'", taskId);

                            long maxTime = 0;
                            for (MonitoringStatistics monitoringStatistics : generalStatistics.findGlobalStatistics(taskId, param)) {
                                long time = monitoringStatistics.getTime();
                                double t = timeShift + time;
                                values.add(t, monitoringStatistics.getAverageValue());

                                if (time > maxTime) {
                                    maxTime = time;
                                }

                                if (showNumbers) {
                                    IntervalMarker marker = markers.get(taskId);
                                    if (marker == null) {
                                        marker = new IntervalMarker(t, t);
                                        marker.setLabel(monitoringStatistics.getTaskData().getNumber().toString());
                                        marker.setAlpha((taskNum % 2 == 0) ? 0.2f : 0.4f);

                                        int mod = taskNum % 3;
                                        if (mod == 0) {
                                            marker.setLabelAnchor(RectangleAnchor.CENTER);
                                        } else if (mod == 1) {
                                            marker.setLabelAnchor(RectangleAnchor.TOP);
                                        } else if (mod == 2) {
                                            marker.setLabelAnchor(RectangleAnchor.BOTTOM);
                                        }

                                        marker.setLabelFont(marker.getLabelFont().deriveFont(10.0f).deriveFont(Font.BOLD));
                                        markers.put(taskId, marker);
                                    } else {
                                        if (t < marker.getStartValue()) {
                                            marker.setStartValue(t);
                                        }
                                        if (t > marker.getEndValue()) {
                                            marker.setEndValue(t);
                                        }
                                    }
                                }
                            }
                            timeShift += maxTime;
                            taskNum++;
                        }
                        if (values.isEmpty()) {
                            values.add(0, 0);
                        }
                        chartsCollection.addSeries(values);
                    }
                }

                log.debug("group name \n{} \nparams {}]\n", groupName, Lists.newArrayList(plotGroups.getPlotGroups().get(groupName)));

                Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(chartsCollection, markers.values());

                chartsCollection = pair.getSecond();

                String name = groupName.getUpperName();

                if (chartsCollection.getSeriesCount() > 0) {
                    JFreeChart chart = ChartHelper.createXYChart(null, chartsCollection, "Time (" + pair.getFirst() + ")",
                            groupName.getLeftName(), 0, 1, ChartHelper.ColorTheme.LIGHT);

                    XYPlot plot = (XYPlot) chart.getPlot();
                    for (IntervalMarker marker : markers.values()) {
                        plot.addDomainMarker(marker);
                    }

                    MonitoringReporterData monitoringReporterData = new MonitoringReporterData();
                    monitoringReporterData.setParameterName(name);
                    monitoringReporterData.setTitle(name);
                    monitoringReporterData.setPlot(new JCommonDrawableRenderer(chart));
                    plots.add(monitoringReporterData);
                }

                if (!plots.isEmpty()) {
                    taskPlots.put(name, plots);
                }
            }

            if (showPlotsByBox) {
                log.debug("        Create general box task plots");

                for (String boxIdentifier : boxIdentifiers) {
                    log.debug("            Create general box task plots for box '{}'", boxIdentifier);

                    List<MonitoringReporterData> plots = new LinkedList<MonitoringReporterData>();
                    XYSeriesCollection chartsCollection = new XYSeriesCollection();
                    LinkedHashMap<String, IntervalMarker> markers = new LinkedHashMap<String, IntervalMarker>();
                    for (MonitoringParameter parameterId : plotGroups.getPlotGroups().get(groupName)) {
                        log.debug("                Create general box task plots for parameter '{}'", parameterId);

                        MonitoringParameterBean param = MonitoringParameterBean.copyOf(parameterId);
                        if (generalStatistics.hasBoxStatistics(param, boxIdentifier)) {
                            XYSeries values = new XYSeries(param.getDescription());
                            long timeShift = 0;
                            int taskNum = 0;
                            for (String taskId : taskIds) {
                                log.debug("                    Create general box task plots for task '{}'", taskId);

                                long maxTime = 0;
                                for (MonitoringStatistics monitoringStatistics : generalStatistics.findBoxStatistics(taskId, param, boxIdentifier)) {
                                    long time = monitoringStatistics.getTime();
                                    double t = timeShift + time;
                                    values.add(t, monitoringStatistics.getAverageValue());

                                    if (time > maxTime) {
                                        maxTime = time;
                                    }

                                    if (showNumbers) {
                                        IntervalMarker marker = markers.get(taskId);
                                        if (marker == null) {
                                            marker = new IntervalMarker(t, t);
                                            marker.setLabel(monitoringStatistics.getTaskData().getNumber().toString());
                                            marker.setAlpha((taskNum % 2 == 0) ? 0.2f : 0.4f);

                                            int mod = taskNum % 3;
                                            if (mod == 0) {
                                                marker.setLabelAnchor(RectangleAnchor.CENTER);
                                            } else if (mod == 1) {
                                                marker.setLabelAnchor(RectangleAnchor.TOP);
                                            } else if (mod == 2) {
                                                marker.setLabelAnchor(RectangleAnchor.BOTTOM);
                                            }

                                            marker.setLabelFont(marker.getLabelFont().deriveFont(10.0f).deriveFont(Font.BOLD));
                                            markers.put(taskId, marker);
                                        } else {
                                            if (t < marker.getStartValue()) {
                                                marker.setStartValue(t);
                                            }
                                            if (t > marker.getEndValue()) {
                                                marker.setEndValue(t);
                                            }
                                        }
                                    }
                                }
                                timeShift += maxTime;
                                taskNum++;
                            }
                            if (values.isEmpty()) {
                                values.add(0, 0);
                            }
                            chartsCollection.addSeries(values);
                        }
                    }

                    log.debug("group name \n{} \nparams {}]\n", groupName, Lists.newArrayList(plotGroups.getPlotGroups().get(groupName)));

                    Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(chartsCollection, markers.values());

                    chartsCollection = pair.getSecond();

                    String name = groupName.getUpperName() + " on " + boxIdentifier;

                    if (chartsCollection.getSeriesCount() > 0) {
                        JFreeChart chart = ChartHelper.createXYChart(null, chartsCollection, "Time (" + pair.getFirst() + ")",
                                groupName.getLeftName(), 0, 1, ChartHelper.ColorTheme.LIGHT);

                        XYPlot plot = (XYPlot) chart.getPlot();
                        for (IntervalMarker marker : markers.values()) {
                            plot.addDomainMarker(marker);
                        }

                        MonitoringReporterData monitoringReporterData = new MonitoringReporterData();
                        monitoringReporterData.setParameterName(name);
                        monitoringReporterData.setTitle(name);
                        monitoringReporterData.setPlot(new JCommonDrawableRenderer(chart));
                        plots.add(monitoringReporterData);
                    }

                    if (!plots.isEmpty()) {
                        taskPlots.put(name, plots);
                    }
                }
            }

            if (showPlotsBySuT) {
                log.debug("        Create general sut task plots");

                for (String sutUrl : sutUrls) {
                    log.debug("            Create general sut task plots for sut '{}'", sutUrl);

                    List<MonitoringReporterData> plots = new LinkedList<MonitoringReporterData>();
                    XYSeriesCollection chartsCollection = new XYSeriesCollection();
                    LinkedHashMap<String, IntervalMarker> markers = new LinkedHashMap<String, IntervalMarker>();
                    for (MonitoringParameter parameterId : plotGroups.getPlotGroups().get(groupName)) {
                        log.debug("                Create general sut task plots for parameter '{}'", parameterId);

                        MonitoringParameterBean param = MonitoringParameterBean.copyOf(parameterId);
                        if (generalStatistics.hasSutStatistics(param, sutUrl)) {
                            XYSeries values = new XYSeries(param.getDescription());
                            long timeShift = 0;
                            int taskNum = 0;
                            for (String taskId : taskIds) {
                                log.debug("                    Create general sut task plots for task '{}'", taskId);

                                long maxTime = 0;
                                for (MonitoringStatistics monitoringStatistics : generalStatistics.findSutStatistics(taskId, param, sutUrl)) {
                                    long time = monitoringStatistics.getTime();
                                    double t = timeShift + time;
                                    values.add(t, monitoringStatistics.getAverageValue());

                                    if (time > maxTime) {
                                        maxTime = time;
                                    }

                                    if (showNumbers) {
                                        IntervalMarker marker = markers.get(taskId);
                                        if (marker == null) {
                                            marker = new IntervalMarker(t, t);
                                            marker.setLabel(monitoringStatistics.getTaskData().getNumber().toString());
                                            marker.setAlpha((taskNum % 2 == 0) ? 0.2f : 0.4f);

                                            int mod = taskNum % 3;
                                            if (mod == 0) {
                                                marker.setLabelAnchor(RectangleAnchor.CENTER);
                                            } else if (mod == 1) {
                                                marker.setLabelAnchor(RectangleAnchor.TOP);
                                            } else if (mod == 2) {
                                                marker.setLabelAnchor(RectangleAnchor.BOTTOM);
                                            }

                                            marker.setLabelFont(marker.getLabelFont().deriveFont(10.0f).deriveFont(Font.BOLD));
                                            markers.put(taskId, marker);
                                        } else {
                                            if (t < marker.getStartValue()) {
                                                marker.setStartValue(t);
                                            }
                                            if (t > marker.getEndValue()) {
                                                marker.setEndValue(t);
                                            }
                                        }
                                    }
                                }
                                timeShift += maxTime;
                                taskNum++;
                            }
                            if (values.isEmpty()) {
                                values.add(0, 0);
                            }
                            chartsCollection.addSeries(values);
                        }
                    }

                    log.debug("group name \n{} \nparams {}]\n", groupName, Lists.newArrayList(plotGroups.getPlotGroups().get(groupName)));

                    Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(chartsCollection, markers.values());

                    chartsCollection = pair.getSecond();

                    String name = groupName.getUpperName() + " on " + sutUrl;

                    if (chartsCollection.getSeriesCount() > 0) {
                        JFreeChart chart = ChartHelper.createXYChart(null, chartsCollection, "Time (" + pair.getFirst() + ")",
                                groupName.getLeftName(), 0, 1, ChartHelper.ColorTheme.LIGHT);

                        XYPlot plot = (XYPlot) chart.getPlot();
                        for (IntervalMarker marker : markers.values()) {
                            plot.addDomainMarker(marker);
                        }

                        MonitoringReporterData monitoringReporterData = new MonitoringReporterData();
                        monitoringReporterData.setParameterName(name);
                        monitoringReporterData.setTitle(name);
                        monitoringReporterData.setPlot(new JCommonDrawableRenderer(chart));
                        plots.add(monitoringReporterData);
                    }

                    if (!plots.isEmpty()) {
                        taskPlots.put(name, plots);
                    }
                }
            }
        }

        clearStatistics();

        log.info("END: Create general task plots");

        return taskPlots;
    }

    public GeneralStatistics getStatistics() {
        if (statistics == null) {
            String sessionId = getSessionIdProvider().getSessionId();

            @SuppressWarnings("unchecked")
            List<MonitoringStatistics> st = getHibernateTemplate().find(
                    "select new MonitoringStatistics(" +
                            "ms.boxIdentifier, ms.systemUnderTestUrl, ms.sessionId, ms.taskData, ms.time, ms.parameterId, ms.averageValue" +
                            ") " +
                            "from MonitoringStatistics ms where ms.sessionId=? " +
                            "order by ms.taskData.number asc, ms.time asc", sessionId);

            statistics = new GeneralStatistics(sessionId, st);
        }
        return statistics;
    }

    public void clearStatistics() {
        statistics = null;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public DynamicPlotGroups getPlotGroups() {
        return plotGroups;
    }

    @Required
    public void setPlotGroups(DynamicPlotGroups plotGroups) {
        this.plotGroups = plotGroups;
    }

    public boolean isShowPlotsByGlobal() {
        return showPlotsByGlobal;
    }

    @Required
    public void setShowPlotsByGlobal(boolean showPlotsByGlobal) {
        this.showPlotsByGlobal = showPlotsByGlobal;
    }

    public boolean isShowPlotsByBox() {
        return showPlotsByBox;
    }

    @Required
    public void setShowPlotsByBox(boolean showPlotsByBox) {
        this.showPlotsByBox = showPlotsByBox;
    }

    public boolean isShowPlotsBySuT() {
        return showPlotsBySuT;
    }

    @Required
    public void setShowPlotsBySuT(boolean showPlotsBySuT) {
        this.showPlotsBySuT = showPlotsBySuT;
    }

    public boolean isShowNumbers() {
        return showNumbers;
    }

    @Required
    public void setShowNumbers(boolean showNumbers) {
        this.showNumbers = showNumbers;
    }

    public static class MonitoringReporterData {
        private String parameterName;
        private String title;
        private JCommonDrawableRenderer plot;

        public String getParameterName() {
            return this.parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public JCommonDrawableRenderer getPlot() {
            return this.plot;
        }

        public void setPlot(JCommonDrawableRenderer plot) {
            this.plot = plot;
        }
    }
}
