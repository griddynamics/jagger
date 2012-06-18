package com.griddynamics.jagger.webclient.client;

import ca.nanometrics.gflot.client.*;
import ca.nanometrics.gflot.client.event.PlotHoverListener;
import ca.nanometrics.gflot.client.event.PlotItem;
import ca.nanometrics.gflot.client.event.PlotPosition;
import ca.nanometrics.gflot.client.jsni.Plot;
import ca.nanometrics.gflot.client.options.*;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.*;
import com.griddynamics.jagger.webclient.client.data.EmptyDateBoxValueChangePropagator;
import com.griddynamics.jagger.webclient.client.data.SessionDataAsyncDataProvider;
import com.griddynamics.jagger.webclient.client.data.SessionDataForDatePeriodAsyncProvider;
import com.griddynamics.jagger.webclient.client.dto.*;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/28/12
 */
public class Trends extends Composite {
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
    ScrollPanel scrollPanel;

    @UiField
    VerticalPanel sessionScopePlotList;

    @UiField
    TextBox sessionNumberTextBox;

    @UiField
    DateBox sessionsFrom;

    @UiField
    DateBox sessionsTo;

    private FlowPanel loadIndicator;

    private SessionDataAsyncDataProvider sessionDataProvider = new SessionDataAsyncDataProvider();

    public Trends() {
        setupTaskDetailsTree();
        setupDataGrid();
        setupPager();
        setupLoadIndicator();
        initWidget(uiBinder.createAndBindUi(this));
        setupSessionNumberTextBox();
        setupSessionsDateRange();
    }

    private SimplePlot createPlot() {
        PlotOptions plotOptions = new PlotOptions();
        plotOptions.setGlobalSeriesOptions(new GlobalSeriesOptions()
                .setLineSeriesOptions(new LineSeriesOptions().setLineWidth(1).setShow(true).setFill(0.1))
                .setPointsOptions(new PointsSeriesOptions().setRadius(1).setShow(true)).setShadowSize(0d));

        plotOptions.setPanOptions(new PanOptions().setInteractive(true));

        plotOptions.addXAxisOptions(new AxisOptions().setZoomRange(true));
        plotOptions.addYAxisOptions(new AxisOptions().setZoomRange(true));

        plotOptions.setLegendOptions(new LegendOptions().setNumOfColumns(2));

        // Make the grid hoverable
        plotOptions.setGridOptions(new GridOptions().setHoverable(true));
        // create the plot
        SimplePlot plot = new SimplePlot(plotOptions);
        plot.setHeight(200);
        plot.setWidth("100%");

        final PopupPanel popup = new PopupPanel();
        final Label label = new Label();
        popup.add(label);

        // add hover listener
        plot.addHoverListener(new PlotHoverListener() {
            public void onPlotHover(Plot plot, PlotPosition position, PlotItem item) {
                if (item != null) {
                    String text = "x: " + item.getDataPoint().getX() + ", y: " + item.getDataPoint().getY();

                    label.setText(text);
                    popup.setPopupPosition(item.getPageX() + 10, item.getPageY() - 25);
                    popup.show();
                } else {
                    popup.hide();
                }
            }
        }, false);

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
        taskDetailsTree = new CellTree(new WorkloadTaskDetailsTreeViewModel(selectionModel), null, res);
        taskDetailsTree.addStyleName("task-details-tree");

        selectionModel.addSelectionChangeHandler(new TaskPlotSelectionChangedHandler());
    }

    private void setupLoadIndicator() {
        ImageResource imageResource = JaggerResources.INSTANCE.getLoadIndicator();
        Image image = new Image(imageResource);
        loadIndicator = new FlowPanel();
        loadIndicator.addStyleName("centered");
        loadIndicator.add(image);
    }

    private void setupSessionNumberTextBox() {
        final ListDataProvider<SessionDataDto> dataProvider = new ListDataProvider<SessionDataDto>();
        final Timer stopTypingTimer = new Timer() {
            @Override
            public void run() {
                try {
                    final String currentContent = sessionNumberTextBox.getText();

                    // If session ID text box is empty then load all sessions
                    if (currentContent == null || currentContent.isEmpty()) {
                        dataProvider.removeDataDisplay(sessionsDataGrid);
                        sessionDataProvider.addDataDisplay(sessionsDataGrid);

                        return;
                    }

                    // Session ID has String type in DB but actually it is positive integer
                    int num = Integer.parseInt(currentContent);
                    if (num < 0) {
                        throw new NumberFormatException();
                    }

                    SessionDataService.Async.getInstance().getBySessionId(currentContent, new AsyncCallback<SessionDataDto>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert("Error is occurred during server request processing (Session data fetching) for session ID " + currentContent + ": " + caught.getMessage());
                        }

                        @Override
                        public void onSuccess(SessionDataDto result) {
                            if (sessionDataProvider.getDataDisplays().contains(sessionsDataGrid)) {
                                sessionDataProvider.removeDataDisplay(sessionsDataGrid);
                            }

                            dataProvider.getList().clear();
                            dataProvider.getList().add(result);
                            if (!dataProvider.getDataDisplays().contains(sessionsDataGrid)) {
                                dataProvider.addDataDisplay(sessionsDataGrid);
                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    Window.alert("Session number must be a positive integer");
                }
            }
        };

        sessionNumberTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                stopTypingTimer.schedule(500);
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
                Date fromDate = sessionsFrom.getValue();
                Date toDate = sessionsTo.getValue();

                if (fromDate == null || toDate == null) {
                    if (!sessionDataProvider.getDataDisplays().contains(sessionsDataGrid)) {
                        sessionDataProvider.addDataDisplay(sessionsDataGrid);
                    }
                    return;
                }

                if (sessionDataProvider.getDataDisplays().contains(sessionsDataGrid)) {
                    sessionDataProvider.removeDataDisplay(sessionsDataGrid);
                }

                AsyncDataProvider<SessionDataDto> asyncDataForDatePeriodProvider = new SessionDataForDatePeriodAsyncProvider(fromDate, toDate);
                asyncDataForDatePeriodProvider.addDataDisplay(sessionsDataGrid);
            }
        };

        sessionsTo.addValueChangeHandler(valueChangeHandler);
        sessionsFrom.addValueChangeHandler(valueChangeHandler);
    }

    private String generateTaskScopePlotId(PlotNameDto plotNameDto) {
        return "" + plotNameDto.getTaskIds() + "#task-scope-plot-" + plotNameDto.getPlotName().toLowerCase().replaceAll("\\s+", "-");
    }

    private String generateSessionScopePlotId(String sessionId, String plotName) {
        return sessionId + "#session-scope-plot-" + plotName.toLowerCase().replaceAll("\\s+", "-");
    }

    private boolean isTaskScopePlotId(String domId) {
        return domId.contains("#task-scope-plot-");
    }

    private boolean isSessionScopePlotId(String domId) {
        return domId.contains("#session-scope-plot-");
    }

    private String extractEntityIdFromDomId(String plotId) {
        return plotId.substring(0, plotId.indexOf("#"));
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
            final SimplePlot plot = createPlot();
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
            xLabel.addStyleName("x-axis-label");

            Label plotHeader = new Label(plotSeriesDto.getPlotHeader());
            plotHeader.addStyleName("plot-header");

            Label plotLegend = new Label("PLOT LEGEND");
            plotLegend.addStyleName("plot-legend");

            VerticalPanel vp = new VerticalPanel();
            vp.setWidth("100%");

            Label zoomInLabel = new Label("Zoom In");
            zoomInLabel.addStyleName("zoom-label");
            zoomInLabel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    plot.zoom();
                }
            });

            Label zoomOutLabel = new Label("Zoom Out");
            zoomOutLabel.addStyleName("zoom-label");
            zoomOutLabel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    plot.zoomOut();
                }
            });

            FlowPanel zoomPanel = new FlowPanel();
            zoomPanel.addStyleName("zoom-panel");
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

            final WorkloadTaskDetailsTreeViewModel workloadTaskDetailsTreeViewModel = (WorkloadTaskDetailsTreeViewModel) taskDetailsTree.getTreeViewModel();
            final ListDataProvider<TaskDataDto> taskDataProvider = workloadTaskDetailsTreeViewModel.getTaskDataProvider();
            final MultiSelectionModel<PlotNameDto> plotNameSelectionModel = workloadTaskDetailsTreeViewModel.getSelectionModel();

            if (selected.isEmpty()) {
                // If no sessions are selected, clear plot display, clear task tree, clear plot selection model
                plotPanel.clear();
                taskDataProvider.getList().clear();
                taskDataProvider.getList().add(WorkloadTaskDetailsTreeViewModel.getNoTasksDummyNode());

                plotNameSelectionModel.clear();

                // Clear session scope plot list
                sessionScopePlotList.clear();
            } else if (selected.size() == 1) {
                // If selected single session clear plot display, clear plot selection and fetch all data for given session

                plotPanel.clear();
                plotNameSelectionModel.clear();

                final SessionDataDto selectedSession = selected.iterator().next();

                // Populate session scope plot list
                PlotProviderService.Async.getInstance().getSessionScopePlotList(selectedSession.getSessionId(), new AsyncCallback<Set<String>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error is occurred during server request processing (Session scope plot names for task fetching)");
                    }

                    @Override
                    public void onSuccess(Set<String> result) {
                        // Populate session scope available plots
                        sessionScopePlotList.clear();
                        for (String plotName : result) {
                            CheckBox checkBox = new CheckBox(plotName);

                            // If plot for this one is already rendered we check it
                            if (plotPanel.getElementById(generateSessionScopePlotId(selectedSession.getSessionId(), plotName)) != null) {
                                checkBox.setValue(true, false);
                            }
                            checkBox.getElement().setId(generateSessionScopePlotId(selectedSession.getSessionId(), plotName)+"_checkbox");
                            checkBox.addClickHandler(sessionScopePlotCheckBoxClickHandler);
                            sessionScopePlotList.add(checkBox);
                        }
                    }
                });

                // TODO Make lazy loading by task selected
                // Populate task scope session list
                TaskDataService.Async.getInstance().getTaskDataForSession(selectedSession.getSessionId(), new AsyncCallback<List<TaskDataDto>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error is occurred during server request processing (Task data fetching)");
                    }

                    @Override
                    public void onSuccess(List<TaskDataDto> result) {
                        // Populate task first level tree with server data
                        taskDataProvider.getList().clear();
                        if (result.isEmpty()) {
                            taskDataProvider.getList().add(WorkloadTaskDetailsTreeViewModel.getNoTasksDummyNode());
                            return;
                        } else {
                            taskDataProvider.getList().addAll(result);
                        }

                        // Populate available plots tree level for each task for selected session
                        for (TaskDataDto taskDataDto : result) {
                            final ListDataProvider<PlotNameDto> plotNameDataProvider = ((WorkloadTaskDetailsTreeViewModel)
                                    taskDetailsTree.getTreeViewModel()).getPlotNameDataProvider(taskDataDto);

                            PlotProviderService.Async.getInstance().getTaskScopePlotList(selectedSession.getSessionId(), taskDataDto.getId(), new AsyncCallback<Set<PlotNameDto>>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    Window.alert("Error is occurred during server request processing (Plot names for task fetching)");
                                }

                                @Override
                                public void onSuccess(Set<PlotNameDto> result) {
                                    plotNameDataProvider.getList().clear();
                                    plotNameDataProvider.getList().addAll(result);

                                    // Close all tree nodes when new session is selected
                                    int childCount = taskDetailsTree.getRootTreeNode().getChildCount();
                                    for (int i = 0; i < childCount; i++) {
                                        taskDetailsTree.getRootTreeNode().setChildOpen(i, false);
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                // If selected several sessions

                plotPanel.clear();
                plotNameSelectionModel.clear();
                sessionScopePlotList.clear();

                //TODO Refactor it
                final Set<String> sessionIds = new HashSet<String>();
                for (SessionDataDto sessionDataDto : selected) {
                    sessionIds.add(sessionDataDto.getSessionId());
                }

                TaskDataService.Async.getInstance().getTaskDataForSessions(sessionIds, new AsyncCallback<List<TaskDataDto>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error is occurred during server request processing (Task data fetching)");
                    }

                    @Override
                    public void onSuccess(List<TaskDataDto> result) {
                        // Populate task first level tree with server data
                        taskDataProvider.getList().clear();
                        if (result.isEmpty()) {
                            taskDataProvider.getList().add(WorkloadTaskDetailsTreeViewModel.getNoTasksDummyNode());
                            return;
                        } else {
                            taskDataProvider.getList().addAll(result);
                        }

                        // Populate available plots tree level for each task for selected session
                        for (TaskDataDto taskDataDto : result) {
                            final ListDataProvider<PlotNameDto> plotNameDataProvider = ((WorkloadTaskDetailsTreeViewModel)
                                    taskDetailsTree.getTreeViewModel()).getPlotNameDataProvider(taskDataDto);

                            PlotProviderService.Async.getInstance().getTaskScopePlotList(sessionIds, taskDataDto, new AsyncCallback<Set<PlotNameDto>>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    Window.alert("Error is occurred during server request processing (Plot names for task fetching)");
                                }

                                @Override
                                public void onSuccess(Set<PlotNameDto> result) {
                                    plotNameDataProvider.getList().clear();
                                    plotNameDataProvider.getList().addAll(result);

                                    // Close all tree nodes when new session is selected
                                    int childCount = taskDetailsTree.getRootTreeNode().getChildCount();
                                    for (int i = 0; i < childCount; i++) {
                                        taskDetailsTree.getRootTreeNode().setChildOpen(i, false);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    /**
     * Handles specific plot of task selection
     */
    private class TaskPlotSelectionChangedHandler implements SelectionChangeEvent.Handler {
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            Set<PlotNameDto> selected = ((MultiSelectionModel<PlotNameDto>) event.getSource()).getSelectedSet();
            final Set<SessionDataDto> selectedSessions = ((MultiSelectionModel<SessionDataDto>)sessionsDataGrid.getSelectionModel()).getSelectedSet();

            if (selected.isEmpty()) {
                // Clear display because of no checked plots
                // Remove plots from display which were unchecked
                for (int i = 0; i < plotPanel.getWidgetCount(); i++) {
                    Widget widget = plotPanel.getWidget(i);
                    String widgetId = widget.getElement().getId();
                    if (isTaskScopePlotId(widgetId)) {
                        // Remove plot
                        plotPanel.remove(i);
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
                    scrollPanel.scrollToBottom();
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
                //TODO Refactor it
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
                    scrollPanel.scrollToBottom();
                    final int loadingId = plotPanel.getWidgetCount() - 1;
                    // Invoke remote service for plot data retrieving
                    //TODO Implement plot rendering
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
    private class SessionScopePlotCheckBoxClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            final CheckBox source = (CheckBox) event.getSource();
            final String sessionId = extractEntityIdFromDomId(source.getElement().getId());
            final String plotName = source.getText();
            final String id = generateSessionScopePlotId(sessionId, plotName);
            // If checkbox is checked
            if (source.getValue()) {
                plotPanel.add(loadIndicator);
                scrollPanel.scrollToBottom();
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
