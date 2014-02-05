package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.dto.NodeInfoDto;

import java.util.List;

@RemoteServiceRelativePath("rpc/NodeInfoService")
public interface NodeInfoService extends RemoteService {
    public static class Async {
        private static final NodeInfoServiceAsync ourInstance = (NodeInfoServiceAsync) GWT.create(NodeInfoService.class);
         public static NodeInfoServiceAsync getInstance() {
            return ourInstance;
        }
    }

    public List<NodeInfoDto> getNodeInfo(String sessionId) throws RuntimeException;
}
