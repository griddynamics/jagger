package com.griddynamics.jagger.webclient.client.dto;

import com.griddynamics.jagger.dbapi.model.WebClientProperties;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Class that contain all start properties for ui side.
 * Used to pass all startup settings from server side to client side with single package.
 */
public class WebClientStartProperties implements Serializable {

    private WebClientProperties webClientProperties;
    private Map<String, Set<String>> defaultMonitoringParameters;

    public WebClientStartProperties() {
    }

    public WebClientProperties getWebClientProperties() {
        return webClientProperties;
    }

    public void setWebClientProperties(WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
    }

    public Map<String, Set<String>> getDefaultMonitoringParameters() {
        return defaultMonitoringParameters;
    }

    public void setDefaultMonitoringParameters(Map<String, Set<String>> defaultMonitoringParameters) {
        this.defaultMonitoringParameters = defaultMonitoringParameters;
    }
}
