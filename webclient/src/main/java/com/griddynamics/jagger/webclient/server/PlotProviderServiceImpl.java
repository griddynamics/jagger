package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.PlotSeriesDto;
import com.griddynamics.jagger.dbapi.dto.SessionPlotNameDto;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;


/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl implements PlotProviderService {

    private DatabaseService databaseService;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Map<MetricNode, PlotSeriesDto> getPlotData(Set<MetricNode> plots) throws RuntimeException {
        return databaseService.getPlotDataByMetricNode(plots);
    }

    @Override
    public Map<SessionPlotNameDto, List<PlotSeriesDto>> getSessionScopePlotData(String sessionId, Collection<SessionPlotNameDto> plotType) throws RuntimeException {
        //@todo JFG-667 delete this function after creating session scope plots for monitoring and test-group metrics
        return Collections.EMPTY_MAP;
    }
}
