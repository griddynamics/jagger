package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.dbapi.dto.SummaryIntegratedDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.WebClientProperties;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;

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

    private SessionComparisonPanel sessionComparisonPanel;

    private Set<SessionDataDto> active = Collections.EMPTY_SET;

    public SummaryPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public HashMap<MetricNode, SummaryIntegratedDto> getCachedMetrics() {
        return sessionComparisonPanel.getCachedMetrics();
    }

    public SessionComparisonPanel getSessionComparisonPanel() {
        return sessionComparisonPanel;
    }

    public void updateSessions(Set<SessionDataDto> chosenSessions, WebClientProperties webClientProperties, DateTimeFormat dateFormatter) {
        if (chosenSessions.size() > 0){
            //show sessions comparison
            pane.clear();
            sessionComparisonPanel = new SessionComparisonPanel(chosenSessions, pane.getOffsetWidth(), webClientProperties, dateFormatter);
            pane.add(sessionComparisonPanel);
        }else{
            pane.clear();
        }
        active = chosenSessions;
    }

    public Set<String> getSessionIds(){
        HashSet<String> ids = new HashSet<String>(active.size());
        for (SessionDataDto session : active){
            ids.add(session.getSessionId());
        }
        return ids;
    }
}