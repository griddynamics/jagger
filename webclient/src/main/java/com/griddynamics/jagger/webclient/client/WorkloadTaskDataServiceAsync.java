package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.WorkloadDetailsDto;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 18.03.13
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public interface WorkloadTaskDataServiceAsync {
    void getWorkloadTaskData(String sessionId, AsyncCallback<List<WorkloadTaskDataDto>> async);
}
