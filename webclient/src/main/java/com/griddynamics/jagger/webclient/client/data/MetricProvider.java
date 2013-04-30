package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 23.04.13
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class MetricProvider extends AsyncDataProvider<MetricNameDto> {

    private TaskDataDto taskDataDto;

    public MetricProvider(TaskDataDto taskDataDto){
        this.taskDataDto = taskDataDto;
    }

    @Override
    protected void onRangeChanged(HasData<MetricNameDto> display) {
        MetricDataService.Async.getInstance().getMetricsNames(new HashSet<TaskDataDto>(Arrays.asList(taskDataDto)), new AsyncCallback<Set<MetricNameDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(Set<MetricNameDto> result) {
                List list =  new ArrayList<MetricNameDto>(result);
                MetricRankingProvider.sortMetricNames(list);
                updateRowData(0, list);
            }
        });
    }
}
