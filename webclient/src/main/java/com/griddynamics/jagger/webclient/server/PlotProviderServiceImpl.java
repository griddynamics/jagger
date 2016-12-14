package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.PlotIntegratedDto;
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
    public Map<MetricNode, PlotIntegratedDto> getPlotData(Set<MetricNode> plots) throws RuntimeException {
        return databaseService.getPlotDataByMetricNode(plots);
    }
}
