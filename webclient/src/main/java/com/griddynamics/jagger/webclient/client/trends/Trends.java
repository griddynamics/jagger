package com.griddynamics.jagger.webclient.client.trends;

import ca.nanometrics.gflot.client.*;
import ca.nanometrics.gflot.client.options.*;
import ca.nanometrics.gflot.client.options.Range;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.griddynamics.jagger.webclient.client.*;
import com.griddynamics.jagger.webclient.client.components.MetricPanel;
import com.griddynamics.jagger.webclient.client.components.SessionPlotPanel;
import com.griddynamics.jagger.webclient.client.components.SummaryPanel;
import com.griddynamics.jagger.webclient.client.data.*;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.client.handler.ShowCurrentValueHoverListener;
import com.griddynamics.jagger.webclient.client.handler.ShowTaskDetailsListener;
import com.griddynamics.jagger.webclient.client.mvp.JaggerPlaceHistoryMapper;
import com.griddynamics.jagger.webclient.client.mvp.NameTokens;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/28/12
 */
public class Trends extends DefaultActivity {
    interface TrendsUiBinder extends UiBinder<Widget, Trends> {
    }

    private static TrendsUiBinder uiBinder = GWT.create(TrendsUiBinder.class);

    private static final int MAX_PLOT_COUNT = 30;

    @UiField
    TabLayoutPanel mainTabPanel;

    @UiField
    HTMLPanel plotPanel;

    @UiField(provided = true)
    DataGrid<SessionDataDto> sessionsDataGrid;

    @UiField(provided = true)
    CellTable<TaskDataDto> testDataGrid;

    @UiField(provided = true)
    SimplePager sessionsPager;

    @UiField(provided = true)
    CellTree taskDetailsTree;

    @UiField
    MetricPanel metricPanel;

    @UiField
    ScrollPanel scrollPanelTrends;

    @UiField
    ScrollPanel scrollPanelMetrics;

    @UiField
    HTMLPanel plotTrendsPanel;

    @UiField
    SummaryPanel summaryPanel;

    @UiField
    VerticalPanel sessionScopePlotList;

    SessionPlotPanel sessionPlotPanel;

    @UiField
    TextBox sessionIdsTextBox;

    private Timer stopTypingSessionIdsTimer;

    @UiField
    DateBox sessionsFrom;

    @UiField
    DateBox sessionsTo;

    @UiField
    Panel trendsDetails;

    @UiField
    SplitLayoutPanel settingsPanel;

    @UiField
    DeckPanel testsMetricsPanel;

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
        sessionIdsTextBox.setText(null);
        stopTypingSessionIdsTimer.schedule(10);
    }

    @UiHandler("getHyperlink")
    void getHyperlink(ClickEvent event){
        MultiSelectionModel<SessionDataDto> sessionModel = (MultiSelectionModel)sessionsDataGrid.getSelectionModel();
        MultiSelectionModel<TaskDataDto> testModel = (MultiSelectionModel)testDataGrid.getSelectionModel();

        Set<SessionDataDto> sessions = sessionModel.getSelectedSet();

        Set<TaskDataDto> tests = testModel.getSelectedSet();

        Set<MetricNameDto> metrics = metricPanel.getSelected();

        TaskDataTreeViewModel taskDataTreeViewModel = (TaskDataTreeViewModel) taskDetailsTree.getTreeViewModel();
        Set<PlotNameDto> trends = taskDataTreeViewModel.getSelectionModel().getSelectedSet();

        HashSet<String> sessionsIds = new HashSet<String>();
        HashSet<TestsMetrics> testsMetricses = new HashSet<TestsMetrics>(tests.size());
        HashMap<String, TestsMetrics> map = new HashMap<String, TestsMetrics>(tests.size());

        for (SessionDataDto session : sessions){
            sessionsIds.add(session.getSessionId());
        }

        for (TaskDataDto taskDataDto : tests){
            TestsMetrics testsMetrics = new TestsMetrics(taskDataDto.getTaskName(), new HashSet<String>(), new HashSet<String>());
            testsMetricses.add(testsMetrics);
            map.put(taskDataDto.getTaskName(), testsMetrics);
        }

        for (MetricNameDto metricNameDto : metrics){
            map.get(metricNameDto.getTests().getTaskName()).getMetrics().add(metricNameDto.getName());
        }

        for (PlotNameDto plotNameDto : trends){
            map.get(plotNameDto.getTest().getTaskName()).getTrends().add(plotNameDto.getPlotName());
        }

        TrendsPlace newPlace = new TrendsPlace(
            mainTabPanel.getSelectedIndex() == 0 ? NameTokens.SUMMARY :
                    mainTabPanel.getSelectedIndex() == 1 ? NameTokens.TRENDS : NameTokens.METRICS
        );

        newPlace.setSelectedSessionIds(sessionsIds);
        newPlace.setSelectedTestsMetrics(testsMetricses);
        newPlace.setSessionTrends(sessionPlotPanel.getSelected());

        String linkText = Window.Location.getHost() + Window.Location.getPath() + Window.Location.getQueryString() +
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

    private final Map<String, Set<MarkingDto>> markingsMap = new HashMap<String, Set<MarkingDto>>();

    private FlowPanel loadIndicator;

    private final SessionDataAsyncDataProvider sessionDataProvider = new SessionDataAsyncDataProvider();
    private final SessionDataForSessionIdsAsyncProvider sessionDataForSessionIdsAsyncProvider = new SessionDataForSessionIdsAsyncProvider();
    private final SessionDataForDatePeriodAsyncProvider sessionDataForDatePeriodAsyncProvider = new SessionDataForDatePeriodAsyncProvider();

    @UiField
    Widget widget;

    public Trends(JaggerResources resources) {
        super(resources);
        createWidget();
    }

    private TrendsPlace place;
    private boolean selectTests = false;

    // cash for chosen metric spike for jfg-418
    MetricFullData chosenMetrics = new MetricFullData();

    //tells if trends plot should be redraw
    private boolean hasChanged = false;
    private boolean hasChangedGrid = false;

    public void updatePlace(TrendsPlace place){
        if (this.place != null)
            return;

        this.place = place;
        final TrendsPlace finalPlace = this.place;
        if (place.getSelectedSessionIds().isEmpty()){

            sessionsDataGrid.getSelectionModel().addSelectionChangeHandler(new SessionSelectChangeHandler());

            selectTests = true;
            testDataGrid.getSelectionModel().addSelectionChangeHandler(new TestSelectChangeHandler());

            TaskDataTreeViewModel viewModel = (TaskDataTreeViewModel)taskDetailsTree.getTreeViewModel();
            viewModel.getSelectionModel().addSelectionChangeHandler(new TaskPlotSelectionChangedHandler());

            metricPanel.addSelectionListener(new MetricsSelectionChangedHandler());

            chooseTab(place.getToken());
            return;
        }

        SessionDataService.Async.getInstance().getBySessionIds(0, place.getSelectedSessionIds().size(), place.getSelectedSessionIds(), new AsyncCallback<PagedSessionDataDto>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
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

    private void filterSessions(Set<SessionDataDto> sessionDataDtoSet) {
        if (sessionDataDtoSet == null || sessionDataDtoSet.isEmpty()) {
            sessionIdsTextBox.setText(null);
            stopTypingSessionIdsTimer.schedule(10);

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
        setupTestDataGrid();
        setupSessionDataGrid();
        setupTestDetailsTree();
        setupPager();
        setupLoadIndicator();

        uiBinder.createAndBindUi(this);

        setupTabPanel();
        setupSessionNumberTextBox();
        setupSessionsDateRange();
        setupSettingsPanel();
    }

    /**
     * Field to hold number of sessions that were chosen.
     * spike for rendering metrics plots
     */
    private ArrayList<Long> chosenSessions = new ArrayList<Long>();

    private SimplePlot createPlot(final String id, Markings markings, String xAxisLabel, double yMinimum, boolean isMetric) {
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
                            if (tickValue >= 0 && tickValue < chosenSessions.size())
                                return String.valueOf(chosenSessions.get((int) tickValue));
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
            plot.addHoverListener(new ShowCurrentValueHoverListener(popup, popupPanelContent, xAxisLabel, chosenSessions), false);
        } else {
            plot.addHoverListener(new ShowCurrentValueHoverListener(popup, popupPanelContent, xAxisLabel, null), false);
        }

        if (!isMetric && markings != null && markingsMap != null && !markingsMap.isEmpty()) {
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

    private void setupSettingsPanel(){
        SplitLayoutPanel root = (SplitLayoutPanel) widget;
        root.setWidgetToggleDisplayAllowed(settingsPanel, true);
        testsMetricsPanel.showWidget(0);

        sessionPlotPanel = new SessionPlotPanel(new SessionScopePlotCheckBoxClickHandler(), plotPanel);
        sessionScopePlotList.add(sessionPlotPanel);
    }

    private void setupTabPanel(){
        mainTabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                int selected = event.getSelectedItem();
                switch (selected) {
                    case 0: onSummaryTabSelected();
                            break;
                    case 1: onTrendsTabSelected();
                            break;
                    case 2: onMetricsTabSelected();
                    default:
                }
            }
        });
    }

    private void onSummaryTabSelected() {
        testsMetricsPanel.showWidget(0);
        if (summaryPanel.getSessionComparisonPanel() != null && hasChangedGrid) {
            if (chosenMetrics.getRecordList().isEmpty()) {
                summaryPanel.getSessionComparisonPanel().getGrid().setData(
                        summaryPanel.getSessionComparisonPanel().getEmptyListGrid()
                );
            } else {
                summaryPanel.getSessionComparisonPanel().getGrid().setData(
                        chosenMetrics.getRecordList()
                );
            }
            hasChangedGrid = false;
        }
    }

    private void onTrendsTabSelected() {
        testsMetricsPanel.showWidget(0);
        mainTabPanel.forceLayout();
        if (!chosenMetrics.getMetrics().isEmpty() && hasChanged) {
            plotTrendsPanel.clear();
            for(Map.Entry<String, MetricDto> entry : chosenMetrics.getMetrics().entrySet()) {
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
        testsMetricsPanel.showWidget(1);
    }

    private void chooseTab(String token) {
        if (NameTokens.SUMMARY.equals(token)) {
            mainTabPanel.selectTab(0);
        } else if (NameTokens.TRENDS.equals(token)) {
            mainTabPanel.selectTab(1);
        } else {
            mainTabPanel.selectTab(2);
        }
    }

    private void setupSessionDataGrid() {
        sessionsDataGrid = new DataGrid<SessionDataDto>();
        sessionsDataGrid.setPageSize(15);
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

        sessionsDataGrid.addColumn(new TextColumn<SessionDataDto>() {
            @Override
            public String getValue(SessionDataDto object) {
                return object.getName();
            }
        }, "Name");

        sessionsDataGrid.addColumn(new TextColumn<SessionDataDto>() {
            @Override
            public String getValue(SessionDataDto object) {
                return object.getStartDate();
            }
        }, "Start Date");

        sessionsDataGrid.addColumn(new TextColumn<SessionDataDto>() {
            @Override
            public String getValue(SessionDataDto object) {
                return object.getEndDate();
            }
        }, "End Date");

        sessionDataProvider.addDataDisplay(sessionsDataGrid);
    }

    private void setupTestDataGrid(){
        testDataGrid = new CellTable<TaskDataDto>();
        testDataGrid.setWidth("500px");
        testDataGrid.setEmptyTableWidget(new Label("No Tests"));

        // Add a selection model so we can select cells.
        final SelectionModel<TaskDataDto> selectionModel = new MultiSelectionModel<TaskDataDto>(new ProvidesKey<TaskDataDto>() {
            @Override
            public Object getKey(TaskDataDto item) {
                return item.getTaskName();
            }
        });
        testDataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<TaskDataDto>createCheckboxManager());

        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable mouse selection.
        Column<TaskDataDto, Boolean> checkColumn =
                new Column<TaskDataDto, Boolean>(new CheckboxCell(true, false)) {
                    @Override
                    public Boolean getValue(TaskDataDto object) {
                        // Get the value from the selection model.
                        return selectionModel.isSelected(object);
                    }
                };
        testDataGrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        testDataGrid.setColumnWidth(checkColumn, 40, Style.Unit.PX);

        testDataGrid.addColumn(new TextColumn<TaskDataDto>() {
            @Override
            public String getValue(TaskDataDto object) {
                return object.getTaskName();
            }
        }, "Tests");
        testDataGrid.setRowData(Collections.EMPTY_LIST);
    }

    private void setupPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        sessionsPager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        sessionsPager.setDisplay(sessionsDataGrid);
    }

    private void setupTestDetailsTree() {
        CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
        final MultiSelectionModel<PlotNameDto> selectionModel = new MultiSelectionModel<PlotNameDto>();
        taskDetailsTree = new CellTree(new TaskDataTreeViewModel(selectionModel, getResources()), null, res);
        taskDetailsTree.addStyleName(getResources().css().taskDetailsTree());
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
                if (currentContent == null || currentContent.isEmpty()) {
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
                sessionDataForSessionIdsAsyncProvider.addDataDisplayIfNotExists(sessionsDataGrid);
            }
        };

        sessionIdsTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                sessionsFrom.setValue(null);
                sessionsTo.setValue(null);
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
                sessionIdsTextBox.setValue(null);
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
                sessionDataForDatePeriodAsyncProvider.addDataDisplayIfNotExists(sessionsDataGrid);
            }
        };

        sessionsTo.addValueChangeHandler(valueChangeHandler);
        sessionsFrom.addValueChangeHandler(valueChangeHandler);
    }

    private boolean isMaxPlotCountReached() {
        return plotPanel.getWidgetCount() >= MAX_PLOT_COUNT;
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

            final SimplePlot plot = createPlot(id, markings, plotSeriesDto.getXAxisLabel(), yMinimum, isMetric);
            redrawingPlot = plot;
            PlotModel plotModel = plot.getModel();

            for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                SeriesHandler handler = plotModel.addSeries(plotDatasetDto.getLegend(), plotDatasetDto.getColor());

                // Populate plot with data
                for (PointDto pointDto : plotDatasetDto.getPlotData()) {
                    handler.add(new DataPoint(pointDto.getX(), pointDto.getY()));
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

    //=================================//
    //==========Nested Classes=========//
    //=================================//

    /**
     * Handles select session event
     */
    private class TestSelectChangeHandler implements SelectionChangeEvent.Handler{

        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            Set<TaskDataDto> selected = ((MultiSelectionModel<TaskDataDto>) event.getSource()).getSelectedSet();
            Set<SessionDataDto> selectedSessions =  ((MultiSelectionModel<SessionDataDto>)sessionsDataGrid.getSelectionModel()).getSelectedSet();
            List<TaskDataDto> result = new ArrayList<TaskDataDto>(selected.size());
            result.addAll(selected);
            TaskDataTreeViewModel taskDataTreeViewModel = (TaskDataTreeViewModel) taskDetailsTree.getTreeViewModel();
            MultiSelectionModel<PlotNameDto> plotNameSelectionModel = taskDataTreeViewModel.getSelectionModel();

            // Clear plots display
            plotPanel.clear();
            plotTrendsPanel.clear();
            chosenMetrics.clear();
            // Clear task scope plot selection model
            plotNameSelectionModel.clear();
            // Clear session scope plot list
            sessionPlotPanel.clearPlots();

            // Clear markings dto map
            markingsMap.clear();
            taskDataTreeViewModel.clear();

            taskDataTreeViewModel.populateTaskList(result);
            // Populate available plots tree level for each task for selected session
            for (TaskDataDto taskDataDto : result) {
                taskDataTreeViewModel.getPlotNameDataProviders().put
                        (taskDataDto, new TaskPlotNamesAsyncDataProvider(taskDataDto, summaryPanel.getSessionIds()));
            }

            summaryPanel.updateTests(selected);
            metricPanel.updateTests(selected);

            if (!selectTests){
                selectTests = true;

                Set<MetricNameDto> metricsToSelect = new HashSet<MetricNameDto>();
                Set<PlotNameDto> trendsToSelect = new HashSet<PlotNameDto>();

                for (TaskDataDto taskDataDto : result){
                    for (TestsMetrics testMetric : place.getSelectedTestsMetrics()){
                        if (testMetric.getTestName().equals(taskDataDto.getTaskName())){
                            //add metrics
                            for (String metricName : testMetric.getMetrics()){
                                MetricNameDto meticDto = new MetricNameDto();
                                meticDto.setName(metricName);
                                meticDto.setTests(taskDataDto);
                                metricsToSelect.add(meticDto);
                            }

                            //add plots
                            for (String trendsName : testMetric.getTrends()){
                                PlotNameDto plotNameDto = new PlotNameDto(taskDataDto, trendsName);
                                trendsToSelect.add(plotNameDto);
                            }
                        }
                    }
                }

                MetricNameDto fireMetric = null;
                if (!metricsToSelect.isEmpty())
                    fireMetric = metricsToSelect.iterator().next();

                for (MetricNameDto metric : metricsToSelect){
                    metricPanel.setSelected(metric);
                }
                metricPanel.addSelectionListener(new MetricsSelectionChangedHandler());

                if (fireMetric != null)
                    metricPanel.setSelected(fireMetric);


                PlotNameDto firePlot = null;
                if (!trendsToSelect.isEmpty())
                    firePlot = trendsToSelect.iterator().next();

                for (PlotNameDto plotNameDto : trendsToSelect){
                    taskDataTreeViewModel.getSelectionModel().setSelected(plotNameDto, true);
                }
                taskDataTreeViewModel.getSelectionModel().addSelectionChangeHandler(new TaskPlotSelectionChangedHandler());

                if (firePlot != null)
                    taskDataTreeViewModel.getSelectionModel().setSelected(firePlot, true);

            }else{
                if (selectedSessions.size() == 1){
                    final SessionDataDto session = selectedSessions.iterator().next();
                    PlotProviderService.Async.getInstance().getSessionScopePlotList(session.getSessionId(),new AsyncCallback<Set<String>>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            throwable.printStackTrace();
                        }

                        @Override
                        public void onSuccess(Set<String> strings) {
                            sessionPlotPanel.update(session.getSessionId(), strings);
                        }
                    });
                }
            }
        }
    }

    private class SessionSelectChangeHandler implements SelectionChangeEvent.Handler {

        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            // Currently selection model for sessions is a single selection model
            Set<SessionDataDto> selected = ((MultiSelectionModel<SessionDataDto>) event.getSource()).getSelectedSet();

            TaskDataTreeViewModel taskDataTreeViewModel = (TaskDataTreeViewModel) taskDetailsTree.getTreeViewModel();
            MultiSelectionModel<PlotNameDto> plotNameSelectionModel = taskDataTreeViewModel.getSelectionModel();

            //Refresh summary
            summaryPanel.updateSessions(selected);
            // Clear plots display
            plotPanel.clear();
            plotTrendsPanel.clear();
            chosenMetrics.clear();
            // Clear task scope plot selection model
            plotNameSelectionModel.clear();
            //clearPlots session plots
            sessionPlotPanel.clearPlots();
            // Clear markings dto map
            markingsMap.clear();
            taskDataTreeViewModel.clear();
            metricPanel.updateTests(Collections.EMPTY_SET);
            testDataGrid.setRowData(Collections.EMPTY_LIST);

            if (selected.size() == 1) {
                // If selected single session clearPlots plot display, clearPlots plot selection and fetch all data for given session

                final String sessionId = selected.iterator().next().getSessionId();

                        // Populate task scope session list
                        TaskDataService.Async.getInstance().getTaskDataForSession(sessionId, new AsyncCallback<List<TaskDataDto>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                caught.printStackTrace();
                            }

                            @Override
                            public void onSuccess(List<TaskDataDto> result) {
                                updateTests(result);
                            }
                        });
            } else if (selected.size() > 1) {
                // If selected several sessions

                chosenSessions.clear();
                final Set<String> sessionIds = new HashSet<String>();
                for (SessionDataDto sessionDataDto : selected) {
                    sessionIds.add(sessionDataDto.getSessionId());
                    chosenSessions.add(Long.valueOf(sessionDataDto.getSessionId()));
                    Collections.sort(chosenSessions);
                }

                TaskDataService.Async.getInstance().getTaskDataForSessions(sessionIds, new AsyncCallback<List<TaskDataDto>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(List<TaskDataDto> result) {
                        updateTests(result);
                    }
                });
            }
        }
    }

    private void updateTests(List<TaskDataDto> tests){
        Set<SessionDataDto> selected = ((MultiSelectionModel<SessionDataDto>) sessionsDataGrid.getSelectionModel()).getSelectedSet();
        if (selected.size() == 1){
            final String sessionId = selected.iterator().next().getSessionId();
            if (!selectTests){
                PlotProviderService.Async.getInstance().getSessionScopePlotList(sessionId,
                        new AsyncCallback<Set<String>>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                throwable.printStackTrace();
                            }

                            @Override
                            public void onSuccess(Set<String> strings) {
                                sessionPlotPanel.update(sessionId, strings);
                                sessionPlotPanel.setSelected(place.getSessionTrends());
                            }
                        });
            }else{
                PlotProviderService.Async.getInstance().getSessionScopePlotList(sessionId,new AsyncCallback<Set<String>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Set<String> strings) {
                        sessionPlotPanel.update(sessionId, strings);
                    }
                });
            }
        }

        if (tests.isEmpty()) {
            return;
        }

        MultiSelectionModel model = (MultiSelectionModel)testDataGrid.getSelectionModel();
        model.clear();

        testDataGrid.redraw();
        testDataGrid.setRowData(tests);

        if (!selectTests){
            TaskDataDto selectObject = null;
            Set<TestsMetrics> testsMetrics = place.getSelectedTestsMetrics();
            for (TaskDataDto taskDataDto : tests){
                for (TestsMetrics testMetric : testsMetrics){
                    if (testMetric.getTestName().equals(taskDataDto.getTaskName())){
                        if (selectObject == null) selectObject = taskDataDto;
                        model.setSelected(taskDataDto, true);
                    }
                }
            }
            model.addSelectionChangeHandler(new TestSelectChangeHandler());

            //fire event
            if (selectObject != null){
                model.setSelected(selectObject, true);
            }else{
                //nothing to select
                selectTests = true;
            }
        }
    }

    /**
     * Handles metrics change
     */
    private class MetricsSelectionChangedHandler extends PlotsServingBase implements SelectionChangeEvent.Handler {

        @Override
        public void onSelectionChange(SelectionChangeEvent event) {

            hasChanged = true;
            hasChangedGrid = true;
            if (summaryPanel.getSessionComparisonPanel() == null) {
                plotTrendsPanel.clear();
                chosenMetrics.clear();
                return;
            }

            Set<MetricNameDto> metrics = metricPanel.getSelected();
            final ListGridRecord[] emptyData = summaryPanel.getSessionComparisonPanel().getEmptyListGrid();

            if (metrics.isEmpty()) {
                // Remove plots from display which were unchecked
                chosenMetrics.clear();
                plotTrendsPanel.clear();
                if (mainTabPanel.getSelectedIndex() == 0) {
                    onSummaryTabSelected();
                }
            } else {

                //Generate all id of plots which should be displayed
                Set<String> selectedMetricsIds = new HashSet<String>();
                for (MetricNameDto plotNameDto : metrics) {
                    selectedMetricsIds.add(generateMetricPlotId(plotNameDto));
                }

                // Remove plots from display which were unchecked
                for (int i = 0; i < plotTrendsPanel.getWidgetCount(); i++) {
                    Widget widget = plotTrendsPanel.getWidget(i);
                    String widgetId = widget.getElement().getId();
                    if (!isMetricPlotId(widgetId) || selectedMetricsIds.contains(widgetId)) {
                        continue;
                    }
                    // Remove plot
                    plotTrendsPanel.remove(i);
                    chosenMetrics.getMetrics().remove(widgetId);
                }

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

                MetricDataService.Async.getInstance().getMetrics(notLoaded, new AsyncCallback<List<MetricDto>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(List<MetricDto> result) {
                        loaded.addAll(result);
                        MetricRankingProvider.sortMetrics(loaded);
                        RecordList recordList = new RecordList();
                        recordList.addList(emptyData);
                        for (MetricDto metric : loaded){
                            summaryPanel.getCachedMetrics().put(metric.getMetricName(), metric);
                            recordList.add(summaryPanel.getSessionComparisonPanel().generateRecord(metric));
                        }
                        chosenMetrics.setRecordList(recordList);
                        if (mainTabPanel.getSelectedIndex() == 0) {
                            onSummaryTabSelected();
                        }
                        renderMetricPlots(loaded);
                    }
                });
            }
        }

        private void renderMetricPlots(List<MetricDto> result) {
            for (MetricDto metric : result) {

                if (isMaxPlotCountReached()) {
                    Window.alert("You are reached max count of plot on display");
                    break;
                }
                // Generate DOM id for plot
                final String id = generateMetricPlotId(metric.getMetricName());

                if (!chosenMetrics.getMetrics().containsKey(id)) {
                    chosenMetrics.getMetrics().put(id, metric);
                }
            }
            if (mainTabPanel.getSelectedIndex() == 1) {
                onTrendsTabSelected();
            }
        }
    }

    /**
     * Handles specific plot of task selection
     */
    private class TaskPlotSelectionChangedHandler extends PlotsServingBase implements SelectionChangeEvent.Handler {
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {

            Set<PlotNameDto> selected = ((MultiSelectionModel<PlotNameDto>) event.getSource()).getSelectedSet();
            final Set<SessionDataDto> selectedSessions = ((MultiSelectionModel<SessionDataDto>) sessionsDataGrid.getSelectionModel()).getSelectedSet();

            if (selected.isEmpty()) {
                // Remove plots from display which were unchecked
                for (int i = 0; i < plotPanel.getWidgetCount(); i++) {
                    Widget widget = plotPanel.getWidget(i);
                    String widgetId = widget.getElement().getId();
                    if (isTaskScopePlotId(widgetId) || isCrossSessionsTaskScopePlotId(widgetId)) {
                        plotPanel.remove(i);
                        break;
                    }
                }
            } else if (selectedSessions.size() == 1) {
                // Generate all id of plots which should be displayed
                Set<String> selectedTaskIds = new HashSet<String>();
                for (PlotNameDto plotNameDto : selected) {
                    selectedTaskIds.add(generateTaskScopePlotId(plotNameDto));
                }

                // Remove plots from display which were unchecked
                for (int i = 0; i < plotPanel.getWidgetCount(); i++) {
                    Widget widget = plotPanel.getWidget(i);
                    String widgetId = widget.getElement().getId();
                    if (!isTaskScopePlotId(widgetId) || selectedTaskIds.contains(widgetId)) {
                        continue;
                    }
                    // Remove plot
                    plotPanel.remove(i);
                }

                PlotProviderService.Async.getInstance().getPlotDatas(selected, new AsyncCallback<Map<PlotNameDto, List<PlotSeriesDto>>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Map<PlotNameDto, List<PlotSeriesDto>> result) {
                        for (PlotNameDto plotNameDto : result.keySet()){
                            if (isMaxPlotCountReached()) {
                                Window.alert("You are reached max count of plot on display");
                                break;
                            }

                            // Generate DOM id for plot
                            final String id = generateTaskScopePlotId(plotNameDto);

                            // If plot has already displayed, then pass it
                            if (plotPanel.getElementById(id) != null) {
                                continue;
                            }

                            renderPlots(plotPanel, result.get(plotNameDto), id);
                            scrollPanelMetrics.scrollToBottom();
                        }
                    }
                });
            } else {
                // Generate all id of plots which should be displayed
                Set<String> selectedTaskIds = new HashSet<String>();
                for (PlotNameDto plotNameDto : selected) {
                    selectedTaskIds.add(generateCrossSessionsTaskScopePlotId(plotNameDto));
                }

                // Remove plots from display which were unchecked
                for (int i = 0; i < plotPanel.getWidgetCount(); i++) {
                    Widget widget = plotPanel.getWidget(i);
                    String widgetId = widget.getElement().getId();
                    if (!isCrossSessionsTaskScopePlotId(widgetId) || selectedTaskIds.contains(widgetId)) {
                        continue;
                    }
                    // Remove plot
                    plotPanel.remove(i);
                }

                // Creating plots and displaying theirs
                PlotProviderService.Async.getInstance().getPlotDatas(selected, new AsyncCallback<Map<PlotNameDto,List<PlotSeriesDto>>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Map<PlotNameDto, List<PlotSeriesDto>> result) {
                        for (PlotNameDto plotNameDto : result.keySet()){
                            if (isMaxPlotCountReached()) {
                                Window.alert("You are reached max count of plot on display");
                                break;
                            }

                            // Generate DOM id for plot
                            final String id = generateCrossSessionsTaskScopePlotId(plotNameDto);

                            // If plot has already displayed, then pass it
                            if (plotPanel.getElementById(id) != null) {
                                continue;
                            }

                            renderPlots(plotPanel, result.get(plotNameDto), id);
                            scrollPanelMetrics.scrollToBottom();
                        }
                    }
                });
            }
        }
    }
    /**
     * Handles clicks on session scope plot checkboxes
     */
    private class SessionScopePlotCheckBoxClickHandler extends PlotsServingBase implements ValueChangeHandler<Boolean> {
        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            final CheckBox source = (CheckBox) event.getSource();
            final String sessionId = extractEntityIdFromDomId(source.getElement().getId());
            final String plotName = source.getText();
            final String id = generateSessionScopePlotId(sessionId, plotName);
            // If checkbox is checked
            if (source.getValue()) {
                plotPanel.add(loadIndicator);
                scrollPanelMetrics.scrollToBottom();
                final int loadingId = plotPanel.getWidgetCount() - 1;
                PlotProviderService.Async.getInstance().getSessionScopePlotData(sessionId, plotName, new AsyncCallback<List<PlotSeriesDto>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        plotPanel.remove(loadingId);
                        Window.alert("Error is occurred during server request processing (Session scope plot data fetching for " + plotName + ")");
                    }

                    @Override
                    public void onSuccess(List<PlotSeriesDto> result) {
                        plotPanel.remove(loadingId);
                        if (result.isEmpty()) {
                            Window.alert("There are no data found for " + plotName);
                        }

                        renderPlots(plotPanel, result, id);
                        scrollPanelMetrics.scrollToBottom();
                    }
                });
            } else {
                // Remove plots from display which were unchecked
                for (int i = 0; i < plotPanel.getWidgetCount(); i++) {
                    Widget widget = plotPanel.getWidget(i);
                    if (id.equals(widget.getElement().getId())) {
                        // Remove plot
                        plotPanel.remove(i);
                        break;
                    }
                }
            }
        }
    }

}
