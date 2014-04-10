package com.griddynamics.jagger.webclient.client.trends;

import ca.nanometrics.gflot.client.*;
import ca.nanometrics.gflot.client.options.*;
import ca.nanometrics.gflot.client.options.Range;
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
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.*;
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


    private static TrendsUiBinder uiBinder = GWT.create(TrendsUiBinder.class);

    @UiField
    TabLayoutPanel searchTabPanel;

    @UiField
    TabLayoutPanel mainTabPanel;

    @UiField
    HTMLPanel plotPanel;

    @UiField(provided = true)
    DataGrid<SessionDataDto> sessionsDataGrid;

    @UiField(provided = true)
    SimplePager sessionsPager;

    @UiField
    ScrollPanel scrollPanelTrends;

    @UiField
    ScrollPanel scrollPanelMetrics;

    @UiField
    HTMLPanel plotTrendsPanel;

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
    void getHyperlink(ClickEvent event){
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

            if (sessionModel.getSelectedSet().size() == 1) {
                Set<String> sessionScopePlots = new HashSet<String>();
                for (String plotName : getLinkRepresentationForSessionScopePlots(controlTree.getRootNode().getDetailsNode().getSessionScopePlotsNode())) {
                    sessionScopePlots.add(plotName);
                }
                linkFragment.setSessionTrends(sessionScopePlots);
            }

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
                Set<MetricNameDto> metricsNames = controlTree.getCheckedMetrics(testNode);

                if (metricsNames.size() < testNode.getMetrics().size()) {
                    for (MetricNameDto mnd : metricsNames) {
                        testsMetrics.getMetrics().add(mnd.getMetricName());
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

                            if ((metricNameDto.getOrigin() == MetricNameDto.Origin.MONITORING) ||
                                (metricNameDto.getOrigin() == MetricNameDto.Origin.TEST_GROUP_METRIC)) {

                                MonitoringIdUtils.MonitoringId monitoringId= MonitoringIdUtils.splitMonitoringMetricId(metricNameDto.getMetricName());
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

    private List<String> getLinkRepresentationForSessionScopePlots(SessionScopePlotsNode sessionScopePlotsNode) {

        if (sessionScopePlotsNode == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> result = new ArrayList<String>();
        for (MonitoringSessionScopePlotNode monitoringSessionScopePlotNode : sessionScopePlotsNode.getPlots()) {
            if (controlTree.isChecked(monitoringSessionScopePlotNode)) {
                result.add(monitoringSessionScopePlotNode.getDisplayName());
            } else if (controlTree.isChosen(monitoringSessionScopePlotNode)) {
                for (SessionPlotNode spn : monitoringSessionScopePlotNode.getPlots()) {
                    if (controlTree.isChecked(spn)) {
                        result.add(spn.getPlotNameDto().getMetricName());
                    }
                }
            }
        }
        return result;
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
    HashMap<String, MetricDto> chosenMetrics = new HashMap<String, MetricDto>();
    Map<String, List<PlotSeriesDto>> chosenPlots = new TreeMap<String, List<PlotSeriesDto>>();

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

    public void getPropertiesUpdatePlace(final TrendsPlace place){

        CommonDataService.Async.getInstance().getWebClientProperties(new AsyncCallback<WebClientProperties>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel("Default properties will be used. Exception while properties retrieving: " + caught.getMessage());
                updatePlace(place);
            }

            @Override
            public void onSuccess(WebClientProperties result) {
                webClientProperties = result;
                updatePlace(place);
            }
        });
    }

    private Map<String,Set<String>> defaultMonitoringParameters;

    public void loadDefaultMonitoringParameters(){

        CommonDataService.Async.getInstance().getDefaultMonitoringParameters(new AsyncCallback<Map<String, Set<String>>>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel("Failed to get description of default monitoring parameters. Exception while description fetching: " + caught.getMessage());
                updatePlace(place);
            }

            @Override
            public void onSuccess(Map<String, Set<String>> result) {
                defaultMonitoringParameters = result;
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
    }


    private SimplePlot createPlot(final String id, Markings markings, String xAxisLabel,
                                  double yMinimum, boolean isMetric, final List<String> sessionIds) {
        PlotOptions plotOptions = new PlotOptions();
        plotOptions.setZoomOptions(new ZoomOptions().setAmount(1.02));
        plotOptions.setGlobalSeriesOptions(new GlobalSeriesOptions()
                .setLineSeriesOptions(new LineSeriesOptions().setLineWidth(1).setShow(true).setFill(0.1))
                .setPointsOptions(new PointsSeriesOptions().setRadius(1).setShow(true)).setShadowSize(0d));

        plotOptions.setPanOptions(new PanOptions().setInteractive(true));

        if (isMetric) {
            plotOptions.addXAxisOptions(new AxisOptions().setZoomRange(true).setTickDecimals(0)
                    .setTickFormatter(new TickFormatter() {
                        @Override
                        public String formatTickValue(double tickValue, Axis axis) {
                            if (tickValue >= 0 && tickValue < sessionIds.size())
                                return sessionIds.get((int) tickValue);
                            else
                                return "";
                        }
                    }));
        } else {
            plotOptions.addXAxisOptions(new AxisOptions().setZoomRange(true).setMinimum(0));
        }

        plotOptions.addYAxisOptions(new AxisOptions().setZoomRange(false).setMinimum(yMinimum));

        plotOptions.setLegendOptions(new LegendOptions().setNumOfColumns(2));

        if (markings == null) {
            // Make the grid hoverable
            plotOptions.setGridOptions(new GridOptions().setHoverable(true));
        } else {
            // Make the grid hoverable and add  markings
            plotOptions.setGridOptions(new GridOptions().setHoverable(true).setMarkings(markings).setClickable(true));
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
            plotTrendsPanel.clear();
            for(Map.Entry<String, MetricDto> entry : chosenMetrics.entrySet()) {
                renderPlots(
                        plotTrendsPanel,
                        Arrays.asList(entry.getValue().getPlotSeriesDto()),
                        entry.getKey(),
                        entry.getValue().getPlotSeriesDto().getYAxisMin(),
                        true
                );
            }
            scrollPanelTrends.scrollToBottom();
            hasChanged = false;
        }
    }

    private void onMetricsTabSelected() {

        mainTabPanel.forceLayout();
        controlTree.onMetricsTab();
        for (String plotId : chosenPlots.keySet()) {
            if (plotPanel.getElementById(plotId) == null) {
                renderPlots(plotPanel, chosenPlots.get(plotId), plotId);
                scrollPanelMetrics.scrollToBottom();
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
                return object.getStartDate();
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
                return object.getEndDate();
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
                sessionsFrom.setValue(null,true);
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

    private void renderPlots(HTMLPanel panel, List<PlotSeriesDto> plotSeriesDtoList, String id) {
        renderPlots(panel, plotSeriesDtoList, id, 0, false);
    }

    private void renderPlots(HTMLPanel panel, List<PlotSeriesDto> plotSeriesDtoList, String id, double yMinimum, boolean isMetric) {
        panel.add(loadIndicator);

        SimplePlot redrawingPlot = null;

        VerticalPanel plotGroupPanel = new VerticalPanel();
        plotGroupPanel.setWidth("100%");
        plotGroupPanel.getElement().setId(id);

        for (PlotSeriesDto plotSeriesDto : plotSeriesDtoList) {
            Markings markings = null;
            if (plotSeriesDto.getMarkingSeries() != null) {
                markings = new Markings();
                for (MarkingDto plotDatasetDto : plotSeriesDto.getMarkingSeries()) {
                    double x = plotDatasetDto.getValue();
                    markings.addMarking(new Marking().setX(new Range(x, x)).setLineWidth(1).setColor(plotDatasetDto.getColor()));
                }

                markingsMap.put(id, new TreeSet<MarkingDto>(plotSeriesDto.getMarkingSeries()));
            }

            final SimplePlot plot;
            PlotModel plotModel;
            if (isMetric) {
                List <String> sessionIds = new ArrayList<String>();
                for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                    // find all sessions in plot
                    for (PointDto pointDto : plotDatasetDto.getPlotData()) {
                        sessionIds.add(String.valueOf((int)pointDto.getX()));
                    }
                }
                plot = createPlot(id, markings, plotSeriesDto.getXAxisLabel(), yMinimum, isMetric, sessionIds);
                plotModel = plot.getModel();
                redrawingPlot = plot;
                int iter = 0;
                for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                    SeriesHandler handler = plotModel.addSeries(plotDatasetDto.getLegend(), plotDatasetDto.getColor());
                    // Populate plot with data
                    for (PointDto pointDto : plotDatasetDto.getPlotData()) {
                        handler.add(new DataPoint(iter ++, pointDto.getY()));
                    }
                }
            } else {
                plot = createPlot(id, markings, plotSeriesDto.getXAxisLabel(), yMinimum, isMetric, null);
                plotModel = plot.getModel();
                redrawingPlot = plot;
                for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                    SeriesHandler handler = plotModel.addSeries(plotDatasetDto.getLegend(), plotDatasetDto.getColor());

                    // Populate plot with data
                    for (PointDto pointDto : plotDatasetDto.getPlotData()) {
                        handler.add(new DataPoint(pointDto.getX(), pointDto.getY()));
                    }
                }
            }

            // Add X axis label
            Label xLabel = new Label(plotSeriesDto.getXAxisLabel());
            xLabel.addStyleName(getResources().css().xAxisLabel());

            Label plotHeader = new Label(plotSeriesDto.getPlotHeader());
            plotHeader.addStyleName(getResources().css().plotHeader());

            Label plotLegend = new Label("PLOT LEGEND");
            plotLegend.addStyleName(getResources().css().plotLegend());

            VerticalPanel vp = new VerticalPanel();
            vp.setWidth("100%");

            Label panLeftLabel = new Label();
            panLeftLabel.addStyleName(getResources().css().panLabel());
            panLeftLabel.getElement().appendChild(new Image(getResources().getArrowLeft()).getElement());
            panLeftLabel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    plot.pan(new Pan().setLeft(-100));
                }
            });

            Label panRightLabel = new Label();
            panRightLabel.addStyleName(getResources().css().panLabel());
            panRightLabel.getElement().appendChild(new Image(getResources().getArrowRight()).getElement());
            panRightLabel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    plot.pan(new Pan().setLeft(100));
                }
            });

            Label zoomInLabel = new Label("Zoom In");
            zoomInLabel.addStyleName(getResources().css().zoomLabel());
            zoomInLabel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    plot.zoom();
                }
            });

            Label zoomOutLabel = new Label("Zoom Out");
            zoomOutLabel.addStyleName(getResources().css().zoomLabel());
            zoomOutLabel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    plot.zoomOut();
                }
            });

            FlowPanel zoomPanel = new FlowPanel();
            zoomPanel.addStyleName(getResources().css().zoomPanel());
            zoomPanel.add(panLeftLabel);
            zoomPanel.add(panRightLabel);
            zoomPanel.add(zoomInLabel);
            zoomPanel.add(zoomOutLabel);

            vp.add(plotHeader);
            vp.add(zoomPanel);
            vp.add(plot);
            vp.add(xLabel);
            // Will be added if there is need it
            //vp.add(plotLegend);

            plotGroupPanel.add(vp);

        }
        int loadingId = panel.getWidgetCount() - 1;
        panel.remove(loadingId);
        panel.add(plotGroupPanel);

        // Redraw plot
        if (redrawingPlot != null) {
            redrawingPlot.redraw();
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
            summaryPanel.updateSessions(selected, webClientProperties);
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
            CheckHandlerMap.setSessionScopePlotFetcher(sessionScopePlotFetcher);
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
            ControlTreeCreatorService.Async.getInstance().getControlTreeForSessions(sessionIds, new AsyncCallback<RootNode>() {
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

            tempTree.setCheckedWithParent(result.getSummary().getSessionInfo());

            SessionScopePlotsNode sessionScopePlotsNode = result.getDetailsNode().getSessionScopePlotsNode();
            if (sessionScopePlotsNode != null) {
                for (MonitoringSessionScopePlotNode monitoringSessionScopePlotNode : sessionScopePlotsNode.getPlots()) {
                    if (place.getSessionTrends().contains(monitoringSessionScopePlotNode.getDisplayName())) {
                        tempTree.setCheckedWithParent(monitoringSessionScopePlotNode);
                        tempTree.setExpanded(sessionScopePlotsNode, true, false);
                    } else {
                        for (SessionPlotNode plotNode: monitoringSessionScopePlotNode.getPlots()) {
                            if (place.getSessionTrends().contains(plotNode.getPlotNameDto().getMetricName())) {
                                tempTree.setCheckedExpandedWithParent(plotNode);
                            }
                        }
                    }
                }
            }


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
                                    // workaround for back compatibility for standard metrics like Latency and Co
                                    else {
                                        if (metricNameDto.getMetricNameSynonyms() != null) {
                                            for (String synonym : metricNameDto.getMetricNameSynonyms()) {
                                                if (testsMetrics.getMetrics().contains(synonym)) {
                                                    tempTree.setCheckedExpandedWithParent(metricNode);
                                                    needTestInfo = true;
                                                    break;
                                                }
                                            }
                                        }
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
            for (TestNode testNode : rootNode.getSummary().getTests()) {
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
            SummaryNode summaryNode = rootNode.getSummary();

            fetchSessionInfoData(summaryNode.getSessionInfo());
            fetchMetricsForTests(summaryNode.getTests());

            fetchSessionScopePlots();
            fetchPlotsForTests();

        }

        private void fetchSessionScopePlots() {
            sessionScopePlotFetcher.fetchPlots(controlTree.getCheckedSessionScopePlots(), true);
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

            for (AbstractIdentifyNode node : result.getChildren()) {
                addToStore(temporaryStore, node);
            }

           return newTree;
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

        public void fetchMetrics(Set<MetricNameDto> metrics, final boolean enableTree) {

            hasChanged = true;
            if (metrics.isEmpty()) {
                // Remove plots from display which were unchecked
                chosenMetrics.clear();
                plotTrendsPanel.clear();
                summaryPanel.getSessionComparisonPanel().clearTreeStore();

                if (enableTree)
                    enableControl();
            } else {

                final ArrayList<MetricNameDto> notLoaded = new ArrayList<MetricNameDto>();
                final ArrayList<MetricDto> loaded = new ArrayList<MetricDto>();

                for (MetricNameDto metricName : metrics){
                    if (!summaryPanel.getCachedMetrics().containsKey(metricName)){
                        notLoaded.add(metricName);
                    }else{
                        MetricDto metric = summaryPanel.getCachedMetrics().get(metricName);
                        loaded.add(metric);
                    }
                }

                //Generate all id of plots which should be displayed
                Set<String> selectedMetricsIds = new HashSet<String>();
                for (MetricNameDto metricNameDto : metrics) {
                    selectedMetricsIds.add(generateMetricPlotId(metricNameDto));
                }

                List<MetricDto> toRemoveFromTable = new ArrayList<MetricDto>();
                // Remove plots from display which were unchecked
                Set<String> metricIdsSet = new HashSet<String>(chosenMetrics.keySet());
                for (String plotId : metricIdsSet) {
                    if (!selectedMetricsIds.contains(plotId)) {
                        toRemoveFromTable.add(chosenMetrics.get(plotId));
                        chosenMetrics.remove(plotId);
                    }
                }
                metricIdsSet = chosenMetrics.keySet();
                List<Widget> toRemove = new ArrayList<Widget>();
                for (int i = 0; i < plotTrendsPanel.getWidgetCount(); i ++ ) {
                    Widget widget = plotTrendsPanel.getWidget(i);
                    String wId = widget.getElement().getId();
                    if (!metricIdsSet.contains(wId)) {
                        toRemove.add(widget);
                    }
                }
                for (Widget widget : toRemove) {
                    plotTrendsPanel.remove(widget);
                }
                summaryPanel.getSessionComparisonPanel().removeRecords(toRemoveFromTable);

                if (!notLoaded.isEmpty()) {
                    disableControl();
                    MetricDataService.Async.getInstance().getMetrics(notLoaded, new AsyncCallback<List<MetricDto>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            caught.printStackTrace();
                            new ExceptionPanel(place, caught.getMessage());
                            if (enableTree)
                                enableControl();
                        }

                        @Override
                        public void onSuccess(List<MetricDto> result) {
                            loaded.addAll(result);
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

        private void renderMetrics(List<MetricDto> loaded) {
            MetricRankingProvider.sortMetrics(loaded);
            summaryPanel.getSessionComparisonPanel().addMetricRecords(loaded);
            renderMetricPlots(loaded);
            summaryPanelScrollPanel.scrollToBottom();
        }

        private void renderMetricPlots(List<MetricDto> result) {
            for (MetricDto metric : result) {

                // Generate DOM id for plot
                final String id = generateMetricPlotId(metric.getMetricName());

                if (!chosenMetrics.containsKey(id)) {
                    chosenMetrics.put(id, metric);
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

                PlotProviderService.Async.getInstance().getPlotData(selectedNodes, new AsyncCallback<Map<MetricNode, PlotSeriesDto>>() {

                    @Override
                    public void onFailure(Throwable caught) {

                        caught.printStackTrace();
                        new ExceptionPanel(place, caught.toString());
                        enableControl();
                    }

                    @Override
                    public void onSuccess(Map<MetricNode, PlotSeriesDto> result) {
                        for (MetricNode metricNode : result.keySet()) {
                            final String id;
                            // Generate DOM id for plot
                            // metricNode.Id - is unique key
                            id = generatePlotId(metricNode);

                            // If plot has already displayed, then pass it
                            if (chosenPlots.containsKey(id)) {
                                continue;
                            }

                            chosenPlots.put(id, Arrays.asList(result.get(metricNode)));

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

            if (metricNodes.isEmpty()) {
                return;
            }

            List<Widget> toRemove = new ArrayList<Widget>();
            Set<String> widgetIds = new HashSet<String>();
            for (MetricNode metricNode : metricNodes) {
                widgetIds.add(generatePlotId(metricNode));
            }
            for (int i = 0; i < plotPanel.getWidgetCount(); i++) {
                Widget widget = plotPanel.getWidget(i);
                String widgetId = widget.getElement().getId();
                if (widgetIds.contains(widgetId))
                    toRemove.add(widget);
            }
            for (Widget w : toRemove) {
                plotPanel.remove(w);
                chosenPlots.remove(w.getElement().getId());
            }
        }
    }

    /**
     * make server calls to fetch session scope plot data
     */
    private SessionScopePlotFetcher sessionScopePlotFetcher = new SessionScopePlotFetcher();

    public class SessionScopePlotFetcher extends PlotsServingBase {


        /**
         * Fetch selected plots
         * @param selected plotNames to fetch and render
         * @param enableTree tells return control or not
         */
        public void fetchPlots(Set<SessionPlotNameDto> selected, final boolean enableTree) {
            if (selected.isEmpty()) {
                if (enableTree)
                    enableControl();
            } else {
                disableControl();
                PlotProviderService.Async.getInstance().getSessionScopePlotData
                        (chosenSessions.get(0), selected,
                                new AsyncCallback<Map<SessionPlotNameDto, List<PlotSeriesDto>>>() {

                                    @Override
                                    public void onFailure(Throwable caught) {
                                        caught.printStackTrace();
                                        if (enableTree)
                                            enableControl();
                                        new ExceptionPanel(place, caught.toString());
                                    }

                                    @Override
                                    public void onSuccess(Map<SessionPlotNameDto, List<PlotSeriesDto>> result) {
                                        for (SessionPlotNameDto plotName : result.keySet()) {
                                            final String id = generateSessionScopePlotId(chosenSessions.get(0), plotName.getMetricName());

                                            // If plot has already displayed, then pass it
                                            if (chosenPlots.containsKey(id)) {
                                                continue;
                                            }

                                            chosenPlots.put(id, result.get(plotName));
                                        }
                                        if (mainTabPanel.getSelectedIndex() == tabMetrics.getTabIndex()) {
                                            onMetricsTabSelected();
                                        }
                                        if (enableTree)
                                            enableControl();
                                    }

                                }
                        );
            }
        }

        public void fetchPlot(SessionPlotNameDto plotName, final boolean enableTree) {
            Set<SessionPlotNameDto> set = new HashSet<SessionPlotNameDto>();
            set.add(plotName);
            fetchPlots(set, enableTree);
        }


        /**
         * Removes plots
         * @param plotNames plotNames to remove
         */
        public void removePlots(Set<? extends MetricName> plotNames) {

            if (plotNames.isEmpty()) {
                return;
            }

            if (plotNames.isEmpty()) {
                return;
            }

            Set<String> widgetIdsToRemove = generateSessionPlotIds(plotNames);
            List<Widget> toRemove = new ArrayList<Widget>();
            for (int i = 0; i < plotPanel.getWidgetCount(); i ++) {
                Widget widget = plotPanel.getWidget(i);
                String widgetId = widget.getElement().getId();
                if (widgetIdsToRemove.contains(widgetId)) {
                    toRemove.add(widget);
                }
            }
            for(Widget widget : toRemove) {
                plotPanel.remove(widget);
                chosenPlots.remove(widget.getElement().getId());
            }
        }

        public void removePlot(MetricName metricNameDto) {
            Set<MetricName> set = new HashSet<MetricName>();
            set.add(metricNameDto);
            removePlots(set);
        }


        private Set<String> generateSessionPlotIds(Set<? extends MetricName> selected) {
            HashSet<String> idSet = new HashSet<String>();
            for (MetricName plotName : selected) {
                idSet.add(generateSessionScopePlotId(chosenSessions.get(0), plotName.getMetricName()));
            }
            return idSet;
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
