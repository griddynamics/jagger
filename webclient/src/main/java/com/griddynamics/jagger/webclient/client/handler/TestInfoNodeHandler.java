package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.TestInfoNode;
import com.griddynamics.jagger.dbapi.model.TestNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class TestInfoNodeHandler extends TreeAwareHandler<TestInfoNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<TestInfoNode> event) {
        TestNode testNode = (TestNode)tree.getStore().getParent(event.getItem());

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            testInfoFetcher.fetchTestInfo(Arrays.asList(testNode.getTaskDataDto()), true);
        } else {
            sessionComparisonPanel.removeTestInfo(testNode.getTaskDataDto());
        }

        tree.enableTree();
    }
}
