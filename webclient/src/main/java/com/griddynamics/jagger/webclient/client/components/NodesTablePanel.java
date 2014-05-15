package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.griddynamics.jagger.dbapi.dto.NodeInfoDto;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import java.util.*;

public class NodesTablePanel extends VerticalPanel{

    private final String NAME = "name";
    private final String SESSION_INFO_ID = "sessionInfo";
    private final int MIN_COLUMN_WIDTH = 250;
    private final String ONE_HUNDRED_PERCENTS = "100%";
    private final double METRIC_COLUMN_WIDTH_FACTOR = 1.5;
    private final String WHITE_SPACE_NORMAL = "white-space: normal";

    private TreeGrid<TreeItem> treeGrid;
    private TreeStore<TreeItem> treeStore = new TreeStore<TreeItem>(new ModelKeyProvider<TreeItem>() {
        @Override
        public String getKey(TreeItem item) {
            return String.valueOf(item.getKey());
        }
    });

    public NodesTablePanel(){
        setWidth(ONE_HUNDRED_PERCENTS);
        setHeight(ONE_HUNDRED_PERCENTS);
    }

    public void createTable(String sessionId, List<NodeInfoDto> nodeInfoDtoList){

        int colWidth = MIN_COLUMN_WIDTH;

        treeStore.clear();
        List<ColumnConfig<TreeItem, ?>> columns = new ArrayList<ColumnConfig<TreeItem, ?>>();

        //sort nodes by nodeId
        SortedSet<NodeInfoDto> sortedNodeInfoDtoList = new TreeSet<NodeInfoDto>(new Comparator<NodeInfoDto>() {
            @Override
            public int compare(NodeInfoDto o, NodeInfoDto o2) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(o.getNodeId(), o2.getNodeId());
                return (res != 0) ? res : o.getNodeId().compareTo(o2.getNodeId());
            }
        });
        sortedNodeInfoDtoList.addAll(nodeInfoDtoList);

        ColumnConfig<TreeItem, String> nameColumn =
                new ColumnConfig<TreeItem, String>(new MapValueProvider(NAME), (int)(colWidth * METRIC_COLUMN_WIDTH_FACTOR));
        nameColumn.setHeader("Parameter");
        nameColumn.setSortable(false);
        nameColumn.setMenuDisabled(true);
        columns.add(nameColumn);

        // Header
        for (NodeInfoDto node : sortedNodeInfoDtoList) {
            ColumnConfig<TreeItem, String> column =
                    new ColumnConfig<TreeItem, String>(new MapValueProvider(node.getNodeId()));
            column.setHeader(node.getNodeId());
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

        treeGrid.setAutoExpand(true);
        treeGrid.getView().setStripeRows(true);
        treeGrid.setMinColumnWidth(MIN_COLUMN_WIDTH);
        treeGrid.setAllowTextSelection(true);

        treeStore.addStoreAddHandler(new StoreAddEvent.StoreAddHandler<TreeItem>() {
            @Override
            public void onAdd(StoreAddEvent<TreeItem> event) {
                for (TreeItem item : event.getItems()) {
                    treeGrid.setExpanded(item, true);
                }
            }
        });

        add(treeGrid);

        // Data
        // Session
        TreeItem sessionInfo = new TreeItem(SESSION_INFO_ID);
        sessionInfo.put(NAME, "Session " + sessionId);
        treeStore.insert(0, sessionInfo);

        // Get all params
        Set<String> param_names = new TreeSet<String>();
        for (NodeInfoDto node : sortedNodeInfoDtoList){
            param_names.addAll(node.getParameters().keySet());
        }

        for (String param_name : param_names) {
            TreeItem date = new TreeItem(param_name);
            date.put(NAME, param_name);
            for (NodeInfoDto node : sortedNodeInfoDtoList){
                if (node.getParameters().containsKey(param_name)) {
                    date.put(node.getNodeId(), node.getParameters().get(param_name));
                }
                else {
                    date.put(node.getNodeId(), "");
                }
            }
            treeStore.add(sessionInfo, date);
        }
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
    }

    private class MapValueProvider implements ValueProvider<TreeItem, String> {
        private String field;

        public MapValueProvider(String field) {
            this.field = field;
        }

        @Override
        public String getValue(TreeItem object) {
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
