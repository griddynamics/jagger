package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.webclient.client.CommonDataService;
import com.griddynamics.jagger.webclient.client.dto.WebClientStartProperties;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.Set;

public class CommonDataServiceImpl implements CommonDataService {

    private DatabaseService databaseService;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public WebClientStartProperties getWebClientStartProperties() {
        WebClientStartProperties startProperties = new WebClientStartProperties();
        startProperties.setWebClientProperties(databaseService.getWebClientProperties());
        startProperties.setDefaultMonitoringParameters(databaseService.getDefaultMonitoringParameters());
        return startProperties;
    }
}
