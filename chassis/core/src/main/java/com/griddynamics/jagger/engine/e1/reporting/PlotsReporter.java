package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.PlotIntegratedDto;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.model.*;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.reporting.chart.ChartHelper;
import com.griddynamics.jagger.util.Pair;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

public class PlotsReporter {
    
    public static final Comparator<TestDetailsNode> BY_TASK_NAME = Comparator.comparing(TestDetailsNode::getDisplayName);
    
    private Logger log = LoggerFactory.getLogger(PlotsReporter.class);

    private DatabaseService databaseService;

    private String sessionId;

    private Map<Long, MetricPlotDTOs> testIdToPlotsMap;

    private MetricPlotDTOs sessionScopePlots;


    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public MetricPlotDTOs getSessionScopePlots(String sessionId) {

        getData(sessionId);

        return sessionScopePlots;
    }


    public Map<Long, MetricPlotDTOs> getTestIdToPlotsMap(String sessionId) {

        getData(sessionId);

        return testIdToPlotsMap;
    }


    private void getData(String sessionId) {

        if (sessionId.equals(this.sessionId)) {
            // data already fetched
            return;
        }

        // fetch data
        this.sessionId = sessionId;

        SessionMatchingSetup sessionMatchingSetup = new SessionMatchingSetup(
                true,
                EnumSet.of(SessionMatchingSetup.MatchBy.ALL));

        RootNode controlTree = databaseService.getControlTreeForSessions(
                new HashSet<>(Collections.singletonList(sessionId)),
                sessionMatchingSetup);
        Collections.sort(controlTree.getDetailsNode().getTests(), BY_TASK_NAME);

        fetchSessionScopeData(controlTree);
        fetchPerTestData(controlTree);
    }

    private void fetchPerTestData(RootNode controlTree) {

        Set<MetricNode> allMetrics = new HashSet<MetricNode>();

        DetailsNode detailsNode = controlTree.getDetailsNode();
        if (detailsNode.getTests().isEmpty())
            return;

        for (TestDetailsNode testDetailsNode : detailsNode.getTests()) {
            allMetrics.addAll(testDetailsNode.getMetrics());
        }

        try {
            Map<MetricNode, PlotIntegratedDto> dataMap = databaseService.getPlotDataByMetricNode(allMetrics);

            for (TestDetailsNode testDetailsNode : detailsNode.getTests()) {
                TaskDataDto taskDataDto = testDetailsNode.getTaskDataDto();
                getPlotsReport(
                        dataMap,
                        testDetailsNode,
                        new PerTestReportPlotHelper(taskDataDto.getId(), taskDataDto.getTaskName())
                );
            }
        } catch (Exception e) {
            log.error("Unable to get metrics plots information for session " + this.sessionId, e);
        }
    }


    /**
     * Populate sessionScopePlots field with data.
     *
     * @param controlTree data structure
     */
    private void fetchSessionScopeData(RootNode controlTree) {


        MetricGroupNode<PlotNode> sessionScopeNode = controlTree.getDetailsNode().getSessionScopeNode();

        if (sessionScopeNode == null || sessionScopeNode.getChildren().isEmpty()) {
            sessionScopePlots = null;
            // no session scope plots
            return;
        }

        Set<MetricNode> allMetrics = new HashSet<MetricNode>(sessionScopeNode.getMetrics());
        try {
            getPlotsReport(
                    databaseService.getPlotDataByMetricNode(allMetrics),
                    sessionScopeNode,
                    new SessionScopeReportPlotHelper());
        } catch (Exception e) {
            log.error("Unable to fetch session scope plots for metrics ", allMetrics);
        }
    }


    private void getPlotsReport(
            Map<MetricNode, PlotIntegratedDto> dataMap,
            MetricGroupNode<? extends MetricNode> metricGroupNode,
            ReportPlotHelper reportPlotHelper) {

        if (metricGroupNode.getMetricGroupNodeList() != null) {
            for (MetricGroupNode<? extends MetricNode> metricGroup : metricGroupNode.getMetricGroupNodeList())
                getPlotsReport(dataMap, metricGroup, reportPlotHelper);
        }
        if (metricGroupNode.getMetricsWithoutChildren() != null) {

            String groupTitle = metricGroupNode.getDisplayName();

            for (MetricNode node : metricGroupNode.getMetricsWithoutChildren()) {
                if (dataMap.get(node).getPlotSeries().isEmpty()) {
                    log.warn(reportPlotHelper.getEmptyPlotSeriesWarnMessage(node));
                    continue;
                }

                MetricPlotDTOs metricPlotDTOs = reportPlotHelper.getMetricPlotDTOs();
                metricPlotDTOs.getMetricPlotDTOs().add(new MetricPlotDTO(
                        node.getDisplayName(),
                        node.getDisplayName(),
                        groupTitle,
                        makePlot(dataMap.get(node))));

                groupTitle = "";
            }
        }
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

    /**
     * Interface to use same function of plot generation for session scope and per test metrics (getPlotsReport(...))
     */
    private interface ReportPlotHelper {
        MetricPlotDTOs getMetricPlotDTOs();

        String getEmptyPlotSeriesWarnMessage(MetricNode metricNode);
    }

    /**
     * Use while session scope plot generation
     */
    private class SessionScopeReportPlotHelper implements ReportPlotHelper {
        @Override
        public MetricPlotDTOs getMetricPlotDTOs() {
            if (sessionScopePlots == null) {
                sessionScopePlots = new MetricPlotDTOs();
            }
            return sessionScopePlots;
        }

        @Override
        public String getEmptyPlotSeriesWarnMessage(MetricNode metricNode) {
            return "No session scope plot data for metric " + metricNode.getDisplayName() + " in session " + sessionId;
        }
    }

    /**
     * Use while per test plot generation
     */
    private class PerTestReportPlotHelper implements ReportPlotHelper {

        private final Long testId;
        private final String testName;

        public PerTestReportPlotHelper(Long testId, String testName) {
            this.testId = testId;
            this.testName = testName;
        }

        @Override
        public MetricPlotDTOs getMetricPlotDTOs() {
            if (testIdToPlotsMap == null) {
                testIdToPlotsMap = new HashMap<Long, MetricPlotDTOs>();
            }

            if (!testIdToPlotsMap.containsKey(testId))
                testIdToPlotsMap.put(testId, new MetricPlotDTOs());

            return testIdToPlotsMap.get(testId);
        }

        @Override
        public String getEmptyPlotSeriesWarnMessage(MetricNode metricNode) {
            return "No plot data for metric " + metricNode.getDisplayName() + " in test " + testName + " in session " + sessionId;
        }
    }

    private JCommonDrawableRenderer makePlot(PlotIntegratedDto plotIntegratedDto) {
        XYSeriesCollection plotCollection = new XYSeriesCollection();
        for (PlotSingleDto datasetDto : plotIntegratedDto.getPlotSeries()) {
            XYSeries plotEntry = new XYSeries(datasetDto.getLegend());
            for (PointDto point : datasetDto.getPlotData()) {                            // draw one line
                plotEntry.add(point.getX(), point.getY());
            }
            plotCollection.addSeries(plotEntry);
        }
        Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(plotCollection, null);
        plotCollection = pair.getSecond();

        JFreeChart chartMetric = ChartHelper.createXYChart(null, plotCollection,
                plotIntegratedDto.getXAxisLabel(), null, 2, 2, ChartHelper.ColorTheme.LIGHT);
        return new JCommonDrawableRenderer(chartMetric);
    }
}
