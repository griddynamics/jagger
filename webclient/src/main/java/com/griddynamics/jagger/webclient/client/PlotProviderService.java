package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.SessionPlotNameDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
@RemoteServiceRelativePath("rpc/PlotProviderService")
public interface PlotProviderService extends RemoteService {

    List<PlotSeriesDto> getPlotData(long taskId, MetricNameDto plotType) throws RuntimeException;

    List<PlotSeriesDto> getPlotData(Set<Long> taskId, MetricNameDto plotType) throws RuntimeException;

    Map<MetricNameDto, List<PlotSeriesDto>> getPlotDatas(Set<MetricNameDto> plots) throws RuntimeException;

    Map<SessionPlotNameDto, List<PlotSeriesDto>> getSessionScopePlotData(String sessionId, Collection<SessionPlotNameDto> plotType) throws RuntimeException;

    public static class Async {
        private static final PlotProviderServiceAsync ourInstance = (PlotProviderServiceAsync) GWT.create(PlotProviderService.class);

        public static PlotProviderServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
