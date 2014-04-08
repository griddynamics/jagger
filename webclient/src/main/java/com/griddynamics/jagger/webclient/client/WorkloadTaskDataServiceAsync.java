package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 18.03.13
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 * @deprecated another conception of control since jagger 1.2.2-m3
 */
@Deprecated
public interface WorkloadTaskDataServiceAsync {
    void getWorkloadTaskData(String sessionId, AsyncCallback<List<WorkloadTaskDataDto>> async);
    void getWorkloadTaskData(String sessionId, long taskId, AsyncCallback<WorkloadTaskDataDto> async);
    void getWorkloadTaskData(Set<TaskDataDto> tests, AsyncCallback<Set<WorkloadTaskDataDto>> async);
}
