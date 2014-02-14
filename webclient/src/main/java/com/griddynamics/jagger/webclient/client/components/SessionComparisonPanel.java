package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.data.WebClientProperties;
import com.griddynamics.jagger.webclient.client.dto.*;
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
public class SessionComparisonPanel extends VerticalPanel{

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

    private Set<SessionDataDto> chosenSessions;

    private final String WHITE_SPACE_NORMAL = "white-space: normal";

    private TreeGrid<TreeItem> treeGrid;
    private TreeStore<TreeItem> treeStore = new TreeStore<TreeItem>(new ModelKeyProvider<TreeItem>() {
        @Override
        public String getKey(TreeItem item) {
            return String.valueOf(item.getKey());
        }
    });

    private HashMap<MetricNameDto, MetricDto> cache = new HashMap<MetricNameDto, MetricDto>();

    private WebClientProperties webClientProperties;

    public HashMap<MetricNameDto, MetricDto> getCachedMetrics() {
        return cache;
    }

    public SessionComparisonPanel(Set<SessionDataDto> chosenSessions, int width, WebClientProperties webClientProperties){
        setWidth(ONE_HUNDRED_PERCENTS);
        setHeight(ONE_HUNDRED_PERCENTS);
        this.chosenSessions = chosenSessions;
        this.webClientProperties = webClientProperties;
        init(chosenSessions, width);
        userCommentBox = new UserCommentBox(webClientProperties.getUserCommentMaxLength());
        userCommentBox.setTreeGrid(treeGrid);
    }


    private void init(Set<SessionDataDto> chosenSessions, int width){

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
                new ColumnConfig<TreeItem, String>(new MapValueProvider(NAME), (int)(colWidth * METRIC_COLUMN_WIDTH_FACTOR));
        nameColumn.setHeader(METRIC);
        nameColumn.setSortable(false);
        nameColumn.setMenuDisabled(true);
        columns.add(nameColumn);

        for (SessionDataDto session: sortedSet) {
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

        if (webClientProperties.isUserCommentAvailable()) {
            treeGrid.addCellDoubleClickHandler(new CellDoubleClickEvent.CellDoubleClickHandler() {
                @Override
                public void onCellClick(CellDoubleClickEvent event) {
                    TreeItem item = treeGrid.findNode(treeGrid.getTreeView().getRow(event.getRowIndex())).getModel();
                    if (item.getKey().equals(USER_COMMENT) && event.getCellIndex() > 0) {
                        String sessionId = treeGrid.getColumnModel().getColumn(event.getCellIndex()).getHeader().asString();
                        userCommentBox.popUp(
                                sessionId,
                                item.get(sessionId),
                                item
                            );
                    }
                }
            });
        }

        add(treeGrid);
    }


    /**
     * calculates width for columns
     * @param size number of chosen sessions
     * @param width is offset width of parent container
     * @return width of Session * column
     */
    private int calculateWidth(int size, int width) {
        int colWidth = (int)(width / (size + METRIC_COLUMN_WIDTH_FACTOR));
        if (colWidth < MIN_COLUMN_WIDTH)
            colWidth = MIN_COLUMN_WIDTH;

        return colWidth;
    }

    public void addSessionInfo() {
        TreeItem sessionInfo = new TreeItem(SESSION_INFO_ID);
        sessionInfo.put(NAME, "Session Info");
        // sessionInfo always on top
        treeStore.insert(0, sessionInfo);

        addCommentRecord(chosenSessions, sessionInfo);
        addUserCommentRecord(chosenSessions, sessionInfo);
        addStartEndTimeRecords(chosenSessions, sessionInfo);
        addAdditionalRecords(chosenSessions, sessionInfo);
    }

    public void removeSessionInfo() {
        TreeItem sessionInfo = treeStore.findModelWithKey(SESSION_INFO_ID);
        if (sessionInfo != null)
            treeStore.remove(sessionInfo);
    }

    private void addAdditionalRecords(Set<SessionDataDto> chosenSessions, TreeItem parent) {
        TreeItem item = new TreeItem(ACTIVE_KERNELS);
        item.put(NAME, ACTIVE_KERNELS);
        for (SessionDataDto session : chosenSessions) {
            item.put(SESSION_HEADER + session.getSessionId(), session.getActiveKernelsCount() + "");
        }
        treeStore.add(parent, item);

        item = new TreeItem(TASKS_EXECUTED);
        item.put(NAME, TASKS_EXECUTED);
        for (SessionDataDto session : chosenSessions) {
            item.put(SESSION_HEADER + session.getSessionId(), session.getTasksExecuted() + "");
        }
        treeStore.add(parent, item);

        item = new TreeItem(TASKS_FAILED);
        item.put(NAME, TASKS_FAILED);
        for (SessionDataDto session : chosenSessions) {
            item.put(SESSION_HEADER + session.getSessionId(), session.getTasksFailed() + "");
        }
        treeStore.add(parent, item);
    }

    private void addStartEndTimeRecords(Set<SessionDataDto> chosenSessions, TreeItem parent) {
        TreeItem date = new TreeItem(START_DATE);
        date.put(NAME, START_DATE);
        for (SessionDataDto session : chosenSessions) {
            date.put(SESSION_HEADER + session.getSessionId(), session.getStartDate());
        }
        treeStore.add(parent, date);

        date = new TreeItem(END_DATE);
        date.put(NAME, END_DATE);
        for (SessionDataDto session : chosenSessions) {
            date.put(SESSION_HEADER + session.getSessionId(), session.getEndDate());
        }
        treeStore.add(parent, date);
    }

    private void addCommentRecord(Set<SessionDataDto> chosenSessions, TreeItem parent) {

        TreeItem comment = new TreeItem(COMMENT);
        comment.put(NAME, COMMENT);
        for (SessionDataDto session : chosenSessions) {
            comment.put(SESSION_HEADER + session.getSessionId(), session.getComment());
        }
        treeStore.add(parent, comment);

    }

    private void addUserCommentRecord(Set<SessionDataDto> chosenSessions, TreeItem parent) {

        TreeItem comment = new TreeItem(USER_COMMENT);
        comment.put(NAME, USER_COMMENT);
        for (SessionDataDto session : chosenSessions) {
            // Add nothing for test. Later it will be taken from SessionDataDto.
            comment.put(SESSION_HEADER + session.getSessionId(), "");
        }
        treeStore.add(parent, comment);
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


    public void addMetricRecord(MetricDto metricDto) {

        cache.put(metricDto.getMetricName(), metricDto);
        TreeItem record = new TreeItem(metricDto);
        addItemToStore(record);
    }


    public void addMetricRecords(List<MetricDto> loaded) {
        for (MetricDto metric : loaded) {
            addMetricRecord(metric);
        }
    }

    private void addItemToStore(TreeItem record) {

        String descriptionString = record.get(TEST_DESCRIPTION);
        String testNameString = record.get(TEST_NAME);

        if (descriptionString == null || testNameString == null)
            return;

        TreeItem testItem = getTestItem(descriptionString, testNameString);
        for (TreeItem rec : treeStore.getChildren(testItem)) {
            if (rec.getKey().equals(record.getKey())) {
                return;
            }
        }
        treeStore.add(testItem, record);
    }


    public void removeRecords(List<MetricDto> list) {

        for (MetricDto metric : list) {
            removeRecord(metric);
        }
    }

    private void removeRecord(MetricDto metric) {

        String description = metric.getMetricName().getTests().getDescription();
        String testName = metric.getMetricName().getTests().getTaskName();
        String key = getItemKey(metric.getMetricName());

        TreeItem testItem = getTestItem(description, testName);
        for (TreeItem item : treeStore.getChildren(testItem)) {
            if (item.getKey().equals(key)) {
                removeWithParent(item);
                return;
            }
        }
    }

    private String getItemKey(MetricNameDto metricName) {
        return metricName.getTests().getDescription() + metricName.getTests().getTaskName() + metricName.getName();
    }

    private void removeWithParent(TreeItem toRemove) {
        TreeItem parent = treeStore.getParent(toRemove);
        treeStore.remove(toRemove);
        if (parent != null && !treeStore.hasChildren(parent)) {
            removeWithParent(parent);
        }
    }


    public void addTestInfo(TaskDataDto test, Map<String, TestInfoDto> testInfoMap) {
        TreeItem testItem = getTestItem(test.getDescription(), test.getTaskName());

        String testInfoId = test.getDescription() + test.getTaskName() + TEST_INFO;
        if (treeStore.findModelWithKey(testInfoId) != null) {
            return;
        }

        TreeItem testInfo = new TreeItem(testInfoId);
        testInfo.put(NAME, TEST_INFO);
        testInfo.put(TEST_DESCRIPTION, test.getDescription());
        testInfo.put(TEST_NAME, test.getTaskName());
        treeStore.insert(testItem, 0 , testInfo);

        TreeItem clock = new TreeItem(testItem.getKey() + "Clock");
        clock.put(NAME, "Clock");
        clock.put(TEST_DESCRIPTION, test.getDescription());
        clock.put(TEST_NAME, test.getTaskName());
        clock.put(TEST_INFO, TEST_INFO);
        for (SessionDataDto session : chosenSessions) {
            if (testInfoMap.get(session.getSessionId()) != null)
                clock.put(SESSION_HEADER + session.getSessionId(), testInfoMap.get(session.getSessionId()).getClock());
        }
        treeStore.add(testInfo, clock);

        TreeItem termination = new TreeItem(testItem.getKey() + "Termination");
        termination.put(NAME, "Termination");
        termination.put(TEST_DESCRIPTION, test.getDescription());
        termination.put(TEST_NAME, test.getTaskName());
        termination.put(TEST_INFO, TEST_INFO);
        for (SessionDataDto session : chosenSessions) {
            if (testInfoMap.get(session.getSessionId()) != null)
                termination.put(SESSION_HEADER + session.getSessionId(), testInfoMap.get(session.getSessionId()).getTermination());
        }
        treeStore.add(testInfo, termination);
    }

    public void removeTestInfo(TaskDataDto test) {

        TreeItem testItem = getTestItemIfExists(test.getDescription(), test.getTaskName());
        if (testItem == null) {
            return;
        }
        TreeItem testInfo = treeStore.getFirstChild(testItem);
        if (testInfo != null && TEST_INFO.equals(testInfo.get(NAME)))
            removeWithParent(testInfo);
    }


    private TreeItem getTestItem(String descriptionStr, String taskNameStr) {
        TreeItem description = getTestDescriptionItem(descriptionStr);
        for (TreeItem item : treeStore.getChildren(description)) {
            if (taskNameStr.equals(item.get(NAME))) {
                return item;
            }
        }
        TreeItem taskName = new TreeItem(descriptionStr + taskNameStr);
        taskName.put(NAME, taskNameStr);
        taskName.put(TEST_DESCRIPTION, descriptionStr);
        treeStore.add(description, taskName);
        return taskName;
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


    private TreeItem getTestItemIfExists(String descriptionStr, String taskNameStr) {
        TreeItem description = getTestDescriptionItemIfExists(descriptionStr);

        if (description ==  null) {
            return null;
        }

        for (TreeItem item : treeStore.getChildren(description)) {
            if (taskNameStr.equals(item.get(NAME))) {
                return item;
            }
        }
        return null;
    }

    private TreeItem getTestDescriptionItemIfExists(String descriptionStr) {
        for (TreeItem item : treeStore.getRootItems()) {
            if (descriptionStr.equals(item.get(NAME))) {
                return item;
            }
        }
        return null;
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

        String key;

        private String getKey() {
            return key;
        }

        @SuppressWarnings("unused")
        public TreeItem() {}

        public TreeItem(String key) {
            this.key = key;
        }

        public TreeItem(MetricDto metricDto) {

            MetricNameDto metricName = metricDto.getMetricName();
            this.key = getItemKey(metricName);
            put(NAME, metricName.getDisplay());
            put(TEST_DESCRIPTION, metricName.getTests().getDescription());
            put(TEST_NAME, metricName.getTests().getTaskName());

            for (MetricValueDto metricValue : metricDto.getValues()) {
                put(SESSION_HEADER + metricValue.getSessionId(), metricValue.getValueRepresentation());
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

            if (webClientProperties.isUserCommentAvailable()) {
                if (object.get(NAME).equals(USER_COMMENT) && !field.equals(NAME)) {
                    String toShow = object.get(field).replaceAll("\n", "<br>");
                    return "<img src=\"" + JaggerResources.INSTANCE.getPencilImage().getSafeUri().asString() + "\" height=\"15\" width=\"15\">"
                            + "<ins font-size='10px'>double click to edit</ins><br><br>"
                            + toShow;
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
