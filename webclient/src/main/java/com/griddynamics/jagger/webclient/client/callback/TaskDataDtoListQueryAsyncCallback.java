package com.griddynamics.jagger.webclient.client.callback;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.MultiSelectionModel;
import com.griddynamics.jagger.webclient.client.components.ExceptionPanel;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/22/12
 */
public class TaskDataDtoListQueryAsyncCallback implements AsyncCallback<List<TaskDataDto>> {

    private Set<String> sessionIds;
    private final CellTable<TaskDataDto> testGrid;

    public TaskDataDtoListQueryAsyncCallback(Set<String> sessionIds, CellTable<TaskDataDto> testGrid) {
        this.sessionIds = sessionIds;
        this.testGrid = testGrid;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    @Override
    public void onFailure(Throwable caught) {
        new ExceptionPanel("Error is occurred during server request processing (Task data fetching)");
    }

    @Override
    public void onSuccess(List<TaskDataDto> result) {
        if (result.isEmpty()) {
            return;
        }
        MultiSelectionModel model = (MultiSelectionModel)testGrid.getSelectionModel();
        model.clear();

        testGrid.redraw();
        testGrid.setRowData(result);
    }
}
