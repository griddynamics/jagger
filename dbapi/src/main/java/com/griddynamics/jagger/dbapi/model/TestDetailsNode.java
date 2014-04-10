package com.griddynamics.jagger.dbapi.model;


import com.griddynamics.jagger.dbapi.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Corresponds to test as child of DetailsNode
 * User: amikryukov
 * Date: 11/27/13
 */

public class TestDetailsNode extends MetricGroupNode<PlotNode> {

    private TaskDataDto taskDataDto;

    public TestDetailsNode(MetricGroupNode that) {
        super(that);
    }
    public TestDetailsNode() {}

    public TaskDataDto getTaskDataDto() {
        return taskDataDto;
    }

    public void setTaskDataDto(TaskDataDto taskDataDto) {
        this.taskDataDto = taskDataDto;
    }

    @Override
    public String getDisplayName() {
        return taskDataDto.getTaskName();
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        ArrayList<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        result.addAll(super.getChildren());
        return result;
    }
}
