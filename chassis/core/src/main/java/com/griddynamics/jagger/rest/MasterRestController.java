package com.griddynamics.jagger.rest;

import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.master.SessionInfoProvider;
import com.griddynamics.jagger.master.configuration.Configuration;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.util.Decision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Master node REST API controller based on Spring MVC.
 * Aimed to expose info about currently running test suit.
 */
@RestController
@RequestMapping("/master")
public class MasterRestController {
    
    @Autowired
    private SessionInfoProvider sessionInfoProvider;
    
    @Resource(name = "${chassis.master.session.configuration.bean.name}")
    private Configuration configuration;
    
    @Value("${chassis.master.session.configuration.bean.name}")
    private String configurationName;
    
    private Map<String, WorkloadTask> nameToTaskMap = Collections.emptyMap();
    
    @PostConstruct
    public void init() {
        nameToTaskMap = mapTaskToName(configuration.getTasks());
    }
    
    @GetMapping(path = "/configuration")
    public ResponseEntity<TestConfig> getTestConfig() {
        TestConfig testConfig = new TestConfig();
        testConfig.name = configurationName;
        return new ResponseEntity<>(testConfig, HttpStatus.OK);
    }
    
    @GetMapping(path = "/session")
    public ResponseEntity<SessionEntity> getSession() {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId(sessionInfoProvider.getSessionId());
        sessionEntity.setComment(sessionInfoProvider.getSessionComment());
        sessionEntity.setKernels(sessionInfoProvider.getKernelsCount());
        if (sessionInfoProvider.getStartTime() > 0) {
            sessionEntity.setStartDate(new Date(sessionInfoProvider.getStartTime()));
            if (sessionInfoProvider.getEndTime() > 0) {
                sessionEntity.setEndDate(new Date(sessionInfoProvider.getEndTime()));
            }
        }
        
        return new ResponseEntity<>(sessionEntity, HttpStatus.OK);
    }
    
    @GetMapping(path = "/tests")
    public ResponseEntity<List<TestEntity>> getTasks() {
        
        List<TestEntity> testEntities = Lists.newArrayList();
        for (WorkloadTask task : nameToTaskMap.values()) {
            testEntities.add(convertFrom(task));
        }
        
        return new ResponseEntity<>(testEntities, HttpStatus.OK);
    }
    
    @GetMapping(path = "/tests/{name}")
    public ResponseEntity<TestEntity> getTestInfo(@PathVariable String name) {
        
        WorkloadTask task = nameToTaskMap.get(name);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(convertFrom(task), HttpStatus.OK);
    }
    
    private Map<String, WorkloadTask> mapTaskToName(List<? extends Task> tasks) {
        Map<String, WorkloadTask> nameToTaskMap = Maps.newHashMap();
        for (Task task : tasks) {
            if (task instanceof WorkloadTask) {
                nameToTaskMap.put(task.getName(), (WorkloadTask) task);
            } else if (task instanceof CompositeTask) {
                nameToTaskMap.putAll(mapTaskToName(((CompositeTask) task).getLeading()));
                nameToTaskMap.putAll(mapTaskToName(((CompositeTask) task).getAttendant()));
            }
        }
        
        return nameToTaskMap;
    }
    
    private TestEntity convertFrom(WorkloadTask task) {
        TestEntity testEntity = new TestEntity();
        testEntity.setName(task.getName());
        testEntity.setDescription(task.getDescription());
        testEntity.setTestGroupIndex(task.getGroupNumber());
        
        testEntity.setStartDate(convertFrom(task.getStartDate()));
        testEntity.setEndDate(convertFrom(task.getEndDate()));
        
        testEntity.setLoad(task.getClock().toString());
        testEntity.setClockValue(task.getClock().getValue());
        testEntity.setTerminationStrategy(task.getTerminateStrategyConfiguration().toString());
        testEntity.setTestExecutionStatus(Decision.FATAL);
        
        testEntity.setTestExecutionStatus(convertFrom(task.getStatus()));
        
        return testEntity;
    }
    
    private Date convertFrom(Long millis) {
        return millis != null ? new Date(millis) : null;
    }
    
    private Decision convertFrom(TaskData.ExecutionStatus status) {
        
        if (status == null) {
            return null;
        }
        
        switch (status) {
            case SUCCEEDED:
                return Decision.OK;
            case FAILED:
                return Decision.ERROR;
            default:
                return null;
        }
    }
    
    public static class TestConfig {
        private String name;
        
        public String getName() {
            return name;
        }
    }
}
