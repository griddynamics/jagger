package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.dto.TestInfoDto;

import java.util.Collection;
import java.util.Map;

@RemoteServiceRelativePath("rpc/TestInfoService")
public interface TestInfoService extends RemoteService {

    Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfos(Collection<TaskDataDto> taskDataDtos) throws RuntimeException;

    public static class Async {
        private static final TestInfoServiceAsync ourInstance = (TestInfoServiceAsync) GWT.create(TestInfoService.class);

        public static TestInfoServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
