package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.TestNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class TestNodeHandler extends TreeAwareHandler<TestNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<TestNode> event) {

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            testInfoFetcher.fetchTestInfo(Arrays.asList(event.getItem().getTaskDataDto()), false);
        } else {
            sessionComparisonPanel.removeTestInfo(event.getItem().getTaskDataDto());
        }
        metricFetcher.fetchMetrics(tree.getCheckedMetrics(), true);
    }
}
