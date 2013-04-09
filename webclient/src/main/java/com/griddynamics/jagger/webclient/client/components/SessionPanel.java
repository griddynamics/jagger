package com.griddynamics.jagger.webclient.client.components;

import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 26.03.13
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
public interface SessionPanel {

    public void update(Set<TaskDataDto> tests);

    public void addTest(TaskDataDto test);

    public void removeTest(TaskDataDto test);

    public void showMetric(TaskDataDto test, String metricName);

    public void hideMetric(TaskDataDto test, String metricName);

    public void showMetric(String metricName);

    public void hideMetric(String metricName);
}
