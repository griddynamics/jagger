package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.SessionInfoNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class SessionInfoNodeHandler extends TreeAwareHandler<SessionInfoNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<SessionInfoNode> event) {

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionComparisonPanel.addSessionInfo();
        } else {
            sessionComparisonPanel.removeSessionInfo();
        }

        tree.enableTree();
    }
}
