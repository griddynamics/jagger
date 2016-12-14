package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.TestInfoService;
import com.griddynamics.jagger.dbapi.dto.TestInfoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;


public class TestInfoServiceImpl implements TestInfoService {

    private DatabaseService databaseService;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Map<TaskDataDto, Map<String, TestInfoDto>> getTestInfos(Collection<TaskDataDto> taskDataDtos) throws RuntimeException {
        return databaseService.getTestInfoByTaskDataDto(taskDataDtos);
    }
}
