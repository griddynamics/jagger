package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.webclient.client.CommonDataService;
import com.griddynamics.jagger.webclient.client.dto.WebClientStartProperties;
import org.springframework.beans.factory.annotation.Required;

public class CommonDataServiceImpl implements CommonDataService {

    private DatabaseService databaseService;
    private WebClientProperties webClientProperties;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Required
    public void setWebClientProperties(WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
        this.webClientProperties.setUserCommentStoreAvailable(databaseService.checkIfUserCommentStorageAvailable());
        this.webClientProperties.setTagsStoreAvailable(databaseService.checkIfTagsStorageAvailable());
    }

    @Override
    public WebClientStartProperties getWebClientStartProperties() {
        WebClientStartProperties startProperties = new WebClientStartProperties();
        startProperties.setWebClientProperties(webClientProperties);
        startProperties.setDefaultMonitoringParameters(databaseService.getDefaultMonitoringParameters());
        return startProperties;
    }
}
