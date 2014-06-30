package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.uibinder.client.UiConstructor;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.components.control.CheckHandlerMap;
import com.griddynamics.jagger.dbapi.model.*;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.HashSet;
import java.util.Set;


/**
 * Extension of com.sencha.gxt.widget.core.client.tree.Tree allows to disable tree items.
 * + no icons.
 *
 *
 * @param <C> cell data type
 */
public class ControlTree<C> extends AbstractTree<AbstractIdentifyNode, C> {

    /**
     * Model helps to fetch all data at once
     */
    private RootNode rootNode;

    public RootNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(RootNode rootNode) {
        this.rootNode = rootNode;
    }


    @Override
    protected void check(AbstractIdentifyNode item, CheckState state) {
        CheckHandlerMap.getHandler(item.getClass())
                .onCheckChange(new CheckChangeEvent(item, state));
    }


    public void setCheckedWithParent (AbstractIdentifyNode item) {
        setChecked(item, Tree.CheckState.CHECKED);
        setStateToSubTree(item, Tree.CheckState.CHECKED);
        checkParent(item);
    }


    public void setCheckedExpandedWithParent (AbstractIdentifyNode item) {
        setChecked(item, Tree.CheckState.CHECKED);
        setStateToSubTree(item, Tree.CheckState.CHECKED);
        checkParent(item);
        setExpanded(item, true, false);
    }

    @UiConstructor
    public ControlTree(TreeStore<AbstractIdentifyNode> store, ValueProvider<? super AbstractIdentifyNode, C> valueProvider) {
        super(store, valueProvider);
    }

    /**
     * results should be chosen from both Summary and Details subtree
     * @return List<TaskDataDto> to use the same link creator.
     */
    public Set<TaskDataDto> getSelectedTests() {

        Set<TaskDataDto> resultSet = new HashSet<TaskDataDto>();
        for (TestNode testNode : rootNode.getSummaryNode().getTests()) {
            if (isChosen(testNode)) {
                resultSet.add(testNode.getTaskDataDto());
            }
        }
        for (TestDetailsNode testNode : rootNode.getDetailsNode().getTests()) {
            if (isChosen(testNode)) {
                resultSet.add(testNode.getTaskDataDto());
            }
        }

        return resultSet;
    }


    /**
     * @return MetricNameDto from all Tests
     */
    public Set<MetricNode> getCheckedMetrics() {

        Set<MetricNode> resultSet = new HashSet<MetricNode>();
        for (TestNode test : rootNode.getSummaryNode().getTests()) {
            resultSet.addAll(getCheckedMetrics(test));
        }
        return resultSet;
    }


    /**
     * @param testNode /
     * @return MetricNameDto from 'TestNode' test
     */
    public Set<MetricNode> getCheckedMetrics(TestNode testNode) {

        Set<MetricNode> resultSet = new HashSet<MetricNode>();
        for (MetricNode metricNode : testNode.getMetrics()) {
            if (isChecked(metricNode)) {
                resultSet.add(metricNode);
            }
        }
        return resultSet;
    }


    public TestNode findTestNode(TaskDataDto taskDataDto) {

        for (TestNode test : rootNode.getSummaryNode().getTests()) {
            if (test.getTaskDataDto().equals(taskDataDto)) {
                return test;
            }
        }

        new ExceptionPanel("can not find TestNode with: " + taskDataDto);
        return null;
    }


    /**
     * @return checked MetricNameDto from all Tests
     */
    public Set<MetricNode> getCheckedPlots() {

        Set<MetricNode> resultSet = new HashSet<MetricNode>();
        for (TestDetailsNode test : rootNode.getDetailsNode().getTests()) {
            for (PlotNode plotNode : test.getMetrics()) {
                if (isChecked(plotNode)) {
                    resultSet.add(plotNode);
                }
            }
        }
        return resultSet;
    }


    public void onSummaryTrendsTab() {
        onMetricsTab(false);
    }

    public void onMetricsTab() {
        onMetricsTab(true);
    }

    private void onMetricsTab(boolean boo) {
        if (rootNode != null) {
            setExpanded(rootNode.getSummaryNode(), !boo);
            setExpanded(rootNode.getDetailsNode(), boo);
        }
    }
}