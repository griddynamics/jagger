package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 18.03.13
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 * @deprecated another conception of control since jagger 1.2.2-m3
 */
@RemoteServiceRelativePath("rpc/WorkloadTaskDataService")
@Deprecated
public interface WorkloadTaskDataService extends RemoteService {

    public List<WorkloadTaskDataDto> getWorkloadTaskData(String sessionId) throws RuntimeException;

    public WorkloadTaskDataDto getWorkloadTaskData(String sessionId, long taskId) throws RuntimeException;

    public Set<WorkloadTaskDataDto> getWorkloadTaskData(Set<TaskDataDto> tests) throws RuntimeException;

    public static class Async {
        private static final WorkloadTaskDataServiceAsync ourInstance = (WorkloadTaskDataServiceAsync) GWT.create(WorkloadTaskDataService.class);

        public static WorkloadTaskDataServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
