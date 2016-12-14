package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.WebClientStartProperties;

public interface CommonDataServiceAsync {

    void getWebClientStartProperties(AsyncCallback<WebClientStartProperties> async);
}
