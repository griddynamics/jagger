package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.smartgwt.client.widgets.grid.ListGrid;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 26.03.13
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class SessionComparisonPanel extends VerticalPanel implements SessionPanel{

    private Label title = new Label();
    private ListGrid grid = new ListGrid();

    public SessionComparisonPanel(Set<SessionDataDto> chosenSessions){
        init(chosenSessions);
    }

    private void init(Set<SessionDataDto> chosenSessions){
        add(title);
        title.setStyleName("sessionNameHeader");
        title.setWidth("1350px");
        StringBuilder titleText = new StringBuilder("Comparison of sessions : ");
        for (SessionDataDto session : chosenSessions){
            titleText.append(session.getSessionId()+",");
        }
        String titleString = titleText.toString();
        title.setText(titleString.substring(0, titleString.length()-1));

        add(grid);
    }

    @Override
    public void update(Set<TaskDataDto> tests) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateSessions(Set<SessionDataDto> sessions){
        //improve
    }

    @Override
    public void addTest(TaskDataDto test) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeTest(TaskDataDto test) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void showMetric(TaskDataDto test, String metricName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hideMetric(TaskDataDto test, String metricName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void showMetric(String metricName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hideMetric(String metricName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
