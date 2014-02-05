package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.NodeInfoDto;

import java.util.List;

public interface NodeInfoServiceAsync {
    void getNodeInfo(String sessionId, AsyncCallback<List<NodeInfoDto>> async);
}
