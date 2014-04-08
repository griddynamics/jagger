package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.TestDetailsNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class TestDetailsNodeHandler extends TreeAwareHandler<TestDetailsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<TestDetailsNode> event) {

        TestDetailsNode testNode = event.getItem();
        Set<MetricNode> dtos = new LinkedHashSet<MetricNode>();
        dtos.addAll(testNode.getMetrics());

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            testPlotFetcher.fetchPlots(dtos);
        } else {
            testPlotFetcher.removePlots(dtos);
        }
    }
}

