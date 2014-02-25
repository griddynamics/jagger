package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Corresponds to test as child of summary node
 * User: amikryukov
 * Date: 11/26/13
 */
public class TestNode extends MetricGroupNode<MetricNode> {

    private TaskDataDto taskDataDto;

    private TestInfoNode testInfo;

    public TestNode(MetricGroupNode that) {
        super(that);
    }
    public TestNode() {}

    @Override
    public String toString() {
        return "TestNode class to string";
    }

    public TaskDataDto getTaskDataDto() {
        return taskDataDto;
    }

    public void setTaskDataDto(TaskDataDto taskDataDto) {
        this.taskDataDto = taskDataDto;
    }

    public TestInfoNode getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(TestInfoNode testInfo) {
        this.testInfo = testInfo;
    }

    @Override
    public String getDisplayName() {
        return taskDataDto.getTaskName();
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        ArrayList<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        if (testInfo != null) result.add(testInfo);
        result.addAll(super.getChildren());
        return result;
    }
}
