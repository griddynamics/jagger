package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.WorkloadDetailsDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public interface WorkloadServiceAsync {
    void getWorkloadDetailsForSession(List<String> sessionIds, AsyncCallback<List<WorkloadDetailsDto>> async);
}
