package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public interface PlotProviderServiceAsync {
    void getPlotData(long taskId, String plotType, AsyncCallback<List<PlotSeriesDto>> async);

    void getSessionScopePlotList(String sessionId, AsyncCallback<Set<String>> async);

    void getSessionScopePlotData(String sessionId, String plotType, AsyncCallback<List<PlotSeriesDto>> async);

    void getTaskScopePlotList(Set<String> sessionIds, TaskDataDto taskDataDto, AsyncCallback<Set<PlotNameDto>> async);

    void getPlotData(Set<Long> taskId, String plotType, AsyncCallback<List<PlotSeriesDto>> async);

    void getPlotDatas(Set<PlotNameDto> plots, AsyncCallback<Map<PlotNameDto,List<PlotSeriesDto>>> async);
}
