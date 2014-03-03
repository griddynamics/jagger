package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.SummaryNode;
import com.griddynamics.jagger.webclient.client.components.control.model.TestNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;


/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class SummaryNodeHandler extends TreeAwareHandler<SummaryNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<SummaryNode> event) {
        SummaryNode summaryNode = event.getItem();

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionComparisonPanel.addSessionInfo();
            for (TestNode testNode : summaryNode.getTests()) {
                testInfoFetcher.fetchTestInfo(testNode.getTaskDataDto(), false);
            }
        } else {
            sessionComparisonPanel.removeSessionInfo();
            for (TestNode testNode : summaryNode.getTests()) {
                sessionComparisonPanel.removeTestInfo(testNode.getTaskDataDto());
            }
        }

        metricFetcher.fetchMetrics(tree.getCheckedMetrics(), true);
    }
}
