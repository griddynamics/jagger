package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;


import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 * @deprecated another conception of control since jagger 1.2.2-m3
 */
@Deprecated
public interface TaskDataServiceAsync {
    void getTaskDataForSessions(Set<String> sessionIds, AsyncCallback<List<TaskDataDto>> async);

    void getTaskDataForSession(String sessionId, AsyncCallback<List<TaskDataDto>> async);
}
