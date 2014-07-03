package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.dbapi.model.RootNode;

import java.util.Set;

@RemoteServiceRelativePath("rpc/ControlTreeCreatorService")
public interface ControlTreeCreatorService extends RemoteService {

    @Deprecated
    RootNode getControlTreeForSession(String sessionId, boolean isShowOnlyMatchedTests) throws RuntimeException;

    RootNode getControlTreeForSessions(Set<String> sessionIds, boolean isShowOnlyMatchedTests) throws RuntimeException;

    public static class Async {
        private static final ControlTreeCreatorServiceAsync ourInstance = (ControlTreeCreatorServiceAsync) GWT.create(ControlTreeCreatorService.class);

        public static ControlTreeCreatorServiceAsync getInstance() {
            return ourInstance;
        }
    }
}