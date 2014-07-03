package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.SummaryIntegratedDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class MetricDataServiceImpl implements MetricDataService {

    private DatabaseService databaseService;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Map<MetricNode, SummaryIntegratedDto> getMetrics(Set<MetricNode> metricNodes, boolean isEnableDecisionsPerMetricFetching) throws RuntimeException {
        return databaseService.getSummaryByMetricNodes(metricNodes, isEnableDecisionsPerMetricFetching);
    }
}