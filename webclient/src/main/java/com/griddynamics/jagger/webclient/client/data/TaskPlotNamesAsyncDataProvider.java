package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/19/12
 */
public class TaskPlotNamesAsyncDataProvider extends AsyncDataProvider<PlotNameDto> {
    private final TaskDataDto taskDataDto;
    private final Set<String> sessionIds;

    public TaskPlotNamesAsyncDataProvider(TaskDataDto taskDataDto, Set<String> sessionIds) {
        this.taskDataDto = taskDataDto;
        this.sessionIds = sessionIds;
    }

    public TaskPlotNamesAsyncDataProvider(ProvidesKey<PlotNameDto> keyProvider, TaskDataDto taskDataDto, Set<String> sessionIds) {
        super(keyProvider);
        this.taskDataDto = taskDataDto;
        this.sessionIds = sessionIds;
    }

    @Override
    protected void onRangeChanged(HasData<PlotNameDto> display) {
        Range range = display.getVisibleRange();
        final int start = range.getStart();

        PlotProviderService.Async.getInstance().getTaskScopePlotList(sessionIds, taskDataDto, new AsyncCallback<Set<PlotNameDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error is occurred during server request processing (Plot names for task fetching)");
            }

            @Override
            public void onSuccess(Set<PlotNameDto> result) {
                updateRowData(start, new ArrayList<PlotNameDto>(result));
                updateRowCount(result.size(), false);
            }
        });
    }
}
