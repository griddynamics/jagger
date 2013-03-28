package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;

import java.util.Collections;
import java.util.HashMap;
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

    private HashMap<SessionDataDto, SessionPanel> loaded = new HashMap<SessionDataDto, SessionPanel>();
    private Set<SessionDataDto> active = Collections.EMPTY_SET;

    public SummaryPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void update(Set<SessionDataDto> chosenSessions){
        active = chosenSessions;
        removeOld(chosenSessions);
        addNew(chosenSessions);
    }

    protected void removeOld(Set<SessionDataDto> chosenSessions){
        for (SessionDataDto session : loaded.keySet()){
            //hide remove session
            if (!chosenSessions.contains(session)){
                loaded.get(session).setVisible(false);
            }
        }
    }

    protected void addNew(Set<SessionDataDto> chosenSessions){
        for (SessionDataDto session : chosenSessions){
            if (loaded.containsKey(session)){
                loaded.get(session).setVisible(true);
            }else{
                SessionPanel panel = new SessionPanel(session);
                loaded.put(session, panel);
                pane.add(panel);
            }
        }
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