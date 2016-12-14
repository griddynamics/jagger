package com.griddynamics.jagger.webclient.client.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TagDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.*;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.RowMouseDownEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;


public class TagBox extends AbstractWindow implements IsWidget {

    private SessionComparisonPanel.TreeItem currentTreeItem;

    private TextArea descriptionPanel;
    private Grid<TagDto> gridStorageL;
    private Grid<TagDto> gridStorageR;

    private ListStore<TagDto> storeFrom;
    private ListStore<TagDto> storeTo;

    private final boolean ADD_NEW = true;

    private final String DEFAULT_TITLE = "Click on any row...";


    private TextButton allRight, right, left, allLeft;

    private TreeGrid<SessionComparisonPanel.TreeItem> treeGrid;

    private SessionDataDto currentSession;

    interface TagDtoProperties extends PropertyAccess<TagDto> {
        @Path("name")
        ModelKeyProvider<TagDto> name();

        @Path("name")
        ValueProvider<TagDto, String> descriptionProp();

    }

    public TagBox() {
        super();
        buttonInitialization();
        defaultButtonInitialization();

        TagDtoProperties props = GWT.create(TagDtoProperties.class);
        storeFrom = new ListStore<TagDto>(props.name());
        storeTo = new ListStore<TagDto>(props.name());


        descriptionPanel = new TextArea();
        descriptionPanel.setReadOnly(true);
        descriptionPanel.setStyleName(JaggerResources.INSTANCE.css().descriptionPanel());
        descriptionPanel.setPixelSize(width, 70);


        allRight.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                onButtonAll(ADD_NEW);
            }
        });
        right.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                onButtonOne(ADD_NEW);
            }
        });
        left.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                onButtonOne(!ADD_NEW);
            }
        });
        allLeft.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                onButtonAll(!ADD_NEW);
            }
        });

        VerticalPanel arrowsButtonBar;

        arrowsButtonBar = new VerticalPanel();
        arrowsButtonBar.getElement().getStyle().setProperty("margin", "1px");
        arrowsButtonBar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        arrowsButtonBar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        arrowsButtonBar.setVisible(true);
        arrowsButtonBar.setPixelSize(100, 150);
        arrowsButtonBar.add(allRight);
        arrowsButtonBar.add(right);
        arrowsButtonBar.add(left);
        arrowsButtonBar.add(allLeft);

        DockPanel dockSaveAndCancel = new DockPanel();
        dockSaveAndCancel.setPixelSize(width, 50);
        dockSaveAndCancel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dockSaveAndCancel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        dockSaveAndCancel.setSpacing(5);
        dockSaveAndCancel.add(getDefaultButtonBar(), DockPanel.EAST);
        dockSaveAndCancel.add(new Label(""), DockPanel.CENTER);


        gridStorageL = new Grid<TagDto>(storeFrom, createColumnList(props, "Available tags"));
        gridStorageL.setBorders(true);
        gridStorageL.getView().setForceFit(true);


        gridStorageR = new Grid<TagDto>(storeTo, createColumnList(props, "Session's tags"));
        gridStorageR.setBorders(true);
        gridStorageR.getView().setForceFit(true);

        new GridDragSource<TagDto>(gridStorageL);
        new GridDragSource<TagDto>(gridStorageR);

        new GridDropTarget<TagDto>(gridStorageL).addDropHandler(new DndDropEvent.DndDropHandler() {
            @Override
            public void onDrop(DndDropEvent dndDropEvent) {
                descriptionPanel.setText(DEFAULT_TITLE);
            }
        });
        new GridDropTarget<TagDto>(gridStorageR).addDropHandler(new DndDropEvent.DndDropHandler() {
            @Override
            public void onDrop(DndDropEvent dndDropEvent) {
                descriptionPanel.setText(DEFAULT_TITLE);
            }
        });


        gridStorageL.setPixelSize(250, 380);
        gridStorageR.setPixelSize(250, 380);

        DockPanel dockGridsAndButtons = new DockPanel();
        dockGridsAndButtons.setPixelSize(width, 390);
        dockGridsAndButtons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        dockGridsAndButtons.add(gridStorageL, DockPanel.WEST);
        dockGridsAndButtons.add(arrowsButtonBar, DockPanel.CENTER);
        dockGridsAndButtons.add(gridStorageR, DockPanel.EAST);

        gridStorageL.addRowMouseDownHandler(new RowMouseDownEvent.RowMouseDownHandler() {
            @Override
            public void onRowMouseDown(RowMouseDownEvent rowMouseDownEvent) {
                descriptionPanel.setText(gridStorageL.getStore().get(rowMouseDownEvent.getRowIndex()).getDescription());
                gridStorageR.getSelectionModel().deselectAll();
            }
        });

        gridStorageR.addRowMouseDownHandler(new RowMouseDownEvent.RowMouseDownHandler() {
            @Override
            public void onRowMouseDown(RowMouseDownEvent rowMouseDownEvent) {
                descriptionPanel.setText(gridStorageR.getStore().get(rowMouseDownEvent.getRowIndex()).getDescription());
                gridStorageL.getSelectionModel().deselectAll();

            }
        });


        if (gridStorageR.getSelectionModel().getSelectedItems().isEmpty() ||
                gridStorageL.getSelectionModel().getSelectedItems().isEmpty())
            descriptionPanel.setText(DEFAULT_TITLE);

        VerticalPanel descriptionShell = new VerticalPanel();

        descriptionShell.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        descriptionShell.add(descriptionPanel);
        descriptionShell.setPixelSize(width, 30);
        descriptionPanel.setReadOnly(true);

        VerticalPanel mainPanel;
        mainPanel = new VerticalPanel();
        mainPanel.setPixelSize(width, height);
        mainPanel.add(dockGridsAndButtons);
        mainPanel.add(descriptionShell);
        mainPanel.add(dockSaveAndCancel);

        setAutoHideEnabled(true);

        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                atClose();
            }
        });
        add(mainPanel);

    }

    public void setTreeGrid(TreeGrid<SessionComparisonPanel.TreeItem> treeGrid) {
        this.treeGrid = treeGrid;
    }


    public void popUpForEdit(SessionDataDto currentSession, SessionComparisonPanel.TreeItem item, List<TagDto> allTags) {
        if (currentSession == null) {
            new ExceptionPanel("The session data has a value null. The session's id is wrong.");
        }
        getApplyButton().removeFromParent();
        this.currentSession = currentSession;
        setText("Session " + currentSession.getSessionId());
        setGrids(allTags, currentSession.getTags());
        currentTreeItem = item;
        show();
    }

    private Set<String> tagNamesSet;

    public void popUpForFilter(List<TagDto> allTags, Set<String> sessionTags) {
        getSaveButton().removeFromParent();
        tagNamesSet = sessionTags;
        setText("Session filter by tags");
        setGrids(allTags);
        show();
    }

    private ColumnModel<TagDto> createColumnList(TagDtoProperties props, String columnName) {
        ColumnConfig<TagDto, String> cc1 = new ColumnConfig<TagDto, String>(props.descriptionProp());
        cc1.setHeader(SafeHtmlUtils.fromString(columnName));
        cc1.setFixed(true);
        cc1.setMenuDisabled(true);
        cc1.setWidth(250);
        List<ColumnConfig<TagDto, ?>> l = new ArrayList<ColumnConfig<TagDto, ?>>();
        l.add(cc1);
        return new ColumnModel<TagDto>(l);
    }

    @Override
    protected void onSaveButtonClick() {
        treeGrid.getTreeView().refresh(false);
        saveTagToDataBase();
    }

    @Override
    protected void onApplyButtonClick() {
        tagNamesSet.clear();
        for (int i = 0; i < storeTo.size(); i++) {
            tagNamesSet.add(storeTo.get(i).getName());
        }
        atClose();
    }

    @Override
    protected void onCancelButtonClick() {
        atClose();
    }

    private void onButtonOne(boolean action) {
        if (action) {
            move(gridStorageL, gridStorageR);
        } else {
            move(gridStorageR, gridStorageL);
        }
    }

    private void move(Grid<TagDto> gridFrom, Grid<TagDto> gridTo) {
        if (gridFrom.getSelectionModel().getSelectedItems().isEmpty())
            return;
        List<TagDto> selectedList = gridFrom.getSelectionModel().getSelectedItems();
        gridFrom.getSelectionModel().selectNext(false);
        descriptionPanel.setText(gridFrom.getSelectionModel().getSelectedItem().getDescription());

        gridTo.getSelectionModel().deselectAll();
        gridTo.getStore().addAll(selectedList);

        for (int i = 0; i < selectedList.size(); i++) {
            gridFrom.getStore().remove(selectedList.get(i));
        }

    }

    private void moveAll(Grid<TagDto> gridFrom, Grid<TagDto> gridTo) {
        gridTo.getStore().addAll(gridFrom.getStore().getAll());
        gridFrom.getStore().clear();
        descriptionPanel.setText(DEFAULT_TITLE);
    }

    private void onButtonAll(boolean action) {
        if (action) {
            moveAll(gridStorageL, gridStorageR);
        } else {
            moveAll(gridStorageR, gridStorageL);
        }
    }

    public void setGrids(List<TagDto> allTags, List<TagDto> sessionTags) {
        gridStorageL.getStore().addAll(allTags);
        gridStorageR.getStore().addAll(sessionTags);
        for (TagDto tag : allTags) {
            for (int i = 0; i < gridStorageR.getStore().size(); i++) {
                if (gridStorageR.getStore().get(i).equals(tag))
                    gridStorageL.getStore().remove(tag);
            }
        }
    }

    public void setGrids(List<TagDto> allTags) {
        gridStorageL.getStore().addAll(allTags);
    }

    private void saveTagToDataBase() {
        final List<TagDto> list = new ArrayList<TagDto>();
        list.addAll(gridStorageR.getStore().getAll());
        SessionDataService.Async.getInstance().saveTags(currentSession.getId(), list, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel("Fail to save into DB session's tags : " + caught.getMessage());
                atClose();
            }

            @Override
            public void onSuccess(Void result) {
                String tags = "";
                for (int i = 0; i < storeTo.size(); i++) {
                    if (i == storeTo.size() - 1)
                        tags += storeTo.get(i).getName();
                    else
                        tags += storeTo.get(i).getName() + ", ";
                }
                currentTreeItem.put(getText(), tags);
                currentSession.setTags(list);
                treeGrid.getTreeView().refresh(false);
                atClose();
            }
        });
    }

    private void buttonInitialization() {
        allRight = new TextButton(">>>");
        allRight.setPixelSize(40, 15);

        right = new TextButton(">");
        right.setPixelSize(40, 15);


        left = new TextButton("<");
        left.setPixelSize(40, 15);


        allLeft = new TextButton("<<<");
        allLeft.setPixelSize(40, 15);
    }

    private void atClose() {
        gridStorageL.getStore().clear();
        gridStorageR.getStore().clear();
        descriptionPanel.setText(DEFAULT_TITLE);
        hide();
    }
}

