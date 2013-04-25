package com.griddynamics.jagger.webclient.client.trends;

import ca.nanometrics.gflot.client.*;
import ca.nanometrics.gflot.client.options.*;
import ca.nanometrics.gflot.client.options.Range;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.*;
import com.griddynamics.jagger.webclient.client.*;
import com.griddynamics.jagger.webclient.client.callback.SessionScopePlotListQueryCallback;
import com.griddynamics.jagger.webclient.client.callback.TaskDataDtoListQueryAsyncCallback;
import com.griddynamics.jagger.webclient.client.components.SessionPanel;
import com.griddynamics.jagger.webclient.client.components.SummaryPanel;
import com.griddynamics.jagger.webclient.client.data.*;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.client.handler.ShowCurrentValueHoverListener;
import com.griddynamics.jagger.webclient.client.handler.ShowTaskDetailsListener;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

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
    HTMLPanel plotPanel;

    @UiField(provided = true)
    DataGrid<SessionDataDto> sessionsDataGrid;

    @UiField(provided = true)
    SimplePager sessionsPager;

    @UiField(provided = true)
    CellTree taskDetailsTree;

    @UiField
    ScrollPanel scrollPanelTrends;

    @UiField
    ScrollPanel scrollPanelSummary;

    @UiField
    SummaryPanel summaryPanel;

    @UiField
    VerticalPanel sessionScopePlotList;

    @UiField
    TextBox sessionIdsTextBox;

    private Timer stopTypingSessionIdsTimer;

    @UiField
    DateBox sessionsFrom;

    @UiField
    DateBox sessionsTo;

    @UiHandler("uncheckSessionsButton")
    void handleUncheckSessionsButtonClick(ClickEvent e) {
        ((MultiSelectionModel<?>) sessionsDataGrid.getSelectionModel()).clear();
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

    public void setSessionIds(Set<String> sessionIds) {
        MultiSelectionModel<SessionDataDto> selectionModel = (MultiSelectionModel<SessionDataDto>) sessionsDataGrid.getSelectionModel();
        selectionModel.clear();

        if (sessionIds == null || sessionsDataGrid == null) {
            return;
        }

        Set<SessionDataDto> sessionDataDtoSet = new HashSet<SessionDataDto>();
        for (String sessionId : sessionIds) {
            SessionDataDto sessionDataDto = new SessionDataDto(sessionId);
            sessionDataDtoSet.add(sessionDataDto);
            selectionModel.setSelected(sessionDataDto, true);
        }
        filterSessions(sessionDataDtoSet);
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
        setupTaskDetailsTree();
        setupDataGrid();
        setupPager();
        setupLoadIndicator();

        uiBinder.createAndBindUi(this);

        setupSessionNumberTextBox();
        setupSessionsDateRange();
    }

    private SimplePlot createPlot(final String id, Markings markings) {
        PlotOptions plotOptions = new PlotOptions();
        plotOptions.setGlobalSeriesOptions(new GlobalSeriesOptions()
                .setLineSeriesOptions(new LineSeriesOptions().setLineWidth(1).setShow(true).setFill(0.1))
                .setPointsOptions(new PointsSeriesOptions().setRadius(1).setShow(true)).setShadowSize(0d));

        plotOptions.setPanOptions(new PanOptions().setInteractive(true));

        plotOptions.addXAxisOptions(new AxisOptions().setZoomRange(true));
        plotOptions.addYAxisOptions(new AxisOptions().setZoomRange(false));

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
        popup.setWidth("50px");
        popup.addStyleName(getResources().css().infoPanel());
        final HTML popupPanelContent = new HTML();
        popup.add(popupPanelContent);

        // add hover listener
        plot.addHoverListener(new ShowCurrentValueHoverListener(popup, 50, popupPanelContent), false);

        if (markings != null && markingsMap != null && !markingsMap.isEmpty()) {
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

    private void setupDataGrid() {
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

        selectionModel.addSelectionChangeHandler(new SessionSelectChangeHandler(new SessionScopePlotCheckBoxClickHandler()));

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

    private void setupPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        sessionsPager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        sessionsPager.setDisplay(sessionsDataGrid);
    }

    private void setupTaskDetailsTree() {
        CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
        final MultiSelectionModel<PlotNameDto> selectionModel = new MultiSelectionModel<PlotNameDto>();
        taskDetailsTree = new CellTree(new TaskDataTreeViewModel(selectionModel, getResources()), null, res);
        taskDetailsTree.addStyleName(getResources().css().taskDetailsTree());

        selectionModel.addSelectionChangeHandler(new TaskPlotSelectionChangedHandler());
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

    private void renderPlots(List<PlotSeriesDto> plotSeriesDtoList, String id) {
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

            final SimplePlot plot = createPlot(id, markings);
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

        plotPanel.add(plotGroupPanel);

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
    private class SessionSelectChangeHandler implements SelectionChangeEvent.Handler {
        private final ClickHandler sessionScopePlotCheckBoxClickHandler;

        private SessionSelectChangeHandler(ClickHandler sessionScopePlotCheckBoxClickHandler) {
            this.sessionScopePlotCheckBoxClickHandler = sessionScopePlotCheckBoxClickHandler;
        }

        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            // Currently selection model for sessions is a single selection model
            final Set<SessionDataDto> selected = ((MultiSelectionModel<SessionDataDto>) event.getSource()).getSelectedSet();

            final TaskDataTreeViewModel taskDataTreeViewModel = (TaskDataTreeViewModel) taskDetailsTree.getTreeViewModel();
            final MultiSelectionModel<PlotNameDto> plotNameSelectionModel = taskDataTreeViewModel.getSelectionModel();

            //Refresh summary
            summaryPanel.update(selected);

            // Clear plots display
            plotPanel.clear();
            // Clear task scope plot selection model
            plotNameSelectionModel.clear();
            // Clear session scope plot list
            sessionScopePlotList.clear();
            // Clear markings dto map
            markingsMap.clear();
            taskDataTreeViewModel.clear();

            if (selected.size() == 1) {
                // If selected single session clear plot display, clear plot selection and fetch all data for given session

                final String sessionId = selected.iterator().next().getSessionId();

                // Populate session scope plot list
                PlotProviderService.Async.getInstance().getSessionScopePlotList(sessionId,
                        new SessionScopePlotListQueryCallback(sessionId, sessionScopePlotList, plotPanel, sessionScopePlotCheckBoxClickHandler, getResources()));

                final Set<String> sessionIds = new HashSet<String>(Arrays.asList(sessionId));

                // Populate task scope session list
                TaskDataService.Async.getInstance().getTaskDataForSession(sessionId,
                        new TaskDataDtoListQueryAsyncCallback(sessionIds, taskDataTreeViewModel));
            } else if (selected.size() > 1) {
                // If selected several sessions

                final Set<String> sessionIds = new HashSet<String>();
                for (SessionDataDto sessionDataDto : selected) {
                    sessionIds.add(sessionDataDto.getSessionId());
                }

                TaskDataService.Async.getInstance().getTaskDataForSessions(sessionIds,
                        new TaskDataDtoListQueryAsyncCallback(sessionIds, taskDataTreeViewModel));
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

                // Creating plots and displaying theirs
                for (final PlotNameDto plotNameDto : selected) {
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

                    plotPanel.add(loadIndicator);
                    scrollPanelTrends.scrollToBottom();
                    final int loadingId = plotPanel.getWidgetCount() - 1;
                    // Invoke remote service for plot data retrieving
                    PlotProviderService.Async.getInstance().getPlotData(plotNameDto.getTaskId(), plotNameDto.getPlotName(), new AsyncCallback<List<PlotSeriesDto>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            plotPanel.remove(loadingId);

                            Window.alert("Error is occurred during server request processing (" + plotNameDto.getPlotName() + " data fetching)");
                        }

                        @Override
                        public void onSuccess(List<PlotSeriesDto> result) {
                            plotPanel.remove(loadingId);

                            if (result.isEmpty()) {
                                Window.alert("There are no data found for " + plotNameDto.getPlotName());
                            }

                            renderPlots(result, id);
                        }
                    });
                }
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
                for (final PlotNameDto plotNameDto : selected) {
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

                    plotPanel.add(loadIndicator);
                    scrollPanelTrends.scrollToBottom();
                    final int loadingId = plotPanel.getWidgetCount() - 1;

                    // Invoke remote service for plot data retrieving
                    PlotProviderService.Async.getInstance().getPlotData(plotNameDto.getTaskIds(), plotNameDto.getPlotName(), new AsyncCallback<List<PlotSeriesDto>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            plotPanel.remove(loadingId);

                            Window.alert("Error is occurred during server request processing (" + plotNameDto.getPlotName() + " data fetching)");
                        }

                        @Override
                        public void onSuccess(List<PlotSeriesDto> result) {
                            plotPanel.remove(loadingId);

                            if (result.isEmpty()) {
                                Window.alert("There are no data found for " + plotNameDto.getPlotName());
                            }

                            renderPlots(result, id);
                        }
                    });
                }
            }
        }
    }

    /**
     * Handles clicks on session scope plot checkboxes
     */
    private class SessionScopePlotCheckBoxClickHandler extends PlotsServingBase implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            final CheckBox source = (CheckBox) event.getSource();
            final String sessionId = extractEntityIdFromDomId(source.getElement().getId());
            final String plotName = source.getText();
            final String id = generateSessionScopePlotId(sessionId, plotName);
            // If checkbox is checked
            if (source.getValue()) {
                plotPanel.add(loadIndicator);
                scrollPanelTrends.scrollToBottom();
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

                        renderPlots(result, id);
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
