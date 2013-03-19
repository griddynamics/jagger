package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 18.03.13
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
@RemoteServiceRelativePath("rpc/WorkloadTaskDataService")
public interface WorkloadTaskDataService extends RemoteService {

    public List<WorkloadTaskDataDto> getWorkloadTaskData(String sessionId);

    public static class Async {
        private static final WorkloadTaskDataServiceAsync ourInstance = (WorkloadTaskDataServiceAsync) GWT.create(WorkloadTaskDataService.class);

        public static WorkloadTaskDataServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
