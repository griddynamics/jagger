package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 05.04.13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class MetricPanel extends Composite {
    interface MetricPanelUiBinder extends UiBinder<Widget, MetricPanel> {
    }

    private static MetricPanelUiBinder ourUiBinder = GWT.create(MetricPanelUiBinder.class);

    @UiField
    VerticalPanel pane;

    final TreeGrid metricTreeGrid = new TreeGrid();

    public MetricPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

        MetricTreeModel tree = new MetricTreeModel(defaultData);

        metricTreeGrid.setWidth(400);
        metricTreeGrid.setHeight(200);
        metricTreeGrid.setShowDropIcons(false);
        metricTreeGrid.setShowOpenIcons(false);
        metricTreeGrid.setClosedIconSuffix("");
        metricTreeGrid.setData(tree);

        metricTreeGrid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
        metricTreeGrid.setShowSelectedStyle(false);
        metricTreeGrid.setShowPartialSelection(true);
        metricTreeGrid.setCascadeSelection(true);

        metricTreeGrid.addDrawHandler(new DrawHandler() {
            public void onDraw(DrawEvent event) {
                metricTreeGrid.selectAllRecords();
            }
        });

        pane.add(metricTreeGrid);
    }

    public void updateTests(Set<TaskDataDto> tests){
        final Set<TaskDataDto> temp = tests;
        MetricDataService.Async.getInstance().getMetricsNames(tests, new AsyncCallback<Set<MetricNameDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(Set<MetricNameDto> result) {
                ArrayList<MetricTreeNode> nodes = new ArrayList<MetricTreeNode>(result.size());

                //init tests nodes
                for (TaskDataDto test : temp){
                    nodes.add(new MetricTreeNode(test.getTaskName(), null));
                }

                //init metrics nodes
                for (MetricNameDto metricName : result)
                    nodes.add(new MetricTreeNode(metricName.getTaskName(), metricName.getName()));
                MetricTreeModel model = new MetricTreeModel(nodes);
                metricTreeGrid.setData(model);
            }
        });
    }

    public void addSelectionListener(SelectionChangedHandler listener){
        metricTreeGrid.addSelectionChangedHandler(listener);
    }

    public TreeNode[] defaultData = new TreeNode[] {
        new MetricTreeNode("Select tests", null),
    };


    private String root = "root";
    private String testNameAttribute = "testName";
    private String metricNameAttribute = "metricName";
    private String nodeId = "id";
    private String nodeParentId = "parentId";
    private String valueAttribute = "value";

    private class MetricTreeModel extends Tree{

        public MetricTreeModel(Collection<? extends TreeNode> data){
            init();
            TreeNode[] mas = data.toArray(new TreeNode[]{});
            setData(mas);
        }

        public MetricTreeModel(TreeNode[] data){
            init();
            setData(data);
        }

        private void init(){
            setModelType(TreeModelType.PARENT);

            setNameProperty(valueAttribute);
            setIdField(nodeId);
            setParentIdField(testNameAttribute);

            setRootValue(root);
        }
    }

    private class MetricTreeNode extends TreeNode {
        public MetricTreeNode(String testName, String metricName) {
            setAttribute(nodeId, testName+(metricName == null ? "" : metricName));
            setAttribute(testNameAttribute, testName);
            setAttribute(metricNameAttribute, metricName);
            if (metricName == null){
                setAttribute(valueAttribute, testName);
                setAttribute(nodeParentId, root);
            }else{
                setAttribute(valueAttribute, metricName);
                setAttribute(nodeParentId, testName);
            }
        }
    }




}