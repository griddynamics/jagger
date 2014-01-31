package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Corresponds to test as child of DetailsNode
 * User: amikryukov
 * Date: 11/27/13
 */

public class TestDetailsNode extends AbstractIdentifyNode {

    private TaskDataDto taskDataDto;

    private List<PlotNode> plots;

    private List<MonitoringPlotNode> monitoringPlots;

    public List<MonitoringPlotNode> getMonitoringPlots() {

        if (monitoringPlots == null) {
            return Collections.EMPTY_LIST;
        }
        return monitoringPlots;
    }

    public void setMonitoringPlots(List<MonitoringPlotNode> monitoringPlots) {
        this.monitoringPlots = monitoringPlots;
    }

    public TaskDataDto getTaskDataDto() {
        return taskDataDto;
    }

    public void setTaskDataDto(TaskDataDto taskDataDto) {
        this.taskDataDto = taskDataDto;
    }

    public List<PlotNode> getPlots() {
        return plots;
    }

    public void setPlots(List<PlotNode> plots) {
        this.plots = plots;
    }

    @Override
    public String getDisplayName() {
        return taskDataDto.getTaskName();
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        ArrayList<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        if (plots != null) result.addAll(plots);
        if (monitoringPlots != null) result.addAll(monitoringPlots);
        return result;
    }
}
