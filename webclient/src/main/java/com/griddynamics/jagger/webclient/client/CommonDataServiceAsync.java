package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;

import java.util.Map;
import java.util.Set;

public interface CommonDataServiceAsync {

    void getWebClientProperties(AsyncCallback<WebClientProperties> async);

    void getDefaultMonitoringParameters(AsyncCallback<Map<String,Set<String>>> async);
}
