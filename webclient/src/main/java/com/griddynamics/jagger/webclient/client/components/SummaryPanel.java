package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.*;

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

    private SessionSummaryPanel sessionSummaryPanel;
    private SessionComparisonPanel sessionComparisonPanel;

    private Set<SessionDataDto> active = Collections.EMPTY_SET;

    public SummaryPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public HashMap<MetricNameDto, MetricDto> getCachedMetrics() {
        return sessionComparisonPanel.getCachedMetrics();
    }

    public SessionComparisonPanel getSessionComparisonPanel() {
        return sessionComparisonPanel;
    }

    public void updateSessions(Set<SessionDataDto> chosenSessions){
        if (chosenSessions.size() == 1){
            //show session summary
            pane.clear();
            sessionSummaryPanel = new SessionSummaryPanel(chosenSessions.iterator().next());
            sessionComparisonPanel = null;
            pane.add(sessionSummaryPanel);
        }else{
            if (chosenSessions.size() > 1){
                //show sessions comparison
                pane.clear();
                sessionComparisonPanel = new SessionComparisonPanel(chosenSessions);
                sessionSummaryPanel = null;
                pane.add(sessionComparisonPanel);
            }else{
                pane.clear();
            }
        }
        active = chosenSessions;
    }


    public void updateTests(Collection<TaskDataDto> tests) {
        if(sessionSummaryPanel != null){
            sessionSummaryPanel.updateTests(tests);
        }
    }

    public Set<String> getSessionIds(){
        HashSet<String> ids = new HashSet<String>(active.size());
        for (SessionDataDto session : active){
            ids.add(session.getSessionId());
        }
        return ids;
    }
}