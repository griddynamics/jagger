package com.griddynamics.jagger.webclient.client.trends;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.*;
import com.googlecode.gflot.client.*;
import com.googlecode.gflot.client.options.*;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.*;
import com.griddynamics.jagger.util.FormatCalculator;
import com.griddynamics.jagger.util.MonitoringIdUtils;
import com.griddynamics.jagger.webclient.client.*;
import com.griddynamics.jagger.webclient.client.components.*;
import com.griddynamics.jagger.webclient.client.components.control.CheckHandlerMap;
import com.griddynamics.jagger.webclient.client.components.control.SimpleNodeValueProvider;
import com.griddynamics.jagger.webclient.client.data.*;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.client.handler.ShowCurrentValueHoverListener;
import com.griddynamics.jagger.webclient.client.handler.ShowTaskDetailsListener;
import com.griddynamics.jagger.webclient.client.mvp.JaggerPlaceHistoryMapper;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.griddynamics.jagger.webclient.client.resources.SessionDataGridResources;
import com.griddynamics.jagger.webclient.client.resources.SessionPagerResources;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/28/12
 */
public class Trends extends DefaultActivity {
    interface TrendsUiBinder extends UiBinder<Widget, Trends> {
    }

    private TabIdentifier tabSummary;
    private TabIdentifier tabTrends;
    private TabIdentifier tabMetrics;
    private TabIdentifier tabNodes;

    private TagBox tagFilterBox;

    private List<TagDto> allTags;
    private boolean allTagsLoadComplete = true;
    private Set<String> tagNames = new HashSet<String>();

    private DateTimeFormat dateFormatter = DateTimeFormat.getFormat(FormatCalculator.DATE_FORMAT);

    private static TrendsUiBinder uiBinder = GWT.create(TrendsUiBinder.class);

    @UiField
    TabLayoutPanel searchTabPanel;

    @UiField
    TabLayoutPanel mainTabPanel;

    @UiField
    PlotsPanel plotPanel;

    @UiField(provided = true)
    DataGrid<SessionDataDto> sessionsDataGrid;

    @UiField(provided = true)
    SimplePager sessionsPager;

    @UiField
    PlotsPanel plotTrendsPanel;

    @UiField
    ScrollPanel summaryPanelScrollPanel;

    @UiField
    SummaryPanel summaryPanel;

    @UiField
    NodesPanel nodesPanel;

    TextBox sessionIdsTextBox = new TextBox();

    TextBox sessionTagsTextBox = new TextBox();

    DateBox sessionsFrom = new DateBox();

    DateBox sessionsTo = new DateBox();

    @UiField
    HorizontalPanel tagsPanel;

    @UiField
    HorizontalPanel idsPanel;

    @UiField
    HorizontalPanel datesPanel;


    private Button tagButton;

    private Timer stopTypingSessionIdsTimer;
    private Timer stopTypingSessionTagsTimer;

    private PlotSaver plotSaver = new PlotSaver();


    @UiHandler("uncheckSessionsButton")
    void handleUncheckSessionsButtonClick(ClickEvent e) {
        MultiSelectionModel model = (MultiSelectionModel<?>) sessionsDataGrid.getSelectionModel();
        model.clear();
    }

    @UiHandler("showCheckedSessionsButton")
    void handleShowCheckedSessionsButtonClick(ClickEvent e) {
        Set<SessionDataDto> sessionDataDtoSet = ((MultiSelectionModel<SessionDataDto>) sessionsDataGrid.getSelectionModel()).getSelectedSet();
        filterSessions(sessionDataDtoSet);
    }

    @UiHandler("clearSessionFiltersButton")
    void handleClearSessionFiltersButtonClick(ClickEvent e) {
        sessionsTo.setValue(null, true);
        sessionsFrom.setValue(null, true);
        sessionTagsTextBox.setValue(null,true);
        sessionIdsTextBox.setValue(null, true);
        stopTypingSessionIdsTimer.schedule(10);
        stopTypingSessionTagsTimer.schedule(10);
    }

    @UiHandler("getHyperlink")
    void getHyperlink(ClickEvent event) {
        MultiSelectionModel<SessionDataDto> sessionModel = (MultiSelectionModel)sessionsDataGrid.getSelectionModel();
        if (sessionModel.getSelectedSet().isEmpty()) {
            return;
        }

        Set<String> selectedSessionIds = new HashSet<String>();
        for (SessionDataDto sessionDataDto : sessionModel.getSelectedSet()) {
            selectedSessionIds.add(sessionDataDto.getSessionId());
        }

        Set<TaskDataDto> allSelectedTests = controlTree.getSelectedTests();


        Map<Set<String>, List<TaskDataDto>> sessionsToTestsMap = new TreeMap<Set<String>, List<TaskDataDto>>(
            new Comparator<Set<String>>() {
                @Override
                public int compare(Set<String> o1, Set<String> o2) {
                    boolean c1 = o1.containsAll(o2);
                    boolean c2 = o2.containsAll(o1);
                    if (c1 && c2) return 0;
                    if (c1) return -1;
                    return 1;
                }
            });

        for (TaskDataDto taskDataDto : allSelectedTests) {
            Set<String> sessionIds = taskDataDto.getSessionIds();
            selectedSessionIds.removeAll(sessionIds);
            if (sessionsToTestsMap.containsKey(sessionIds)) {
                sessionsToTestsMap.get(sessionIds).add(taskDataDto);
            } else {
                List<TaskDataDto> taskList = new ArrayList<TaskDataDto>();
                taskList.add(taskDataDto);
                sessionsToTestsMap.put(sessionIds, taskList);
            }
        }
        if (!selectedSessionIds.isEmpty()) {
            sessionsToTestsMap.put(selectedSessionIds, null);
        }

        List<LinkFragment> linkFragments = new ArrayList<LinkFragment>();

        for (Map.Entry<Set<String>, List<TaskDataDto>> sessionsToTestsEntry : sessionsToTestsMap.entrySet()) {

            List<TaskDataDto> tests = sessionsToTestsEntry.getValue();

            LinkFragment linkFragment = new LinkFragment();

            if (tests == null) { // this is fragment with no tests chosen
                linkFragment.setSelectedSessionIds(sessionsToTestsEntry.getKey());
                linkFragments.add(linkFragment);
                continue;
            }

            Map<String, List<String>> trends = getTestTrendsMap(controlTree.getRootNode().getDetailsNode().getTests(), tests);

            HashSet<TestsMetrics> testsMetricses = new HashSet<TestsMetrics>(tests.size());
            HashMap<String, TestsMetrics> map = new HashMap<String, TestsMetrics>(tests.size());

            for (TaskDataDto taskDataDto : tests){
                TestsMetrics testsMetrics = new TestsMetrics(taskDataDto.getTaskName(), new HashSet<String>(), new HashSet<String>());

                TestNode testNode = controlTree.findTestNode(taskDataDto);
                if (testNode == null) continue;

                Set<MetricNode> checkedNodes = controlTree.getCheckedMetrics(testNode);
                if (checkedNodes.size() < testNode.getMetrics().size()) {
                    for (MetricNode metricNode : checkedNodes) {
                        for (MetricNameDto mnd : metricNode.getMetricNameDtoList()) {
                            testsMetrics.getMetrics().add(mnd.getMetricName());
                        }
                    }
                }

                testsMetricses.add(testsMetrics);
                map.put(taskDataDto.getTaskName(), testsMetrics);
            }

            for (Map.Entry<String, List<String>> entry : trends.entrySet()) {
                map.get(entry.getKey()).getTrends().addAll(entry.getValue());
            }

            linkFragment.setSelectedSessionIds(sessionsToTestsEntry.getKey());
            linkFragment.setSelectedTestsMetrics(testsMetricses);

            linkFragments.add(linkFragment);
        }

        TrendsPlace newPlace = new TrendsPlace(
                mainTabPanel.getSelectedIndex() == tabSummary.getTabIndex() ? tabSummary.getTabName() :
                        mainTabPanel.getSelectedIndex() == tabTrends.getTabIndex() ? tabTrends.getTabName() : tabMetrics.getTabName()
        );

        newPlace.setLinkFragments(linkFragments);

        String linkText = "http://" + Window.Location.getHost() + Window.Location.getPath() + Window.Location.getQueryString() +
                "#" + new JaggerPlaceHistoryMapper().getToken(newPlace);
        linkText = URL.encode(linkText);

        //create a dialog for copy link
        final DialogBox dialog = new DialogBox(false, true);
        dialog.setText("Share link");
        dialog.setModal(true);
        dialog.setAutoHideEnabled(true);
        dialog.setPopupPosition(event.getClientX(), event.getClientY());

        final TextArea textArea = new TextArea();
        textArea.setText(linkText);
        textArea.setWidth("300px");
        textArea.setHeight("40px");
        //select text
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                textArea.setVisible(true);
                textArea.setFocus(true);
                textArea.selectAll();
            }
        });

        dialog.add(textArea);

        dialog.show();

    }

    private String getMonitoringIdByMetricNameDtoId(String metricNameDtoId) {
        for (String defaultMonitoringParam : defaultMonitoringParameters.keySet()) {
            for (String id : defaultMonitoringParameters.get(defaultMonitoringParam)) {
                if (id.equals(metricNameDtoId)) {
                    return defaultMonitoringParam;
                }
            }
        }
        return null;
    }

    private Map<String, List<String>> getTestTrendsMap(List<TestDetailsNode> tests, List<TaskDataDto> taskDataDtos) {
        Map<String, List<String>> resultMap = new LinkedHashMap<String, List<String>>();

        for (TestDetailsNode test : tests) {
            if (controlTree.isChosen(test)) {
                if (taskDataDtos.contains(test.getTaskDataDto())) {

                    Map<String,Boolean> uniteAgentsForMonitoringNames = new HashMap<String, Boolean>();
                    List<String> trends = new ArrayList<String>();
                    List<String> trendsMonitoring = new ArrayList<String>();
                    for (PlotNode plotNode : test.getMetrics()) {

                        // temporary work around to make URL shorter starts here
                        // it groups metricNameDtoId|agentName id to old monitoringId|agentName
                        if (plotNode.getMetricNameDtoList().size() > 0) {
                            MetricNameDto metricNameDto = plotNode.getMetricNameDtoList().get(0);

                            if (metricNameDto.getOrigin() == MetricNameDto.Origin.TEST_GROUP_METRIC) {

                                MonitoringIdUtils.MonitoringId monitoringId = MonitoringIdUtils.splitMonitoringMetricId(metricNameDto.getMetricName());
                                if (monitoringId != null) {
                                    String monitoringOldName = getMonitoringIdByMetricNameDtoId(monitoringId.getMonitoringName());
                                    if (monitoringOldName != null) {
                                        if (!uniteAgentsForMonitoringNames.containsKey(monitoringOldName)){
                                            uniteAgentsForMonitoringNames.put(monitoringOldName,true);
                                        }

                                        if (controlTree.isChecked(plotNode)) {
                                            trendsMonitoring.add(monitoringOldName + MonitoringIdUtils.AGENT_NAME_SEPARATOR + monitoringId.getAgentName());
                                            // for this plotNode we are using work around. we will not go normal way
                                            continue;
                                        }
                                        else {
                                            // plot for some of agent of this monitoring metric is not checked
                                            // we will not process this monitoring metric in next workaround
                                            uniteAgentsForMonitoringNames.put(monitoringOldName,false);
                                        }
                                    }
                                }
                            }
                        }
                        // temporary work around to make URL shorter ends here

                        // this is correct way, but is has very long URL
                        if (controlTree.isChecked(plotNode)) {
                            for (MetricNameDto metricNameDto : plotNode.getMetricNameDtoList()) {
                                trends.add(metricNameDto.getMetricName());
                            }
                        }
                    }

                    // temporary work around to make URL shorter SECOND ROUND starts here
                    // it groups old monitoringId|agentName to old monitoringId
                    List<String> newTrendMonitoring = new ArrayList<String>();
                    for (Map.Entry<String,Boolean> canWeOptimizeMore : uniteAgentsForMonitoringNames.entrySet()) {
                        if (canWeOptimizeMore.getValue()) {
                            // remove monitoringId|agentName
                            Iterator<String> iterator = trendsMonitoring.iterator();
                            while (iterator.hasNext()) {
                                String id = iterator.next();
                                if (id.matches("^" + canWeOptimizeMore.getKey() + ".*")) {
                                    iterator.remove();
                                }
                            }
                            // leave monitoringId
                            newTrendMonitoring.add(canWeOptimizeMore.getKey());
                        }
                    }
                    trendsMonitoring.addAll(newTrendMonitoring);
                    // temporary work around to make URL shorter SECOND ROUND ends here

                    trends.addAll(trendsMonitoring);
                    resultMap.put(test.getTaskDataDto().getTaskName(), trends);
                }
            }
        }

        return resultMap;
    }


    private final Map<String, Set<MarkingDto>> markingsMap = new HashMap<String, Set<MarkingDto>>();

    private FlowPanel loadIndicator;

    private final SessionDataAsyncDataProvider sessionDataProvider = new SessionDataAsyncDataProvider();
    private final SessionDataForSessionIdsAsyncProvider sessionDataForSessionIdsAsyncProvider = new SessionDataForSessionIdsAsyncProvider();
    private final SessionDataForDatePeriodAsyncProvider sessionDataForDatePeriodAsyncProvider = new SessionDataForDatePeriodAsyncProvider();
    private final SessionDataForSessionTagsAsyncProvider sessionDataForSessionTagsAsyncProvider = new SessionDataForSessionTagsAsyncProvider();

    @UiField
    Widget widget;

    ControlTree<String> controlTree;

    private final ModelKeyProvider <AbstractIdentifyNode> modelKeyProvider = new ModelKeyProvider<AbstractIdentifyNode>() {

        @Override
        public String getKey(AbstractIdentifyNode item) {
            return item.getId();
        }
    };

    @UiField
    SimplePanel controlTreePanel;

    /**
     * used to disable sessionDataGrid while rpc call
     */
    @UiField
    ContentPanel sessionDataGridContainer;

    public Trends(JaggerResources resources) {
        super(resources);
        createWidget();
    }

    private TrendsPlace place;
    private boolean selectTests = false;

    /**
     * fields that contain gid/plot information
     * to provide rendering in time of choosing special tab(mainTab) to avoid view problems
     */
    private HashMap<MetricNode, SummaryIntegratedDto> chosenMetrics = new HashMap<MetricNode, SummaryIntegratedDto>();
    private Map<MetricNode, PlotIntegratedDto> chosenPlots = new HashMap<MetricNode, PlotIntegratedDto>();

    /**
     * Field to hold number of sessions that were chosen.
     * spike for rendering metrics plots
     */
    private ArrayList<String> chosenSessions = new ArrayList<String>();
    //tells if trends plot should be redraw
    private boolean hasChanged = false;

    public void updatePlace(TrendsPlace place){
        if (this.place != null)
            return;

        this.place = place;
        final TrendsPlace finalPlace = this.place;
        if (place.getSelectedSessionIds().isEmpty()){
            noSessionsFromLink();
            return;
        }

        loadRangeForSessionIds(place.getSelectedSessionIds());

        SessionDataService.Async.getInstance().getBySessionIds(0, place.getSelectedSessionIds().size(), place.getSelectedSessionIds(), new AsyncCallback<PagedSessionDataDto>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel(finalPlace , caught.getMessage());
                noSessionsFromLink();
            }

            @Override
            public void onSuccess(PagedSessionDataDto result) {
                for (SessionDataDto session : result.getSessionDataDtoList()){
                    sessionsDataGrid.getSelectionModel().setSelected(session, true);
                }
                sessionsDataGrid.getSelectionModel().addSelectionChangeHandler(new SessionSelectChangeHandler());
                sessionsDataGrid.getSelectionModel().setSelected(result.getSessionDataDtoList().iterator().next(), true);
                chooseTab(finalPlace.getToken());
            }
        });
        History.newItem(NameTokens.EMPTY);
    }

    private WebClientProperties webClientProperties = new WebClientProperties();
    private Map<String,Set<String>> defaultMonitoringParameters = Collections.emptyMap();

    public void getPropertiesUpdatePlace(final TrendsPlace place){

        CommonDataService.Async.getInstance().getWebClientStartProperties(new AsyncCallback<WebClientStartProperties>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel("Default properties will be used. Exception while properties retrieving: " + caught.getMessage());
                updatePlace(place);
            }

            @Override
            public void onSuccess(WebClientStartProperties result) {
                webClientProperties = result.getWebClientProperties();
                defaultMonitoringParameters = result.getDefaultMonitoringParameters();
                updatePlace(place);
            }
        });
    }

    private void noSessionsFromLink() {
        sessionsDataGrid.getSelectionModel().addSelectionChangeHandler(new SessionSelectChangeHandler());
        selectTests = true;
        chooseTab(place.getToken());
    }

    private void loadRangeForSessionIds(Set<String> sessionIds){
        final int rangeLength = sessionsDataGrid.getVisibleRange().getLength();
        SessionDataService.Async.getInstance().getStartPosition(sessionIds, new AsyncCallback<Long>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel(caught.getMessage());
            }

            @Override
            public void onSuccess(Long result) {
                sessionsDataGrid.setVisibleRange(result.intValue(), rangeLength);
            }
        });
    }

    private void filterSessions(Set<SessionDataDto> sessionDataDtoSet) {
        if (sessionDataDtoSet == null || sessionDataDtoSet.isEmpty()) {
            sessionIdsTextBox.setText(null);
            sessionTagsTextBox.setText(null);
            stopTypingSessionIdsTimer.schedule(10);
            stopTypingSessionTagsTimer.schedule(10);
            return;
        }

        final StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (SessionDataDto sessionDataDto : sessionDataDtoSet) {
            if (!first) {
                builder.append("/");
            }
            builder.append(sessionDataDto.getSessionId());
            first = false;
        }
        sessionIdsTextBox.setText(builder.toString());
        stopTypingSessionIdsTimer.schedule(10);
    }

    @Override
    protected Widget initializeWidget() {
        return widget;
    }

    private void createWidget() {
        setupSessionDataGrid();
        setupPager();
        setupLoadIndicator();

        uiBinder.createAndBindUi(this);

        setupTabPanel();
        setupSearchTabPanel();
        setupSessionNumberTextBox();
        setupSessionTagsTextBox();
        setupSessionsDateRange();
        setupControlTree();
    }

    private final Widget NO_SESSION_CHOSEN = new Label("Choose at least One session");

    private void setupControlTree() {

        controlTree = new ControlTree<String>(new TreeStore<AbstractIdentifyNode>(modelKeyProvider), new SimpleNodeValueProvider());
        setupControlTree(controlTree);

        Label label = new Label("Choose at least One session");
        label.setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant.startOf(HasDirection.Direction.DEFAULT));
        label.setStylePrimaryName(JaggerResources.INSTANCE.css().centered());
        label.setHeight("100%");
        NO_SESSION_CHOSEN.setStyleName(JaggerResources.INSTANCE.css().controlFont());
        controlTreePanel.add(NO_SESSION_CHOSEN);
    }

    private void setupControlTree(ControlTree<String> tree) {
        tree.setTitle("Control Tree");
        tree.setCheckable(true);
        tree.setCheckStyle(Tree.CheckCascade.NONE);
        tree.setCheckNodes(Tree.CheckNodes.BOTH);
        tree.setWidth("100%");
        tree.setHeight("100%");
        CheckHandlerMap.setTree(tree);

        plotPanel.setControlTree(tree);
        plotTrendsPanel.setControlTree(tree);
    }

    // @param yMinimum - set null if you don't want to set y minimum (will be set automatically)
    private SimplePlot createPlot(PlotsPanel panel, final String id, Markings markings, String xAxisLabel,
                                  Double yMinimum, boolean isMetric, final List<Integer> sessionIds) {
        PlotOptions plotOptions = PlotOptions.create();
        plotOptions.setZoomOptions(ZoomOptions.create().setAmount(1.02));
        plotOptions.setGlobalSeriesOptions(GlobalSeriesOptions.create()
                .setLineSeriesOptions(LineSeriesOptions.create().setLineWidth(1).setShow(true).setFill(0.1))
                .setPointsOptions(PointsSeriesOptions.create().setRadius(1).setShow(true)).setShadowSize(0d));

        plotOptions.setCanvasEnabled(true);

        FontOptions fontOptions = FontOptions.create()
                .setSize(11D)
                .setColor("black");

        AxisOptions xAxisOptions = AxisOptions.create().setZoomRange(true)
                .setFont(fontOptions);

        if (!panel.isEmpty()) {
            xAxisOptions.setMaximum(panel.getMaxXAxisVisibleValue());
            xAxisOptions.setMinimum(panel.getMinXAxisVisibleValue());
        } else {
            if (!isMetric)
                xAxisOptions.setMinimum(0);
        }

        if (isMetric) {
            // todo : JFG-803 simplify trends plotting mechanism
            xAxisOptions
                .setTickDecimals(0)
                .setTickFormatter(new TickFormatter() {
                    @Override
                    public String formatTickValue(double tickValue, Axis axis) {
                        if (tickValue >= 0 && tickValue < sessionIds.size())
                            return "" + sessionIds.get((int) tickValue);
                        else
                            return "";
                    }
                });
        }
        plotOptions.addXAxisOptions(xAxisOptions);

        AxisOptions yAxisOptions = AxisOptions.create()
                .setFont(fontOptions).setLabelWidth(40).setTickFormatter(new TickFormatter() {

                    private NumberFormat format;

                    @Override
                    public String formatTickValue(double tickValue, Axis axis) {
                        // decided to show values as 7 positions only

                        if (tickValue == 0) {
                            return "0";
                        }

                        if (format == null) {
                            double tempDouble = tickValue * 5;
                            format = NumberFormat.getFormat(FormatCalculator.getNumberFormat(tempDouble));
                        }

                        return format.format(tickValue).replace('E', 'e');
                    }
                })
                .setZoomRange(false);
        if (yMinimum != null) {
            yAxisOptions.setMinimum(yMinimum);
        }
        plotOptions.addYAxisOptions(yAxisOptions);

        plotOptions.setLegendOptions(LegendOptions.create().setShow(false));

        plotOptions.setCanvasEnabled(true);
        if (markings == null) {
            // Make the grid hoverable
            plotOptions.setGridOptions(GridOptions.create().setHoverable(true));
        } else {
            // Make the grid hoverable and add  markings
            plotOptions.setGridOptions(GridOptions.create().setHoverable(true).setMarkings(markings).setClickable(true));
        }

        // create the plot
        SimplePlot plot = new SimplePlot(plotOptions);
        plot.setHeight(200);
        plot.setWidth("100%");

        final PopupPanel popup = new PopupPanel();
        popup.addStyleName(getResources().css().infoPanel());
        final HTML popupPanelContent = new HTML();
        popup.add(popupPanelContent);

        // add hover listener
        if (isMetric) {
            plot.addHoverListener(new ShowCurrentValueHoverListener(popup, popupPanelContent, xAxisLabel, sessionIds), false);
        } else {
            plot.addHoverListener(new ShowCurrentValueHoverListener(popup, popupPanelContent, xAxisLabel, null), false);
        }

        if (!isMetric && markings != null && !markingsMap.isEmpty()) {
            final PopupPanel taskInfoPanel = new PopupPanel();
            taskInfoPanel.setWidth("200px");
            taskInfoPanel.addStyleName(getResources().css().infoPanel());
            final HTML taskInfoPanelContent = new HTML();
            taskInfoPanel.add(taskInfoPanelContent);
            taskInfoPanel.setAutoHideEnabled(true);

            plot.addClickListener(new ShowTaskDetailsListener(id, markingsMap, taskInfoPanel, 200, taskInfoPanelContent), false);
        }

        return plot;
    }

    private void setupTabPanel(){
        final int indexSummary = 0;
        final int indexTrends = 1;
        final int indexMetrics = 2;
        final int indexNodes = 3;

        tabSummary = new TabIdentifier(NameTokens.SUMMARY,indexSummary);
        tabTrends = new TabIdentifier(NameTokens.TRENDS,indexTrends);
        tabMetrics = new TabIdentifier(NameTokens.METRICS,indexMetrics);
        tabNodes = new TabIdentifier(NameTokens.NODES,indexNodes);

        mainTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                int selected = event.getSelectedItem();
                switch (selected) {
                    case indexSummary:
                        onSummaryTabSelected();
                        break;
                    case indexTrends:
                        onTrendsTabSelected();
                        break;
                    case indexMetrics:
                        onMetricsTabSelected();
                        break;
                    case indexNodes:
                        onNodesTabSelected();
                    default:
                }
            }
        });
    }

    private boolean needRefresh = false;
    private void onSummaryTabSelected() {
        mainTabPanel.forceLayout();
        controlTree.onSummaryTrendsTab();
        // to make columns fit 100% width if grid created not on Summary Tab
        //summaryPanel.getSessionComparisonPanel().refresh();
        if (needRefresh) {
            summaryPanel.getSessionComparisonPanel().refresh();
            needRefresh = false;
        }
    }

    private void onTrendsTabSelected() {
        mainTabPanel.forceLayout();
        controlTree.onSummaryTrendsTab();
        if (!chosenMetrics.isEmpty() && hasChanged) {
            for(Map.Entry<MetricNode, SummaryIntegratedDto> entry : chosenMetrics.entrySet()) {

                String plotId = entry.getKey().getId();
                if (plotTrendsPanel.containsElementWithId(plotId)) {
                    // plot already presented on trends panel
                    continue;
                }

                renderPlots(
                        plotTrendsPanel,
                        entry.getKey(),
                        entry.getValue().getPlotIntegratedDto(),
                        plotId,
                        true
                );
                plotTrendsPanel.scrollToBottom();
            }
            hasChanged = false;
        }
    }

    private void onMetricsTabSelected() {

        mainTabPanel.forceLayout();
        controlTree.onMetricsTab();
        for (MetricNode metricNode : chosenPlots.keySet()) {
            String id = metricNode.getId();
            if (!plotPanel.containsElementWithId(id)) {
                renderPlots(plotPanel, metricNode, chosenPlots.get(metricNode), id);
                plotPanel.scrollToBottom();
            }
        }
    }

    private void onNodesTabSelected() {
        mainTabPanel.forceLayout();
        controlTree.onSummaryTrendsTab();
        nodesPanel.getNodeInfo();
    }

    private void chooseTab(String token) {
        if (tabSummary.getTabName().equals(token)) {
            mainTabPanel.selectTab(tabSummary.getTabIndex());
        } else if (tabTrends.getTabName().equals(token)) {
            mainTabPanel.selectTab(tabTrends.getTabIndex());
        } else if (tabMetrics.getTabName().equals(token)) {
            mainTabPanel.selectTab(tabMetrics.getTabIndex());
        } else if (tabNodes.getTabName().equals(token)){
            mainTabPanel.selectTab(tabNodes.getTabIndex());
        } else {
            new ExceptionPanel("Unknown tab with name " + token + " selected");
        }
    }

    private void setupSessionDataGrid() {
        SessionDataGridResources resources = GWT.create(SessionDataGridResources.class);
        sessionsDataGrid = new DataGrid<SessionDataDto>(15, resources);
        sessionsDataGrid.setEmptyTableWidget(new Label("No Sessions"));

        // Add a selection model so we can select cells.
        final SelectionModel<SessionDataDto> selectionModel = new MultiSelectionModel<SessionDataDto>(new ProvidesKey<SessionDataDto>() {
            @Override
            public Object getKey(SessionDataDto item) {
                return item.getSessionId();
            }
        });
        sessionsDataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<SessionDataDto>createCheckboxManager());

        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable mouse selection.
        Column<SessionDataDto, Boolean> checkColumn =
                new Column<SessionDataDto, Boolean>(new CheckboxCell(true, false)) {
                    @Override
                    public Boolean getValue(SessionDataDto object) {
                        // Get the value from the selection model.
                        return selectionModel.isSelected(object);
                    }
                };
        sessionsDataGrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        sessionsDataGrid.setColumnWidth(checkColumn, 40, Style.Unit.PX);

        TextColumn<SessionDataDto> nameColumn = new TextColumn<SessionDataDto>() {
            @Override
            public String getCellStyleNames(Cell.Context context, SessionDataDto object) {
                return super.getCellStyleNames(context, object) + " " + JaggerResources.INSTANCE.css().controlFont();
            }

            @Override
            public String getValue(SessionDataDto object) {
                return object.getName();
            }
        };
        sessionsDataGrid.addColumn(nameColumn, "Name");
        sessionsDataGrid.setColumnWidth(nameColumn, 25, Style.Unit.PCT);

        TextColumn<SessionDataDto> startDateColumn = new TextColumn<SessionDataDto>() {

            @Override
            public String getCellStyleNames(Cell.Context context, SessionDataDto object) {
                return super.getCellStyleNames(context, object) + " " + JaggerResources.INSTANCE.css().controlFont();
            }

            @Override
            public String getValue(SessionDataDto object) {
                return dateFormatter.format(object.getStartDate());
            }
        };
        sessionsDataGrid.addColumn(startDateColumn, "Start Date");
        sessionsDataGrid.setColumnWidth(startDateColumn, 30, Style.Unit.PCT);


        TextColumn<SessionDataDto> endDateColumn = new TextColumn<SessionDataDto>() {

            @Override
            public String getCellStyleNames(Cell.Context context, SessionDataDto object) {
                return super.getCellStyleNames(context, object) + " " + JaggerResources.INSTANCE.css().controlFont();
            }

            @Override
            public String getValue(SessionDataDto object) {
                return dateFormatter.format(object.getEndDate());
            }
        };
        sessionsDataGrid.addColumn(endDateColumn, "End Date");
        sessionsDataGrid.setColumnWidth(endDateColumn, 30, Style.Unit.PCT);

        sessionDataProvider.addDataDisplay(sessionsDataGrid);
    }

    private void setupPager() {
        SimplePager.Resources pagerResources = GWT.create(SessionPagerResources.class);
        sessionsPager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        //sessionsPager.setStylePrimaryName(JaggerResources.INSTANCE.css().controlFont());
        sessionsPager.setDisplay(sessionsDataGrid);
    }

    private void setupLoadIndicator() {
        ImageResource imageResource = getResources().getLoadIndicator();
        Image image = new Image(imageResource);
        loadIndicator = new FlowPanel();
        loadIndicator.addStyleName(getResources().css().centered());
        loadIndicator.add(image);
    }

    private void setupSessionNumberTextBox() {
        stopTypingSessionIdsTimer = new Timer() {

            @Override
            public void run() {
                final String currentContent = sessionIdsTextBox.getText().trim();

                // If session ID text box is empty then load all sessions
                if (currentContent.isEmpty()) {
                    sessionDataProvider.addDataDisplayIfNotExists(sessionsDataGrid);
                    sessionDataForSessionIdsAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);

                    return;
                }

                Set<String> sessionIds = new HashSet<String>();
                if (currentContent.contains(",") || currentContent.contains(";") || currentContent.contains("/")) {
                    sessionIds.addAll(Arrays.asList(currentContent.split("\\s*[,;/]\\s*")));
                } else {
                    sessionIds.add(currentContent);
                }

                sessionDataForSessionIdsAsyncProvider.setSessionIds(sessionIds);

                sessionDataProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForDatePeriodAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForSessionTagsAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForSessionIdsAsyncProvider.addDataDisplayIfNotExists(sessionsDataGrid);
            }
        };

        sessionIdsTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                sessionsFrom.setValue(null, true);
                sessionsTo.setValue(null, true);
                sessionTagsTextBox.setValue(null, true);
                stopTypingSessionIdsTimer.schedule(500);
            }
        });
    }

    private void setupSessionsDateRange() {
        DateTimeFormat format = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_NUM_DAY);

        sessionsFrom.setFormat(new DateBox.DefaultFormat(format));
        sessionsTo.setFormat(new DateBox.DefaultFormat(format));

        sessionsFrom.getTextBox().addValueChangeHandler(new EmptyDateBoxValueChangePropagator(sessionsFrom));
        sessionsTo.getTextBox().addValueChangeHandler(new EmptyDateBoxValueChangePropagator(sessionsTo));

        final ValueChangeHandler<Date> valueChangeHandler = new ValueChangeHandler<Date>() {

            @Override
            public void onValueChange(ValueChangeEvent<Date> dateValueChangeEvent) {

                sessionTagsTextBox.setValue(null, true);
                sessionIdsTextBox.setValue(null, true);
                Date fromDate = sessionsFrom.getValue();
                Date toDate = sessionsTo.getValue();

                if (fromDate == null || toDate == null) {
                    sessionDataProvider.addDataDisplayIfNotExists(sessionsDataGrid);
                    sessionDataForDatePeriodAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);

                    return;
                }

                sessionDataForDatePeriodAsyncProvider.setDateRange(fromDate, toDate);

                sessionDataProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForSessionIdsAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForSessionTagsAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForDatePeriodAsyncProvider.addDataDisplayIfNotExists(sessionsDataGrid);
            }
        };

        sessionsTo.addValueChangeHandler(valueChangeHandler);
        sessionsFrom.addValueChangeHandler(valueChangeHandler);
    }


    private void setupSessionTagsTextBox() {

        stopTypingSessionTagsTimer = new Timer() {

            @Override
            public void run() {

                final String generalContent = sessionTagsTextBox.getText().trim();

                // If session tags text box is empty then load all sessions
                if (generalContent.isEmpty()) {
                    sessionDataProvider.addDataDisplayIfNotExists(sessionsDataGrid);
                    sessionDataForSessionTagsAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                    return;
                }

                if (generalContent.contains(",") || generalContent.contains(";") || generalContent.contains("/")) {
                    tagNames.addAll(Arrays.asList(generalContent.split("\\s*[,;/]\\s*")));
                } else {
                    tagNames.add(generalContent);
                }

                sessionDataForSessionTagsAsyncProvider.setTagNames(tagNames);
                sessionDataProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForDatePeriodAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForSessionIdsAsyncProvider.removeDataDisplayIfNotExists(sessionsDataGrid);
                sessionDataForSessionTagsAsyncProvider.addDataDisplayIfNotExists(sessionsDataGrid);
            }
        };

        sessionTagsTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                sessionsTo.setValue(null, true);
                sessionsFrom.setValue(null, true);
                sessionIdsTextBox.setValue(null, true);
                tagNames.clear();
                stopTypingSessionTagsTimer.schedule(500);
            }
        });
        tagFilterBox.addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                sessionsTo.setValue(null, true);
                sessionsFrom.setValue(null, true);
                sessionIdsTextBox.setValue(null, true);
                if (!tagNames.isEmpty())
                    sessionTagsTextBox.setValue(toParsableString(tagNames));
                tagNames.clear();
                stopTypingSessionTagsTimer.schedule(500);
            }
        });
    }

    private void renderPlots(PlotsPanel panel, MetricNode metricNode, PlotIntegratedDto plotSeriesDto, String id) {
        renderPlots(panel, metricNode, plotSeriesDto, id, false);
    }

    private void renderPlots(final PlotsPanel panel, MetricNode metricNode, PlotIntegratedDto plotSeriesDto, String id, boolean isTrend) {

        Markings markings = null;
        if (plotSeriesDto.getMarkingSeries() != null) {
            markings = Markings.create();
            for (MarkingDto plotDatasetDto : plotSeriesDto.getMarkingSeries()) {
                double x = plotDatasetDto.getValue();
                markings.addMarking(Marking.create().setX(com.googlecode.gflot.client.options.Range.create().setFrom(x).setTo(x)).setLineWidth(1).setColor(plotDatasetDto.getColor()));
            }

            markingsMap.put(id, new TreeSet<MarkingDto>(plotSeriesDto.getMarkingSeries()));
        }

        final SimplePlot plot;
        List<Integer> trendSessionIds = null;
        if (isTrend) {
            // Trends plot panel

            // todo : JFG-803 simplify trends plotting mechanism
            trendSessionIds = new ArrayList<Integer>();
            double yMin = Double.MAX_VALUE;
            for (PlotSingleDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                // find all sessions in plot
                for (PointDto pointDto : plotDatasetDto.getPlotData()) {
                    int sId = (int) pointDto.getX();
                    if (!trendSessionIds.contains(sId)) {
                        trendSessionIds.add(sId);
                    }
                    if (pointDto.getY() < yMin) {
                        yMin = pointDto.getY();
                    }
                }
            }
            Collections.sort(trendSessionIds);
            plot = createPlot(panel, id, markings, plotSeriesDto.getXAxisLabel(), yMin, true, trendSessionIds);
        } else {
            plot = createPlot(panel, id, markings, plotSeriesDto.getXAxisLabel(), null, false, null);
        }

        LegendTree legendTree = new LegendTree(plot, panel, trendSessionIds);
        MetricGroupNode<LegendNode> inTree = plotSeriesDto.getLegendTree();

        translateIntoTree(inTree, legendTree);

        // All lines checked by default
        legendTree.checkAll();


        // Add X axis label
        final String xAxisLabel = plotSeriesDto.getXAxisLabel();

        Label zoomInLabel = new Label("Zoom In");
        zoomInLabel.addStyleName(getResources().css().zoomLabel());
        zoomInLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                panel.zoomIn();
            }
        });

        Label zoomBack = new Label("Zoom default");
        zoomBack.addStyleName(getResources().css().zoomLabel());
        zoomBack.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                panel.zoomDefault(plot);
            }
        });

        Label zoomOutLabel = new Label("Zoom Out");
        zoomOutLabel.addStyleName(getResources().css().zoomLabel());
        zoomOutLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                panel.zoomOut();
            }
        });

        FlowPanel zoomPanel = new FlowPanel();
        zoomPanel.addStyleName(getResources().css().zoomPanel());
        zoomPanel.add(zoomInLabel);
        zoomPanel.add(zoomOutLabel);
        zoomPanel.add(zoomBack);


        PlotRepresentation plotRepresentation = new PlotRepresentation(
                metricNode,
                zoomPanel,
                plot,
                legendTree,
                xAxisLabel,
                plotSeriesDto);

        PlotContainer pc = new PlotContainer(id, plotSeriesDto.getPlotHeader(), plotRepresentation, plotSaver);

        panel.addElement(pc);
    }


    /**
     * Populate Tree tree with data.
     * @param inTree model of tree with data
     * @param tree tree that will be populated with data
     */
    private void translateIntoTree(
            AbstractIdentifyNode inTree,
            AbstractTree<AbstractIdentifyNode, ?> tree) {

        TreeStore<AbstractIdentifyNode> ll = tree.getStore();

        for (AbstractIdentifyNode ln : inTree.getChildren()) {
            addToStore(ll, ln);
        }
    }


    private void addToStore(TreeStore<AbstractIdentifyNode> store, AbstractIdentifyNode node) {
        store.add(node);

        for (AbstractIdentifyNode child : node.getChildren()) {
            addToStore(store, child, node);
        }
    }

    private void addToStore(TreeStore<AbstractIdentifyNode> store, AbstractIdentifyNode node, AbstractIdentifyNode parent) {
        store.add(parent, node);
        for (AbstractIdentifyNode child : node.getChildren()) {

            try {
                addToStore(store, child, node);
            }
            catch (AssertionError e) {
                new ExceptionPanel(place, "Was not able to insert node with id '" + child.getId() + "' and name '"
                        + child.getDisplayName() + "' into control tree. Id is already in use. Error message:\n" + e.getMessage());
            }
        }
    }


    private void enableControl() {
        sessionDataGridContainer.enable();
        controlTree.enableTree();
    }


    private void disableControl() {
        sessionDataGridContainer.disable();
        controlTree.disable();
    }


    /**
     * Handles selection on SessionDataGrid
     */
    private class SessionSelectChangeHandler implements SelectionChangeEvent.Handler {

        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            // Currently selection model for sessions is a single selection model
            Set<SessionDataDto> selected = ((MultiSelectionModel<SessionDataDto>) event.getSource()).getSelectedSet();

            controlTree.disable();
            //Refresh summary
            chosenMetrics.clear();
            chosenPlots.clear();
            summaryPanel.updateSessions(selected, webClientProperties, dateFormatter);
            needRefresh = mainTabPanel.getSelectedIndex() != tabSummary.getTabIndex();
            // Reset node info and remember selected sessions
            // Fetch info when on Nodes tab
            nodesPanel.clear();
            nodesPanel.updateSetup(selected,place);
            if (mainTabPanel.getSelectedIndex() == tabNodes.getTabIndex())
                nodesPanel.getNodeInfo();

            CheckHandlerMap.setTestInfoFetcher(testInfoFetcher);
            CheckHandlerMap.setMetricFetcher(metricFetcher);
            CheckHandlerMap.setTestPlotFetcher(testPlotFetcher);
            CheckHandlerMap.setSessionComparisonPanel(summaryPanel.getSessionComparisonPanel());

            // Clear plots display
            plotPanel.clear();
            plotTrendsPanel.clear();
            // Clear markings dto map
            markingsMap.clear();
            chosenSessions.clear();

            if(selected.isEmpty()){
                controlTreePanel.clear();
                controlTreePanel.add(NO_SESSION_CHOSEN);

                controlTree.clearStore();
                return;
            }

            final Set<String> sessionIds = new HashSet<String>();
            for (SessionDataDto sessionDataDto : selected) {
                sessionIds.add(sessionDataDto.getSessionId());
                chosenSessions.add(sessionDataDto.getSessionId());
            }
            Collections.sort(chosenSessions, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return (Long.parseLong(o2) - Long.parseLong(o1)) > 0 ? 0 : 1;
                }
            });

            disableControl();
            ControlTreeCreatorService.Async.getInstance().getControlTreeForSessions(sessionIds,
                webClientProperties.isShowOnlyMatchedTests(),
                new AsyncCallback<RootNode>() {
                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                    new ExceptionPanel(caught.getMessage());
                    enableControl();
                }

                @Override
                public void onSuccess(RootNode result) {
                    if (!selectTests) { // if it was link
                        selectTests = true;

                        processLink(result);

                    } else if (controlTree.getStore().getAllItemsCount() == 0) {
                        controlTree = createControlTree(result);

                        controlTree.setRootNode(result);
                        controlTreePanel.clear();
                        controlTreePanel.add(controlTree);

                        if (mainTabPanel.getSelectedIndex() != tabMetrics.getTabIndex()) {
                            controlTree.onSummaryTrendsTab();
                        } else {
                            controlTree.onMetricsTab();
                        }
                        enableControl();

                    } else {
                        updateControlTree(result);
                        enableControl();
                    }

                }

            });
        }

        private void processLink(RootNode result) {

            ControlTree<String> tempTree = createControlTree(result);

            tempTree.disableEvents();

            tempTree.setCheckedWithParent(result.getSummaryNode().getSessionInfo());


            for (LinkFragment linkFragment : place.getLinkFragments()) {
                for (TestsMetrics testsMetrics : linkFragment.getSelectedTestsMetrics()) {
                    TestNode testNode = getTestNodeByNameAndSessionIds(testsMetrics.getTestName(), linkFragment.getSelectedSessionIds(), result);
                    boolean needTestInfo = false;
                    if (testNode == null) { // have not find appropriate TestNode
                        new ExceptionPanel("could not find Test with test name \'" + testsMetrics.getTestName() + "\' for summary");
                        continue;
                    } else {

                        if (testsMetrics.getMetrics().isEmpty()) {
                            // check all metrics
                            tempTree.setCheckedExpandedWithParent(testNode);
                        } else {
                            tempTree.setExpanded(testNode, true);
                            for (MetricNode metricNode : testNode.getMetrics()) {
                                for (MetricNameDto metricNameDto : metricNode.getMetricNameDtoList()) {
                                    if (testsMetrics.getMetrics().contains(metricNameDto.getMetricName())) {
                                        tempTree.setCheckedExpandedWithParent(metricNode);
                                        needTestInfo = true;
                                    }
                                }
                            }
                            if (needTestInfo) {
                                tempTree.setCheckedWithParent(testNode.getTestInfo());
                            }
                        }
                    }

                    TestDetailsNode testDetailsNode = getTestDetailsNodeByNameAndSessionIds(testsMetrics.getTestName(), linkFragment.getSelectedSessionIds(), result);
                    if (testDetailsNode == null) { // have not find appropriate TestDetailNode
                        new ExceptionPanel("could not find Test with test name \'" + testsMetrics.getTestName() + "\' for details");
                    } else {

                        // To be compatible with old hyperlinks with monitoring parameters
                        // Replace old monitoring parameters Ids with new metricNameDto ids
                        Set<String> newTrends = new HashSet<String>();
                        Iterator<String> iterator = testsMetrics.getTrends().iterator();
                        while (iterator.hasNext()) {
                            String id = iterator.next();
                            for (String defaultMonitoringParam : defaultMonitoringParameters.keySet()) {
                                if (id.matches("^" + defaultMonitoringParam + ".*")) {
                                    if (id.equals(defaultMonitoringParam)) {
                                        // select all
                                        for (String metricId : defaultMonitoringParameters.get(defaultMonitoringParam)) {
                                            String regex = "^" + MonitoringIdUtils.getEscapedStringForRegex(metricId) + ".*";
                                            newTrends.addAll(getMatchingMetricNameDtos(regex,testDetailsNode));
                                        }
                                    }
                                    else {
                                        // selection per agent node
                                        MonitoringIdUtils.MonitoringId monitoringId = MonitoringIdUtils.splitMonitoringMetricId(id);
                                        if (monitoringId != null) {
                                            for (String metricId : defaultMonitoringParameters.get(defaultMonitoringParam)) {
                                                String monitoringMetricId = MonitoringIdUtils.getMonitoringMetricId(metricId, monitoringId.getAgentName());
                                                String regex = "^" + MonitoringIdUtils.getEscapedStringForRegex(monitoringMetricId) + ".*";
                                                newTrends.addAll(getMatchingMetricNameDtos(regex,testDetailsNode));
                                            }
                                        }
                                    }
                                    // old monitoring id was replaced with new metricNameDto ids
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                        testsMetrics.getTrends().addAll(newTrends);

                        for (PlotNode plotNode : testDetailsNode.getMetrics()) {
                            for (MetricNameDto metricNameDto : plotNode.getMetricNameDtoList()) {
                                if (testsMetrics.getTrends().contains(metricNameDto.getMetricName())) {
                                    tempTree.setCheckedExpandedWithParent(plotNode);
                                }

                            }
                        }

                    }
                }
            }


            tempTree.enableEvents();

            controlTreePanel.clear();
            controlTree = tempTree;
            controlTree.setRootNode(result);

            if (mainTabPanel.getSelectedIndex() == tabMetrics.getTabIndex()) {
                controlTree.onMetricsTab();
            } else {
                controlTree.onSummaryTrendsTab();
            }

            controlTreePanel.add(controlTree);

            fireCheckEvents();
        }

        private Set<String> getMatchingMetricNameDtos(String regex, TestDetailsNode testDetailsNode) {
            Set <String> result = new HashSet<String>();
            for (PlotNode plotNode : testDetailsNode.getMetrics()) {
                for (MetricNameDto metricNameDto : plotNode.getMetricNameDtoList()) {
                    String metricId = metricNameDto.getMetricName();
                    if (metricId.matches(regex)) {
                        result.add(metricId);
                    }
                }
            }
            return result;
        }

        /**
         * @param testName name of the test
         * @param selectedSessionIds session ids of chosen test
         * @return null if no Test found
         */
        private TestNode getTestNodeByNameAndSessionIds(String testName, Set<String> selectedSessionIds, RootNode rootNode) {
            for (TestNode testNode : rootNode.getSummaryNode().getTests()) {
                if (testNode.getDisplayName().equals(testName)) {
                    if (testNode.getTaskDataDto().getSessionIds().containsAll(selectedSessionIds)
                            && selectedSessionIds.containsAll(testNode.getTaskDataDto().getSessionIds()))
                        return testNode;
                }
            }
            return null;
        }


        /**
         * @param testName name of the test
         * @return null if no Test found
         */
        public TestDetailsNode getTestDetailsNodeByNameAndSessionIds(String testName, Set<String> selectedSessionIds, RootNode rootNode) {

            for (TestDetailsNode testNode : rootNode.getDetailsNode().getTests()) {
                if (testNode.getDisplayName().equals(testName)) {
                    if (testNode.getTaskDataDto().getSessionIds().containsAll(selectedSessionIds)
                            && selectedSessionIds.containsAll(testNode.getTaskDataDto().getSessionIds()))
                        return testNode;
                }
            }
            return null;
        }

       private void updateControlTree(RootNode result) {
            ControlTree<String> tempTree = createControlTree(result);

            tempTree.disableEvents();
            for (AbstractIdentifyNode s : controlTree.getStore().getAll()) {
                AbstractIdentifyNode model = tempTree.getStore().findModelWithKey(s.getId());
                if (model == null) continue;
                tempTree.setChecked(model, controlTree.getChecked(s));
                if (controlTree.isExpanded(s)) {
                    tempTree.setExpanded(model, true);
                } else {
                    tempTree.setExpanded(model, false);
                }
            }
            // collapse/expand root nodes
            for (AbstractIdentifyNode s : controlTree.getStore().getRootItems()) {
                AbstractIdentifyNode model = tempTree.getStore().findModelWithKey(s.getId());
                if (controlTree.isExpanded(s)) {
                    tempTree.setExpanded(model, true);
                } else {
                    tempTree.setExpanded(model, false);
                }
            }
            tempTree.enableEvents();

            controlTreePanel.clear();
            controlTree = tempTree;
            controlTree.setRootNode(result);
            controlTreePanel.add(controlTree);

            fireCheckEvents();
        }

        private void fireCheckEvents() {

            disableControl();

            RootNode rootNode = controlTree.getRootNode();
            SummaryNode summaryNode = rootNode.getSummaryNode();

            fetchSessionInfoData(summaryNode.getSessionInfo());
            fetchMetricsForTests(summaryNode.getTests());
            fetchPlotsForTests();

        }

        private void fetchPlotsForTests() {
            testPlotFetcher.fetchPlots(controlTree.getCheckedPlots());
        }

        private void fetchMetricsForTests(List<TestNode> testNodes) {

            List<TaskDataDto> taskDataDtos = new ArrayList<TaskDataDto>();
            for (TestNode testNode : testNodes) {
                if (controlTree.isChecked(testNode.getTestInfo())) {
                    taskDataDtos.add(testNode.getTaskDataDto());
                }
            }

            testInfoFetcher.fetchTestInfo(taskDataDtos, false);
            metricFetcher.fetchMetrics(controlTree.getCheckedMetrics(), false);
        }

        private void fetchSessionInfoData(SessionInfoNode sessionInfoNode) {
            if (Tree.CheckState.CHECKED.equals(controlTree.getChecked(sessionInfoNode))) {
                summaryPanel.getSessionComparisonPanel().addSessionInfo();
            }
        }

        private ControlTree<String> createControlTree(RootNode result) {

            TreeStore<AbstractIdentifyNode> temporaryStore = new TreeStore<AbstractIdentifyNode>(modelKeyProvider);
            ControlTree<String> newTree = new ControlTree<String>(temporaryStore, new SimpleNodeValueProvider());
            setupControlTree(newTree);

            translateIntoTree(result, newTree);

            return newTree;
        }
    }


    /**
     * make server calls to fetch testInfo
     */
    private TestInfoFetcher testInfoFetcher = new TestInfoFetcher();


    public class TestInfoFetcher {
        public void fetchTestInfo(final Collection<TaskDataDto> taskDataDtos, final boolean enableTree) {

            TestInfoService.Async.getInstance().getTestInfos(taskDataDtos, new AsyncCallback<Map<TaskDataDto, Map<String, TestInfoDto>>>() {
                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                    new ExceptionPanel(place, caught.getMessage());
                    if (enableTree)
                        enableControl();
                }

                @Override
                public void onSuccess(Map<TaskDataDto, Map<String, TestInfoDto>> result) {
                    SessionComparisonPanel scp =  summaryPanel.getSessionComparisonPanel();
                    for (TaskDataDto td : result.keySet()) {
                        scp.addTestInfo(td, result.get(td));
                    }
                    if (enableTree)
                        enableControl();
                }
            });
        }
    }


    /**
     * make server calls to fetch metric data (summary table, trends plots)
     */
    private MetricFetcher metricFetcher = new MetricFetcher();

    public class MetricFetcher extends PlotsServingBase {

        public void fetchMetrics(Set<MetricNode> metrics, final boolean enableTree) {

            hasChanged = true;
            if (metrics.isEmpty()) {
                // Remove plots from display which were unchecked
                chosenMetrics.clear();
                plotTrendsPanel.clear();
                summaryPanel.getSessionComparisonPanel().clearTreeStore();

                if (enableTree)
                    enableControl();
            } else {

                final Set<MetricNode> notLoaded = new HashSet<MetricNode>();
                final Map<MetricNode, SummaryIntegratedDto> loaded = new HashMap<MetricNode, SummaryIntegratedDto>();

                for (MetricNode metricNode : metrics){
                    if (!summaryPanel.getCachedMetrics().containsKey(metricNode)){
                        notLoaded.add(metricNode);
                    }else{
                        loaded.put(metricNode, summaryPanel.getCachedMetrics().get(metricNode));
                    }
                }

                //Generate all id of plots which should be displayed
                Set<String> selectedMetricsIds = new HashSet<String>();
                for (MetricNode metricNode : metrics) {
                    selectedMetricsIds.add(metricNode.getId());
                }

                List<SummaryIntegratedDto> toRemoveFromTable = new ArrayList<SummaryIntegratedDto>();
                // Remove plots from display which were unchecked
                Set<MetricNode> metricNodesToRemove = new HashSet<MetricNode>();
                for (MetricNode metricNode : chosenMetrics.keySet()) {
                    if (!selectedMetricsIds.contains(metricNode.getId())) {
                        toRemoveFromTable.add(chosenMetrics.get(metricNode));
                        metricNodesToRemove.add(metricNode);
                    }
                }
                if (!metricNodesToRemove.isEmpty()) {
                    plotTrendsPanel.removeByMetricNodes(metricNodesToRemove);
                    for (MetricNode metricNode : metricNodesToRemove) {
                        chosenMetrics.remove(metricNode);
                    }
                }

                summaryPanel.getSessionComparisonPanel().removeRecords(toRemoveFromTable);

                if (!notLoaded.isEmpty()) {
                    disableControl();
                    MetricDataService.Async.getInstance().getMetrics(notLoaded, webClientProperties.isEnableDecisionsPerMetricHighlighting(),
                        new AsyncCallback<Map<MetricNode, SummaryIntegratedDto>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            caught.printStackTrace();
                            new ExceptionPanel(place, caught.getMessage());
                            if (enableTree)
                                enableControl();
                        }

                        @Override
                        public void onSuccess(Map<MetricNode, SummaryIntegratedDto> result) {
                            loaded.putAll(result);
                            renderMetrics(loaded);
                            if (enableTree)
                                enableControl();
                        }
                    });
                } else {
                    renderMetrics(loaded);
                }
            }
        }

        private void renderMetrics(Map<MetricNode, SummaryIntegratedDto> loaded) {
            summaryPanel.getSessionComparisonPanel().addMetricRecords(loaded);
            renderMetricPlots(loaded);
            summaryPanelScrollPanel.scrollToBottom();
        }

        private void renderMetricPlots(Map<MetricNode, SummaryIntegratedDto> result) {
            for (MetricNode metricNode : result.keySet()) {

                if (!chosenMetrics.containsKey(metricNode)) {
                    chosenMetrics.put(metricNode, result.get(metricNode));
                }
            }
            if (mainTabPanel.getSelectedIndex() == tabTrends.getTabIndex()) {
                onTrendsTabSelected();
            }
        }
    }

    /**
     * make server calls to fetch test scope plot data
     */
    private TestPlotFetcher testPlotFetcher = new TestPlotFetcher();

    public class TestPlotFetcher extends PlotsServingBase {

        public void fetchPlots(Set<MetricNode> selectedNodes) {
            if (selectedNodes.isEmpty()) {
                enableControl();
            } else {
                disableControl();

                PlotProviderService.Async.getInstance().getPlotData(selectedNodes, new AsyncCallback<Map<MetricNode, PlotIntegratedDto>>() {

                    @Override
                    public void onFailure(Throwable caught) {

                        caught.printStackTrace();
                        new ExceptionPanel(place, caught.toString());
                        enableControl();
                    }

                    @Override
                    public void onSuccess(Map<MetricNode, PlotIntegratedDto> result) {
                        for (MetricNode metricNode : result.keySet()) {

                            // DOM id for plot = metricNode.Id - is unique key
                            // If plot has already displayed, then pass it
                            if (chosenPlots.containsKey(metricNode)) {
                                continue;
                            }

                            chosenPlots.put(metricNode, result.get(metricNode));

                        }
                        if (mainTabPanel.getSelectedIndex() == tabMetrics.getTabIndex()) {
                            onMetricsTabSelected();
                        }
                        enableControl();
                    }
                });
            }
        }

        /**
         * Removes plots
         * @param metricNodes metricNodes to remove
         */
        public void removePlots(Set<MetricNode> metricNodes) {

            plotPanel.removeByMetricNodes(metricNodes);
            for (MetricNode metricNode : metricNodes) {
                chosenPlots.remove(metricNode);
            }
        }
    }


    private void allTags() {

        allTags = new ArrayList<TagDto>();
        SessionDataService.Async.getInstance().getAllTags(new AsyncCallback<List<TagDto>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new ExceptionPanel("Fail to fetch all tags from the database: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(List<TagDto> tagDtos) {
                allTags.addAll(tagDtos);
                allTagsLoadComplete = true;
            }
        });
    }

    void setupSearchTabPanel() {

        final int indexId = 0;
        final int indexTag = 1;
        final int indexDate = 2;
        allTags();
        tagFilterBox = new TagBox();
        tagButton = new Button("...");
        tagButton.setStyleName(JaggerResources.INSTANCE.css().tagButton());
        tagButton.setSize("30px","23px");
        tagButton.setEnabled(allTagsLoadComplete);

        tagButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                tagNames.clear();
                tagFilterBox.popUpForFilter(allTags, tagNames);
            }
        });
        Label from = new Label("From ");
        Label to = new Label(" to ");
        from.setStyleName(JaggerResources.INSTANCE.css().searchPanel());
        to.setStyleName(JaggerResources.INSTANCE.css().searchPanel());

        datesPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        setPanel(datesPanel, from, sessionsFrom, to, sessionsTo);
        datesPanel.setBorderWidth(0);
        sessionsFrom.setSize("97%","21px");
        sessionsFrom.setStyleName(JaggerResources.INSTANCE.css().searchPanel());

        sessionsTo.setSize("97%","21px");
        sessionsTo.setStyleName(JaggerResources.INSTANCE.css().searchPanel());

        DockPanel dockTag = new DockPanel();
        dockTag.setBorderWidth(0);
        dockTag.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        sessionTagsTextBox.setSize("108%","21px");
        sessionTagsTextBox.setStyleName(JaggerResources.INSTANCE.css().searchPanel());
        dockTag.setSize("100%","100%");
        dockTag.add(sessionTagsTextBox,DockPanel.WEST);
        dockTag.add(tagButton,DockPanel.EAST);


        tagsPanel.setBorderWidth(0);
        setPanel(tagsPanel,dockTag);


        setPanel(idsPanel, sessionIdsTextBox);

        idsPanel.setBorderWidth(0);
        sessionIdsTextBox.setSize("99%", "21px");
        sessionIdsTextBox.setStyleName(JaggerResources.INSTANCE.css().searchPanel());

        searchTabPanel.selectTab(indexId);

        searchTabPanel.getTabWidget(indexId).setTitle("Search by a session's id");
        searchTabPanel.getTabWidget(indexTag).setTitle("Search by session's tags");
        searchTabPanel.getTabWidget(indexDate).setTitle("Search by a session's date");


        searchTabPanel.setTitle("A search bar");

        searchTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                int selected = event.getSelectedItem();
                switch (selected) {
                    case indexId:
                        onIdSearchTabSelected();
                        break;
                    case indexTag:
                        onTagSearchTabSelected();
                        break;
                    case indexDate:
                        onDateSearchTabSelected();
                        break;
                    default:
                }
            }
        });

    }
    private void onDateSearchTabSelected() {
        searchTabPanel.forceLayout();
    }

    private void onIdSearchTabSelected() {
        searchTabPanel.forceLayout();
    }

    private void onTagSearchTabSelected() {
        searchTabPanel.forceLayout();

    }

    private void setPanel(HorizontalPanel panel, Widget... widgets){
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        for (Widget widget:widgets){
            panel.add(widget);
        }
        panel.setSize("100%","100%");


    }
//    Tab index should be defined in single place
//    to avoid problems during adding/deleting new tabs
    private class TabIdentifier {

        public TabIdentifier(String tabName, int tabIndex) {
            this.tabName = tabName;
            this.tabIndex = tabIndex;
        }

        public String getTabName() {
            return tabName;
        }

        public int getTabIndex() {
            return tabIndex;
        }

        private String tabName = "";
        private int tabIndex = 0;
    }

    private String toParsableString(Collection T){
        String str="";
        for(Object t:T){
            str+=t.toString()+'/';
        }
        return str;
    }
}
