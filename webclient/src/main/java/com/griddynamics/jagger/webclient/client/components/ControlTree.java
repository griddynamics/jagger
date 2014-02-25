package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.griddynamics.jagger.webclient.client.components.control.CheckHandlerMap;
import com.griddynamics.jagger.webclient.client.components.control.model.*;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.SessionPlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Extension of com.sencha.gxt.widget.core.client.tree.Tree allows to disable tree items.
 * + no icons.
 *
 *
 * @param <C> cell data type
 */
public class ControlTree<C> extends Tree <AbstractIdentifyNode, C> {


    /**
     * boolean disabled tree or not
     * uses for canceling events
     */
    private boolean disabled;


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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    {

        this.addBeforeExpandHandler(new BeforeExpandItemEvent.BeforeExpandItemHandler<AbstractIdentifyNode>() {
            @Override
            public void onBeforeExpand(BeforeExpandItemEvent<AbstractIdentifyNode> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addBeforeCollapseHandler(new BeforeCollapseItemEvent.BeforeCollapseItemHandler<AbstractIdentifyNode>() {
            @Override
            public void onBeforeCollapse(BeforeCollapseItemEvent<AbstractIdentifyNode> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addBeforeCheckChangeHandler(new BeforeCheckChangeEvent.BeforeCheckChangeHandler<AbstractIdentifyNode>() {
            @Override
            public void onBeforeCheckChange(BeforeCheckChangeEvent<AbstractIdentifyNode> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addCheckChangeHandler(new CheckChangeEvent.CheckChangeHandler<AbstractIdentifyNode>() {
            @Override
            public void onCheckChange(CheckChangeEvent<AbstractIdentifyNode> event) {

                tree.disableEvents();
                    check(event.getItem(), event.getChecked());
            }

            private void check(AbstractIdentifyNode item, CheckState state) {
                checkSubTree(item, state);
                if (state.equals(CheckState.CHECKED)) {
                    checkParent(item);
                    tree.setExpanded(item, true, false);
                } else {
                    unCheckParent(item);
                }

                tree.enableEvents();
                CheckHandlerMap.getHandler(item.getClass()).onCheckChange(new CheckChangeEvent(item, state));
            }

            private Tree<AbstractIdentifyNode, C> tree = ControlTree.this;
            private TreeStore<AbstractIdentifyNode> treeStore = ControlTree.this.getStore();



            private void unCheckParent(AbstractIdentifyNode item) {
                AbstractIdentifyNode parent = treeStore.getParent(item);
                if (parent == null) return;
                boolean hasChecked = false;
                for (AbstractIdentifyNode ch : treeStore.getChildren(parent)) {
                    if (tree.getChecked(ch).equals(CheckState.CHECKED) || tree.getChecked(ch).equals(CheckState.PARTIAL)) {
                        tree.setChecked(parent, CheckState.PARTIAL);
                        hasChecked = true;
                        break;
                    }
                }
                if (!hasChecked)
                    tree.setChecked(parent, CheckState.UNCHECKED);

                unCheckParent(parent);
            }


        });
    }

    private void checkSubTree(AbstractIdentifyNode item, CheckState state) {
        if (store.hasChildren(item))
            for (AbstractIdentifyNode child : store.getChildren(item)) {
                setChecked(child, state);
                checkSubTree(child, state);
            }
    }

    public void checkParent(AbstractIdentifyNode item) {
        AbstractIdentifyNode parent = store.getParent(item);
        if (parent == null) return;

        boolean hasUnchecked = false;

        for (AbstractIdentifyNode ch : store.getChildren(parent)) {
            if (!isChecked(ch) || CheckState.PARTIAL.equals(getChecked(ch))) {
                setChecked(parent, CheckState.PARTIAL);
                hasUnchecked = true;
                break;
            }
        }

        if (!hasUnchecked)
            setChecked(parent, CheckState.CHECKED);

        checkParent(parent);
    }



    public void setCheckedWithParent (AbstractIdentifyNode item) {
        setChecked(item, Tree.CheckState.CHECKED);
        checkSubTree(item, Tree.CheckState.CHECKED);
        checkParent(item);
    }


    public void setCheckedExpandedWithParent (AbstractIdentifyNode item) {
        setChecked(item, Tree.CheckState.CHECKED);
        checkSubTree(item, Tree.CheckState.CHECKED);
        checkParent(item);
        setExpanded(item, true, false);
    }

    @UiConstructor
    public ControlTree(TreeStore<AbstractIdentifyNode> store, ValueProvider<? super AbstractIdentifyNode, C> valueProvider) {
        super(store, valueProvider);
    }

    @Override
    protected ImageResource calculateIconStyle(AbstractIdentifyNode model) {
        return null;
    }

    /**
     * disable ability to check/unCheck, collapse/expand actions
     */
    @Override
    public void disable() {
        super.disable();
        setDisabled(true);
    }

    /**
     * disable ability to check/unCheck, collapse/expand actions
     */
    @Override
    public void enable() {
        super.enable();
        setDisabled(false);
    }

    public void enable(boolean enableTree) {
        if (enableTree) {
            enable();
        }
    }

    public void enableTree() {
        this.enable();
        this.enableEvents();
    }

    public void clearStore() {
        store.clear();
    }

    /**
     * results should be chosen from both Summary and Details subtree
     * @return List<TaskDataDto> to use the same link creator.
     */
    public Set<TaskDataDto> getSelectedTests() {

        Set<TaskDataDto> resultSet = new HashSet<TaskDataDto>();
        for (TestNode testNode : rootNode.getSummary().getTests()) {
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
    public Set<MetricNameDto> getCheckedMetrics() {

        Set<MetricNameDto> resultSet = new HashSet<MetricNameDto>();
        for (TestNode test : rootNode.getSummary().getTests()) {
            resultSet.addAll(getCheckedMetrics(test));
        }
        return resultSet;
    }


    /**
     * @param testNode /
     * @return MetricNameDto from 'TestNode' test
     */
    public Set<MetricNameDto> getCheckedMetrics(TestNode testNode) {

        Set<MetricNameDto> resultSet = new HashSet<MetricNameDto>();
            for (MetricNode metricNode : testNode.getMetrics()) {
                if (isChecked(metricNode)) {
                    resultSet.add(metricNode.getMetricNameDto());
                }
            }
        return resultSet;
    }


    public TestNode findTestNode(TaskDataDto taskDataDto) {

        for (TestNode test : rootNode.getSummary().getTests()) {
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
    public Set<MetricNameDto> getCheckedPlots() {

        Set<MetricNameDto> resultSet = new HashSet<MetricNameDto>();
        for (TestDetailsNode test : rootNode.getDetailsNode().getTests()) {
            for (PlotNode plotNode : test.getMetrics()) {
                if (isChecked(plotNode)) {
                    resultSet.add(plotNode.getMetricNameDto());
                }
            }
            for (MonitoringPlotNode monitoringPlotNode : test.getMonitoringPlots()) {
                for (PlotNode plotNode : monitoringPlotNode.getPlots()) {
                    if (isChecked(plotNode)) {
                        resultSet.add(plotNode.getMetricNameDto());
                    }
                }
            }
        }
        return resultSet;
    }


    /**
     * @return checked MetricNameDto for session scope plots
     */
    public Set<SessionPlotNameDto> getCheckedSessionScopePlots() {
        if (rootNode.getDetailsNode().getSessionScopePlotsNode() == null) {
            return Collections.EMPTY_SET;
        }
        Set<SessionPlotNameDto> resultSet = new HashSet<SessionPlotNameDto>();
        for (MonitoringSessionScopePlotNode mPlotNode : rootNode.getDetailsNode().getSessionScopePlotsNode().getPlots()) {
            for (SessionPlotNode plotNode: mPlotNode.getPlots()) {
                if (isChecked(plotNode)) {
                    resultSet.add(plotNode.getPlotNameDto());
                }
            }
        }
        return resultSet;
    }


    /**
     * return false if CheckState = Tree.CheckState.UNCHECKED
     *        true in other cases
     * @param model tree model
     * @return bool
     */
    public boolean isChosen(AbstractIdentifyNode model) {
        return !CheckState.UNCHECKED.equals(getChecked(model));
    }

    public void onSummaryTrendsTab() {
        onMetricsTab(false);
    }

    public void onMetricsTab() {
        onMetricsTab(true);
    }

    private void onMetricsTab(boolean boo) {
        if (rootNode != null) {
            setExpanded(rootNode.getSummary(), !boo);
            setExpanded(rootNode.getDetailsNode(), boo);
        }
    }
}