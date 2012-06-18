package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.griddynamics.jagger.webclient.client.dto.WorkloadDetailsDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
@RemoteServiceRelativePath("rpc/WorkloadService")
public interface WorkloadService extends RemoteService {

    List<WorkloadDetailsDto> getWorkloadDetailsForSession(List<String> sessionIds);

    public static class Async {
        private static final WorkloadServiceAsync ourInstance = (WorkloadServiceAsync) GWT.create(WorkloadService.class);

        public static WorkloadServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
