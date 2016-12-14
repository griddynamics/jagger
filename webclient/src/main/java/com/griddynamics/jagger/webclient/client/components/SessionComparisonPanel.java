package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.MetricRankingProvider;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.griddynamics.jagger.dbapi.dto.SummaryIntegratedDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 26.03.13
 * Time: 12:30
 * Panel that contains table of metrics in comparison mod (multiple session selected)
 */
public class SessionComparisonPanel extends VerticalPanel {

    private final String TEST_DESCRIPTION = "testDescription";
    private final String TEST_NAME = "testName";
    // property to render in Metric column
    private final String NAME = "name";
    @SuppressWarnings("all")
    private final String METRIC = "Metric";
    private final String SESSION_HEADER = "Session ";
    private final String SESSION_INFO_ID = "sessionInfo";
    @SuppressWarnings("all")
    private final String COMMENT = "Comment";
    @SuppressWarnings("all")
    private final String USER_COMMENT = "User Comment";
    @SuppressWarnings("all")
    private final String SESSION_TAGS = "Tags";
    @SuppressWarnings("all")
    private final int MIN_COLUMN_WIDTH = 200;
    @SuppressWarnings("all")
    private final String ONE_HUNDRED_PERCENTS = "100%";
    @SuppressWarnings("all")
    private final String START_DATE = "Start Date";
    @SuppressWarnings("all")
    private final String END_DATE = "End Date";
    @SuppressWarnings("all")
    private final String ACTIVE_KERNELS = "Active Kernels";
    @SuppressWarnings("all")
    private final String TASKS_EXECUTED = "Tasks Executed";
    @SuppressWarnings("all")
    private final String TASKS_FAILED = "Tasks Failed";
    private final String TEST_INFO = "Test Info";
    private final double METRIC_COLUMN_WIDTH_FACTOR = 1.5;

    private final UserCommentBox userCommentBox;
    private final TagBox tagBox;

    private Set<SessionDataDto> chosenSessions;

    private final String WHITE_SPACE_NORMAL = "white-space: normal";

    private TreeGrid<TreeItem> treeGrid;
    private TreeStore<TreeItem> treeStore = new TreeStore<TreeItem>(new ModelKeyProvider<TreeItem>() {
        @Override
        public String getKey(TreeItem item) {
            return String.valueOf(item.getKey());
        }
    });

    private HashMap<MetricNode, SummaryIntegratedDto> cache = new HashMap<MetricNode, SummaryIntegratedDto>();

    private WebClientProperties webClientProperties;

    private List<TagDto> allTags;

    private boolean allTagsLoadComplete = false;

    public HashMap<MetricNode, SummaryIntegratedDto> getCachedMetrics() {
        return cache;
    }

    private final DateTimeFormat dateFormatter;

    public SessionComparisonPanel(
            Set<SessionDataDto> chosenSessions,
            int width,
            WebClientProperties webClientProperties,
            DateTimeFormat dateFormatter) {

        this.dateFormatter = dateFormatter;
        setWidth(ONE_HUNDRED_PERCENTS);
        setHeight(ONE_HUNDRED_PERCENTS);
        this.chosenSessions = chosenSessions;
        this.webClientProperties = webClientProperties;
        init(this.chosenSessions, width);
        userCommentBox = new UserCommentBox(webClientProperties.getUserCommentMaxLength());
        userCommentBox.setTreeGrid(treeGrid);
        tagBox = new TagBox();
        tagBox.setTreeGrid(treeGrid);
        if (webClientProperties.isTagsStoreAvailable())
            allTags();
    }

    private void init(Set<SessionDataDto> chosenSessions, int width) {

        int colWidth = calculateWidth(chosenSessions.size(), width);


        treeStore.clear();
        List<ColumnConfig<TreeItem, ?>> columns = new ArrayList<ColumnConfig<TreeItem, ?>>();

        //sort sessions by number sessionId
        SortedSet<SessionDataDto> sortedSet = new TreeSet<SessionDataDto>(new Comparator<SessionDataDto>() {
            @Override
            public int compare(SessionDataDto o, SessionDataDto o2) {
                return (Long.parseLong(o.getSessionId()) - Long.parseLong(o2.getSessionId())) > 0 ? 1 : -1;
            }
        });
        sortedSet.addAll(chosenSessions);

        ColumnConfig<TreeItem, String> nameColumn =
                new ColumnConfig<TreeItem, String>(new MapValueProvider(NAME), (int) (colWidth * METRIC_COLUMN_WIDTH_FACTOR));
        nameColumn.setHeader(METRIC);
        nameColumn.setSortable(false);
        nameColumn.setMenuDisabled(true);
        columns.add(nameColumn);

        for (SessionDataDto session : sortedSet) {
            ColumnConfig<TreeItem, String> column = new ColumnConfig<TreeItem, String>(
                    new MapValueProvider(SESSION_HEADER + session.getSessionId())
            );
            column.setHeader(SESSION_HEADER + session.getSessionId());
            column.setWidth(colWidth);
            column.setSortable(false);
            column.setCell(new AbstractCell<String>() {
                @Override
                public void render(Context context, String value, SafeHtmlBuilder sb) {
                    if (value != null) {
                        sb.appendHtmlConstant(value);
                    }
                }
            });
            column.setMenuDisabled(true);

            column.setColumnTextStyle(new SafeStyles() {
                @Override
                public String asString() {
                    return WHITE_SPACE_NORMAL;
                }
            });

            columns.add(column);
        }


        ColumnModel<TreeItem> cm = new ColumnModel<TreeItem>(columns);


        treeGrid = new NoIconsTreeGrid(treeStore, cm, nameColumn);

        treeGrid.addBeforeCollapseHandler(new BeforeCollapseItemEvent.BeforeCollapseItemHandler<TreeItem>() {
            @Override
            public void onBeforeCollapse(BeforeCollapseItemEvent<TreeItem> event) {
                event.setCancelled(true);
            }
        });

        treeGrid.setAutoExpand(true);
        treeGrid.getView().setStripeRows(true);
        treeGrid.setMinColumnWidth(MIN_COLUMN_WIDTH);
        treeGrid.setAllowTextSelection(true);
        treeGrid.getView().setForceFit(true);

        treeStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<TreeItem>() {
            @Override
            public void onAdd(StoreAddEvent<TreeItem> event) {
                for (TreeItem item : event.getItems()) {
                    treeGrid.setExpanded(item, true);
                }
            }
        });

        if (webClientProperties.isUserCommentEditAvailable()) {
            treeGrid.addCellDoubleClickHandler(new CellDoubleClickEvent.CellDoubleClickHandler() {
                @Override
                public void onCellClick(CellDoubleClickEvent event) {
                    TreeItem item = treeGrid.findNode(treeGrid.getTreeView().getRow(event.getRowIndex())).getModel();
                    if (item.getKey().equals(USER_COMMENT) && event.getCellIndex() > 0) {
                        SessionDataDto currentSession = defineCurrentSession(event);
                        userCommentBox.popUp(currentSession,
                                item.get(SESSION_HEADER + currentSession.getSessionId()),
                                item
                        );
                    }

                }
            });
        }

        if (webClientProperties.isTagsAvailable()) {
            treeGrid.addCellDoubleClickHandler(new CellDoubleClickEvent.CellDoubleClickHandler() {
                @Override
                public void onCellClick(CellDoubleClickEvent event) {
                    TreeItem item = treeGrid.findNode(treeGrid.getTreeView().getRow(event.getRowIndex())).getModel();
                    if (item.getKey().equals(SESSION_TAGS) && event.getCellIndex() > 0) {
                        SessionDataDto currentSession = defineCurrentSession(event);
                        if (allTagsLoadComplete)
                            tagBox.popUpForEdit(currentSession,
                                    item, allTags);
                    }
                }
            });
        }


        add(treeGrid);
    }

    private SessionDataDto defineCurrentSession(CellDoubleClickEvent event) {
        String sessionId = treeGrid.getColumnModel().getColumn(event.getCellIndex()).getHeader().asString();
        String sessionData_id = sessionId.substring(sessionId.indexOf(' ') + 1);
        SessionDataDto currentSession = null;
        for (SessionDataDto sessionDataDto : SessionComparisonPanel.this.chosenSessions) {
            if (sessionDataDto.getSessionId().equals(sessionData_id)) {
                currentSession = sessionDataDto;
                break;
            }
        }
        return currentSession;
    }

    /**
     * calculates width for columns
     *
     * @param size  number of chosen sessions
     * @param width is offset width of parent container
     * @return width of Session * column
     */
    private int calculateWidth(int size, int width) {
        int colWidth = (int) (width / (size + METRIC_COLUMN_WIDTH_FACTOR));
        if (colWidth < MIN_COLUMN_WIDTH)
            colWidth = MIN_COLUMN_WIDTH;

        return colWidth;
    }

    public void addSessionInfo() {
        TreeItem sessionInfo = new TreeItem(SESSION_INFO_ID);
        sessionInfo.put(NAME, "Session Info");
        treeStore.insert(0, sessionInfo);

        String tagsStr = "";
        TreeItem itemActiveKernels = new TreeItem(ACTIVE_KERNELS);
        TreeItem itemTaskExecuted = new TreeItem(TASKS_EXECUTED);
        TreeItem itemTaskFailed = new TreeItem(TASKS_FAILED);
        TreeItem itemDateStart = new TreeItem(START_DATE);
        TreeItem itemDateEnd = new TreeItem(END_DATE);
        TreeItem itemComment = new TreeItem(COMMENT);
        TreeItem itemUserComment = new TreeItem(USER_COMMENT);
        TreeItem itemTags = new TreeItem(SESSION_TAGS);

        itemActiveKernels.put(NAME, ACTIVE_KERNELS);
        itemTaskExecuted.put(NAME, TASKS_EXECUTED);
        itemTaskFailed.put(NAME, TASKS_FAILED);
        itemDateStart.put(NAME, START_DATE);
        itemDateEnd.put(NAME, END_DATE);
        itemComment.put(NAME, COMMENT);
        if (webClientProperties.isUserCommentStoreAvailable())
            itemUserComment.put(NAME, USER_COMMENT);
        if (webClientProperties.isTagsStoreAvailable())
            itemTags.put(NAME, SESSION_TAGS);

        for (SessionDataDto session : chosenSessions) {
            itemActiveKernels.put(SESSION_HEADER + session.getSessionId(), session.getActiveKernelsCount() + "");
            itemTaskExecuted.put(SESSION_HEADER + session.getSessionId(), session.getTasksExecuted() + "");
            itemTaskFailed.put(SESSION_HEADER + session.getSessionId(), session.getTasksFailed() + "");
            itemDateStart.put(SESSION_HEADER + session.getSessionId(), dateFormatter.format(session.getStartDate()));
            itemDateEnd.put(SESSION_HEADER + session.getSessionId(), dateFormatter.format(session.getEndDate()));
            itemComment.put(SESSION_HEADER + session.getSessionId(), session.getComment());
            if (webClientProperties.isUserCommentStoreAvailable()) {
                String userComment = session.getUserComment() == null ? "" : session.getUserComment();
                itemUserComment.put(SESSION_HEADER + session.getSessionId(), userComment);
            }
            if (webClientProperties.isTagsStoreAvailable()) {
                for (int i = 0; i < session.getTags().size(); i++) {
                    if (i == session.getTags().size() - 1)
                        tagsStr += session.getTags().get(i).getName();
                    else
                        tagsStr += session.getTags().get(i).getName() + ", ";
                }
                itemTags.put(SESSION_HEADER + session.getSessionId(), tagsStr);
                tagsStr = "";
            }
        }
        treeStore.add(sessionInfo, itemComment);
        if (webClientProperties.isUserCommentStoreAvailable())
            treeStore.add(sessionInfo, itemUserComment);
        if (webClientProperties.isTagsStoreAvailable())
            treeStore.add(sessionInfo, itemTags);
        treeStore.add(sessionInfo, itemDateStart);
        treeStore.add(sessionInfo, itemDateEnd);

        treeStore.add(sessionInfo, itemActiveKernels);
        treeStore.add(sessionInfo, itemTaskExecuted);
        treeStore.add(sessionInfo, itemTaskFailed);

    }

    public void removeSessionInfo() {
        TreeItem sessionInfo = treeStore.findModelWithKey(SESSION_INFO_ID);
        if (sessionInfo != null)
            treeStore.remove(sessionInfo);
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


    // // to make columns fit 100% width if grid created not on Summary Tab
    public void refresh() {
        treeGrid.getView().refresh(true);
    }

    // clear everything but Session Information
    public void clearTreeStore() {

        for (TreeItem root : treeStore.getRootItems()) {
            if (root.getKey().equals(SESSION_INFO_ID)) {
                continue;
            }
            for (TreeItem test : treeStore.getChildren(root)) {
                for (TreeItem item : treeStore.getChildren(test)) {
                    if (TEST_INFO.equals(item.get(NAME))) {
                        continue;
                    }
                    removeWithParent(item);
                }
            }
        }
    }


    public void addMetricRecord(SummarySingleDto metricDto) {

        TreeItem record = new TreeItem(metricDto);
        addItemToStore(record, metricDto);
    }


    public void addMetricRecords(Map<MetricNode, SummaryIntegratedDto> loaded) {

        cache.putAll(loaded);

        List<SummarySingleDto> loadedSorted = new ArrayList<SummarySingleDto>();
        for (SummaryIntegratedDto summaryMetricDto : loaded.values()) {
            for (SummarySingleDto metricDto : summaryMetricDto.getSummarySingleDtoList()) {
                loadedSorted.add(metricDto);
            }
        }

        MetricRankingProvider.sortMetrics(loadedSorted);
        for (SummarySingleDto metricDto : loadedSorted) {
            addMetricRecord(metricDto);
        }
    }

    private void addItemToStore(TreeItem record, SummarySingleDto metricDto) {

        TreeItem taskItem = getTestItem(metricDto.getMetricName().getTest());
        for (TreeItem rec : treeStore.getChildren(taskItem)) {
            if (rec.getKey().equals(record.getKey())) {
                return;
            }
        }
        treeStore.add(taskItem, record);
    }


    public void removeRecords(List<SummaryIntegratedDto> list) {

        List<SummarySingleDto> list2 = new ArrayList<SummarySingleDto>(list.size());
        for (SummaryIntegratedDto smd : list) {
            list2.addAll(smd.getSummarySingleDtoList());
        }

        for (SummarySingleDto metric : list2) {
            removeRecord(metric);
        }
    }

    private void removeRecord(SummarySingleDto metric) {

        TreeItem testItem = getTestItem(metric.getMetricName().getTest());
        String key = testItem.getKey() + metric.getMetricName().getMetricName();

        for (TreeItem item : treeStore.getChildren(testItem)) {
            if (item.getKey().equals(key)) {
                removeWithParent(item);
                return;
            }
        }
    }

    private String getItemKey(MetricNameDto metricName) {
        return metricName.getTest().getDescription() + metricName.getTest().getTaskName() + metricName.getMetricName();
    }

    private void removeWithParent(TreeItem toRemove) {
        TreeItem parent = treeStore.getParent(toRemove);
        treeStore.remove(toRemove);
        if (parent != null && !treeStore.hasChildren(parent)) {
            removeWithParent(parent);
        }
    }


    public void addTestInfo(TaskDataDto test, Map<String, TestInfoDto> testInfoMap) {

        TreeItem testItem = getTestItem(test);

        String testInfoId = testItem.getKey() + TEST_INFO;
        if (treeStore.findModelWithKey(testInfoId) != null) {
            return;
        }

        String testItemName = getTestItemName(test);
        TreeItem testInfo = new TreeItem(testInfoId);
        testInfo.put(NAME, TEST_INFO);
        testInfo.put(TEST_DESCRIPTION, test.getDescription());
        testInfo.put(TEST_NAME, testItemName);
        treeStore.insert(testItem, 0, testInfo);

        TreeItem clock = new TreeItem(testItem.getKey() + "Clock");
        clock.put(NAME, "Clock");
        clock.put(TEST_DESCRIPTION, test.getDescription());
        clock.put(TEST_NAME, testItemName);
        clock.put(TEST_INFO, TEST_INFO);
        for (SessionDataDto session : chosenSessions) {
            if (testInfoMap.get(session.getSessionId()) != null)
                clock.put(SESSION_HEADER + session.getSessionId(),
                        testInfoMap.get(session.getSessionId()).getClock() + " (" +
                        testInfoMap.get(session.getSessionId()).getClockValue().toString() + ")");
        }
        treeStore.add(testInfo, clock);

        TreeItem termination = new TreeItem(testItem.getKey() + "Termination");
        termination.put(NAME, "Termination");
        termination.put(TEST_DESCRIPTION, test.getDescription());
        termination.put(TEST_NAME, testItemName);
        termination.put(TEST_INFO, TEST_INFO);
        for (SessionDataDto session : chosenSessions) {
            if (testInfoMap.get(session.getSessionId()) != null)
                termination.put(SESSION_HEADER + session.getSessionId(), testInfoMap.get(session.getSessionId()).getTermination());
        }
        treeStore.add(testInfo, termination);

        TreeItem startTime = new TreeItem(testItem.getKey() + "Start time");
        startTime.put(NAME, "Start time");
        startTime.put(TEST_DESCRIPTION, test.getDescription());
        startTime.put(TEST_NAME, testItemName);
        startTime.put(TEST_INFO, TEST_INFO);
        for (SessionDataDto session : chosenSessions) {
            if (testInfoMap.get(session.getSessionId()) != null) {
                Date date = testInfoMap.get(session.getSessionId()).getStartTime();
                startTime.put(SESSION_HEADER + session.getSessionId(), dateFormatter.format(date));
            }
        }
        treeStore.add(testInfo, startTime);
    }

    public void removeTestInfo(TaskDataDto test) {

        String id = getTestItemId(test);
        TreeItem testItem = treeStore.findModelWithKey(id);
        if (testItem == null) {
            return;
        }
        TreeItem testInfo = treeStore.getFirstChild(testItem);
        if (testInfo != null && TEST_INFO.equals(testInfo.get(NAME)))
            removeWithParent(testInfo);
    }


    private TreeItem getTestItem(TaskDataDto tdd) {

        String key = getTestItemId(tdd);
        TreeItem taskItem = treeStore.findModelWithKey(key);
        if (taskItem != null) {
            return taskItem;
        }

        TreeItem description = getTestDescriptionItem(tdd.getDescription());

        taskItem = new TreeItem(key);
        taskItem.put(NAME, getTestItemName(tdd));
        taskItem.put(TEST_DESCRIPTION, tdd.getDescription());
        treeStore.add(description, taskItem);
        return taskItem;
    }

    private String getTestItemName(TaskDataDto tdd) {
        if (webClientProperties.isShowOnlyMatchedTests())
            return tdd.getTaskName();
        else
            return tdd.getTaskName() + " from sessions: " + tdd.getSessionIds();

    }

    private TreeItem getTestDescriptionItem(String descriptionStr) {
        for (TreeItem item : treeStore.getRootItems()) {
            if (descriptionStr.equals(item.get(NAME))) {
                return item;
            }
        }
        TreeItem description = new TreeItem(descriptionStr);
        description.put(NAME, descriptionStr);
        treeStore.add(description);
        return description;
    }


    private String getTestItemId(TaskDataDto tdd) {
        StringBuilder sessionIds = new StringBuilder();
        for (String id : tdd.getSessionIds()) {
            sessionIds.append(id);
        }
        return tdd.getDescription() + tdd.getTaskName() + sessionIds.toString();
    }

    private class NoIconsTreeGrid extends TreeGrid<TreeItem> {


        public NoIconsTreeGrid(TreeStore<TreeItem> store, ColumnModel<TreeItem> cm, ColumnConfig<TreeItem, ?> treeColumn) {
            super(store, cm, treeColumn);
        }

        @Override
        protected ImageResource calculateIconStyle(TreeItem model) {
            return null;
        }
    }

    public class TreeItem extends HashMap<String, String> {

        protected String key;

        private String getKey() {
            return key;
        }

        @SuppressWarnings("unused")
        public TreeItem() {}

        public TreeItem(String key) {
            this.key = key;
        }

        public TreeItem(SummarySingleDto metricDto) {

            MetricNameDto metricName = metricDto.getMetricName();
            this.key =   getTestItemId(metricDto.getMetricName().getTest()) + metricDto.getMetricName().getMetricName();
            put(NAME, metricName.getMetricDisplayName());
            put(TEST_DESCRIPTION, metricName.getTest().getDescription());
            put(TEST_NAME, getItemKey(metricName));

            for (SummaryMetricValueDto metricValue : metricDto.getValues()) {
                String value    = metricValue.getValueRepresentation();

                // highlight results according to decision when available
                if (metricValue.getDecision() != null) {
                    String toolTip = "Decision for metric during test run. Green - value in limits. Yellow - value crossed warning limits. Red - value outside limits";
                    switch (metricValue.getDecision()) {
                        case OK:
                            value = "<p title=\"" + toolTip + "\" style=\"color:green;font-weight:700;display:inline;\">" + value + "</p>";
                            break;
                        case WARNING:
                            value = "<p title=\"" + toolTip + "\" style=\"color:#B8860B;font-weight:700;display:inline;\">" + value + "</p>";
                            break;
                        default:
                            value = "<p title=\"" + toolTip + "\" style=\"color:red;font-weight:700;display:inline;\">" + value + "</p>";
                            break;
                        }
                }

                put(SESSION_HEADER + metricValue.getSessionId(),value);
            }
        }
    }

    private class MapValueProvider implements ValueProvider<TreeItem, String> {
        private String field;

        public MapValueProvider(String field) {
            this.field = field;
        }

        @Override
        public String getValue(TreeItem object) {
            String penImageResource = "<img src=\"" + JaggerResources.INSTANCE.getPencilImage().getSafeUri().asString() + "\" height=\"15\" width=\"15\">"
                    + "<ins font-size='10px'>double click to edit</ins><br><br>";
            String toShow;

            // Only for columns with data
            if (!field.equals(NAME)) {
                if (webClientProperties.isUserCommentEditAvailable()) {
                    if (object.get(NAME).equals(USER_COMMENT)) {
                        toShow = object.get(field).replaceAll("\n", "<br>");
                        return penImageResource + toShow;
                    }
                }
                if (webClientProperties.isTagsAvailable()) {
                    if (object.get(NAME).equals(SESSION_TAGS)) {
                        toShow = object.get(field).replaceAll("\n", "<br>");
                        return penImageResource + toShow;
                    }
                }
            }

            return object.get(field);
        }

        @Override
        public void setValue(TreeItem object, String value) {
            object.put(field, value);
        }

        @Override
        public String getPath() {
            return field;
        }
    }
}
