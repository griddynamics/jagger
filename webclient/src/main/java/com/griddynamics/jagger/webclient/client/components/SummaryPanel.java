package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 20.03.13
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */
public class SummaryPanel extends Composite {

    interface SummaryPanelUiBinder extends UiBinder<Widget, SummaryPanel> {
    }

    private static SummaryPanelUiBinder ourUiBinder = GWT.create(SummaryPanelUiBinder.class);

    @UiField
    VerticalPanel pane;

    private SessionPanel sessionPanel;

    private Set<SessionDataDto> active = Collections.EMPTY_SET;

    public SummaryPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void updateSessions(Set<SessionDataDto> chosenSessions){
        if (chosenSessions.size() == 1){
            //show session summary
            pane.clear();
            sessionPanel = new SessionSummaryPanel(chosenSessions.iterator().next());
            pane.add((SessionSummaryPanel)sessionPanel);
        }else{
            if (chosenSessions.size() > 1){
                //show sessions somparison
                pane.clear();
                sessionPanel = new SessionComparisonPanel(chosenSessions);
                pane.add((SessionComparisonPanel)sessionPanel);
            }else{
                pane.clear();
            }
        }
        active = chosenSessions;
    }

    public void updateTests(Set<TaskDataDto> tests){
        sessionPanel.update(tests);
    }

    public void addTest(TaskDataDto test){
        sessionPanel.addTest(test);
    }

    public void removeTest(TaskDataDto test){
        sessionPanel.removeTest(test);
    }

    public void showMetric(TaskDataDto test, String metricName){
        sessionPanel.showMetric(test, metricName);
    }

    public void hideMetric(TaskDataDto test, String metricName){
        sessionPanel.hideMetric(test, metricName);
    }

    public void showMetric(String metricName){
        sessionPanel.showMetric(metricName);
    }

    public void hideMetric(String metricName){
        sessionPanel.hideMetric(metricName);
    }

    public Set<SessionDataDto> getSessions(){
        return active;
    }

    public Set<String> getSessionIds(){
        HashSet<String> ids = new HashSet<String>(active.size());
        for (SessionDataDto session : active){
            ids.add(session.getSessionId());
        }
        return ids;
    }
}