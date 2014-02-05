package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.dto.NodeInfoDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

import java.util.List;
import java.util.Set;

public class NodesPanel extends Composite {

    interface BoxesPanelUiBinder extends UiBinder<Widget, NodesPanel> {
    }

    private static BoxesPanelUiBinder ourUiBinder = GWT.create(BoxesPanelUiBinder.class);

    @UiField
    VerticalPanel pane;

    private NodesTablePanel boxesTablePanel;
    private long currentSessionId = -1;
    private Label label = new Label("No info about nodes available");

    public NodesPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setLabel();
        cleanBoxes();
    }

    public NodesTablePanel getBoxesTablePanel() {
        return boxesTablePanel;
    }

    public CheckUpdateResult doYouNeedToUpdate(Set<SessionDataDto> chosenSessions) {
        boolean result = true;
        long sessionId = -1;

        if (chosenSessions.size() > 0) {
            sessionId = getLowestSessionId(chosenSessions);
            if (sessionId != currentSessionId)
                result = true;
            else
                result = false;
        }

        return new CheckUpdateResult(result,sessionId);
    }

    public void cleanBoxes() {
        pane.clear();
        pane.add(getLabel());
        currentSessionId = -1;
    }

    public void updateBoxes(long sessionId, List<NodeInfoDto> nodeInfoDtoList){
        pane.clear();
        if (nodeInfoDtoList.size() > 0) {
            boxesTablePanel = new NodesTablePanel(sessionId, nodeInfoDtoList, pane.getOffsetWidth());
            pane.add(boxesTablePanel);
            currentSessionId = sessionId;
        }
        else {
            pane.add(getLabel());
        }
    }

    public class CheckUpdateResult {
        boolean needToUpdate = true;
        long sessionId = -1;

        public CheckUpdateResult(boolean needToUpdate, long sessionId) {
            this.needToUpdate=needToUpdate;
            this.sessionId=sessionId;
        }

        public boolean isNeedToUpdate() {
            return needToUpdate;
        }

        public long getSessionId() {
            return sessionId;
        }
    }

    private long getLowestSessionId(Set<SessionDataDto> chosenSessions) {
        long sessionId = -1;
        for (SessionDataDto session : chosenSessions) {
            if ((sessionId == -1) || (Long.parseLong(session.getSessionId()) < sessionId))
                sessionId = Long.parseLong(session.getSessionId());
        }
        return sessionId;
    }

    private void setLabel() {
        label.setHorizontalAlignment(HasHorizontalAlignment.HorizontalAlignmentConstant.startOf(HasDirection.Direction.DEFAULT));
        label.setStylePrimaryName(JaggerResources.INSTANCE.css().centered());
        label.setHeight("100%");
    }
    private Label getLabel() {
        return label;
    }
}