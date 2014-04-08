package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 * @deprecated another conception of control since jagger 1.2.2-m3
 */
@RemoteServiceRelativePath("rpc/TaskDataService")
@Deprecated
public interface TaskDataService extends RemoteService {

    List<TaskDataDto> getTaskDataForSession(String sessionId) throws RuntimeException;

    List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds) throws RuntimeException;

    public static class Async {
        private static final TaskDataServiceAsync ourInstance = (TaskDataServiceAsync) GWT.create(TaskDataService.class);

        public static TaskDataServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
