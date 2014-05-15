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

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.*;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import com.griddynamics.jagger.reporting.chart.ChartHelper;
import com.griddynamics.jagger.util.Pair;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Nikolay Musienko
 *         Date: 19.03.13
 */

public class MetricPlotsReporter extends AbstractMappedReportProvider<String> {
    private Logger log = LoggerFactory.getLogger(MetricPlotsReporter.class);

    private Map<Long, MetricPlotDTOs> plots;
    private Map<MetricNode, PlotSeriesDto> plotsReal = Collections.EMPTY_MAP;

    private String sessionId;
    private DatabaseService databaseService;

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

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
        private String groupTitle;

        public MetricPlotDTO(String metricName, String title, String groupTitle, JCommonDrawableRenderer metricPlot) {
            this.metricPlot = metricPlot;
            this.metricName = metricName;
            this.title = title;
            this.groupTitle = groupTitle;
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

        public String getGroupTitle() {
            return groupTitle;
        }

        public void setGroupTitle(String groupTitle) {
            this.groupTitle = groupTitle;
        }


    }

    @Override
    public JRDataSource getDataSource(String id) {
        sessionId = getSessionIdProvider().getSessionId();

        if (plots == null) {
            createFromTree();
        }
        if (plots.size() == 0) {
            return null;
        }
        MetricPlotDTOs result = plots.get(new Long(id));

        return new JRBeanCollectionDataSource(Collections.singleton(result));
    }

    // todo Session scope plots isn't supported by code below (JFG-724)
    private void createFromTree() {

        plots = new HashMap<Long, MetricPlotDTOs>();

        Set<MetricNode> allMetrics = new HashSet<MetricNode>();

        SessionMatchingSetup sessionMatchingSetup = new SessionMatchingSetup(
                databaseService.getWebClientProperties().isShowOnlyMatchedTests(),
                EnumSet.of(SessionMatchingSetup.MatchBy.ALL));
        RootNode rootNode = databaseService.getControlTreeForSessions(new HashSet<String>(Arrays.asList(sessionId)),sessionMatchingSetup);
        DetailsNode detailsNode = rootNode.getDetailsNode();
        if (detailsNode.getChildren().isEmpty())
            return;

        for (TestDetailsNode testDetailsNode : detailsNode.getTests()) {
            allMetrics.addAll(testDetailsNode.getMetrics());
        }

        try {
            plotsReal = databaseService.getPlotDataByMetricNode(allMetrics);
        } catch (Exception e) {
            log.error("Unable to get plots information for metrics");
        }

        for (TestDetailsNode testDetailsNode : detailsNode.getTests()) {
            getReport(testDetailsNode, testDetailsNode.getTaskDataDto().getId());
        }
    }

    private JCommonDrawableRenderer makePlot(PlotSeriesDto plotSeriesDto) {
        XYSeriesCollection plotCollection = new XYSeriesCollection();
        for (PlotDatasetDto datasetDto : plotSeriesDto.getPlotSeries()) {
            XYSeries plotEntry = new XYSeries(datasetDto.getLegend());
            for (PointDto point : datasetDto.getPlotData()) {                            // draw one line
                plotEntry.add(point.getX(), point.getY());
            }
            plotCollection.addSeries(plotEntry);
        }
        Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(plotCollection, null);
        plotCollection = pair.getSecond();

        JFreeChart chartMetric = ChartHelper.createXYChart(null, plotCollection,
                "Time, sec", null, 2, 2, ChartHelper.ColorTheme.LIGHT);
        return new JCommonDrawableRenderer(chartMetric);
    }


    private void getReport(MetricGroupNode metricGroupNode, Long testId) {
        try {

            if (metricGroupNode.getMetricGroupNodeList() != null) {
                for (MetricGroupNode metricGroup : (List<MetricGroupNode>) metricGroupNode.getMetricGroupNodeList())
                    getReport(metricGroup, testId);
            }
            if (metricGroupNode.getMetricsWithoutChildren() != null) {

                String groupTitle = metricGroupNode.getDisplayName();
                for (MetricNode node : (List<MetricNode>) metricGroupNode.getMetricsWithoutChildren()) {
                    if (plotsReal.get(node).getPlotSeries().isEmpty())   {
                        log.warn("No plot data for "+ node.getDisplayName());
                        continue;
                    }
                    if (!plots.containsKey(testId))
                        plots.put(testId, new MetricPlotDTOs());
                    plots.get(testId).getMetricPlotDTOs().add(new MetricPlotDTO(node.getDisplayName(), node.getDisplayName(), groupTitle, makePlot(plotsReal.get(node))));
                    groupTitle = "";
                }
            }
        } catch (Exception e) {
            log.error("Unable to take plot data for {}", metricGroupNode.getDisplayName());
        }
    }
}
