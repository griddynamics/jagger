package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.dbapi.dto.NodeInfoPerSessionDto;

import java.util.List;
import java.util.Set;

@RemoteServiceRelativePath("rpc/NodeInfoService")
public interface NodeInfoService extends RemoteService {
    public static class Async {
        private static final NodeInfoServiceAsync ourInstance = (NodeInfoServiceAsync) GWT.create(NodeInfoService.class);
         public static NodeInfoServiceAsync getInstance() {
            return ourInstance;
        }
    }

    public List<NodeInfoPerSessionDto> getNodeInfo(Set<String> sessionIds) throws RuntimeException;
}
