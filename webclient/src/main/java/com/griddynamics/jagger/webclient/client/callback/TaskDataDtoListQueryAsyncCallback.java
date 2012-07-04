package com.griddynamics.jagger.webclient.client.callback;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.TaskDataTreeViewModel;
import com.griddynamics.jagger.webclient.client.data.TaskPlotNamesAsyncDataProvider;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/22/12
 */
public class TaskDataDtoListQueryAsyncCallback implements AsyncCallback<List<TaskDataDto>> {

    private Set<String> sessionIds;
    private final TaskDataTreeViewModel taskDataTreeViewModel;

    public TaskDataDtoListQueryAsyncCallback(Set<String> sessionIds, TaskDataTreeViewModel taskDataTreeViewModel) {
        this.sessionIds = sessionIds;
        this.taskDataTreeViewModel = taskDataTreeViewModel;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    @Override
    public void onFailure(Throwable caught) {
        Window.alert("Error is occurred during server request processing (Task data fetching)");
    }

    @Override
    public void onSuccess(List<TaskDataDto> result) {
        if (result.isEmpty()) {
            return;
        }

        // Populate task first level tree with server data
        taskDataTreeViewModel.populateTaskList(result);

        // Populate available plots tree level for each task for selected session
        for (TaskDataDto taskDataDto : result) {
            taskDataTreeViewModel.getPlotNameDataProviders().put
                    (taskDataDto, new TaskPlotNamesAsyncDataProvider(taskDataDto, sessionIds));
        }
    }
}
