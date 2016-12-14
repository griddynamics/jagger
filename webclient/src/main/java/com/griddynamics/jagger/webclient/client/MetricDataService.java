package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.griddynamics.jagger.dbapi.dto.SummaryIntegratedDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;


import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
@RemoteServiceRelativePath("rpc/MetricDataService")
public interface MetricDataService extends RemoteService {
    /**
     * Utility/Convenience class.
     * Use MetricDataService.App.getInstance() to access static instance of MetricDataServiceAsync
     */
    public static class Async {
        private static final MetricDataServiceAsync ourInstance = (MetricDataServiceAsync) GWT.create(MetricDataService.class);

        public static MetricDataServiceAsync getInstance() {
            return ourInstance;
        }
    }

    public Map<MetricNode, SummaryIntegratedDto> getMetrics(Set<MetricNode> metricNames, boolean isEnableDecisionsPerMetricFetching) throws RuntimeException;
}
