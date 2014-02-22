package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.NodeInfoPerSessionDto;

import java.util.List;
import java.util.Set;

public interface NodeInfoServiceAsync {
    void getNodeInfo(Set<String> sessionIds, AsyncCallback<List<NodeInfoPerSessionDto>> async);
}
