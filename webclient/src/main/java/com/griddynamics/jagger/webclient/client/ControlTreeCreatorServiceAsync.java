package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.model.RootNode;

import java.util.Set;

public interface ControlTreeCreatorServiceAsync {
    void getControlTreeForSession(String sessionId, boolean isShowOnlyMatchedTests, AsyncCallback<RootNode> async);

    void getControlTreeForSessions(Set<String> sessionIds, boolean isShowOnlyMatchedTests, AsyncCallback<RootNode> async) throws RuntimeException;
}
