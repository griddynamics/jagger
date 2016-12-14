package com.griddynamics.jagger.webclient.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.dto.WebClientStartProperties;

import java.util.Map;
import java.util.Set;

@RemoteServiceRelativePath("rpc/CommonDataService")
public interface CommonDataService extends RemoteService {

    public WebClientStartProperties getWebClientStartProperties();

    public static class Async {
        private static final CommonDataServiceAsync ourInstance = (CommonDataServiceAsync) GWT.create(CommonDataService.class);

        public static CommonDataServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
