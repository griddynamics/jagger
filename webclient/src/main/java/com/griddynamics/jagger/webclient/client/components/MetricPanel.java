package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

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

    @UiField(provided = true)
    CellTree tree;

    private final ListDataProvider<TaskDataDto> provider = new ListDataProvider<TaskDataDto>(Arrays.asList(MetricModel.NO_METRIC_TO_SHOW));
    private final MultiSelectionModel<MetricNameDto> selectionModel = new MultiSelectionModel<MetricNameDto>();

    public MultiSelectionModel<MetricNameDto> getSelectionModel() {
        return selectionModel;
    }

    public MetricPanel() {
        CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
        tree = new CellTree(new MetricModel(selectionModel, provider), null, res);
        tree.addStyleName(JaggerResources.INSTANCE.css().taskDetailsTree());

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void updateTests(Collection<TaskDataDto> tests){

        selectionModel.clear();
        provider.setList(Arrays.asList((TaskDataDto)null));

        boolean manySessions = false;
        for (TaskDataDto test : tests){
            if (test.getIds().size() > 1){
                manySessions = true;
                break;
            }
        }

        if (tests.size()==0 || !manySessions){
            //nothing to show
            provider.setList(Arrays.asList(MetricModel.NO_METRIC_TO_SHOW));
            return;
        }
        provider.setList(new ArrayList<TaskDataDto>(tests));
    }

    public Set<MetricNameDto> getSelected(){
        return selectionModel.getSelectedSet();
    }

    public void setSelected(MetricNameDto metric){
        selectionModel.setSelected(metric, true);
    }

    public void addSelectionListener(SelectionChangeEvent.Handler handler){
        selectionModel.addSelectionChangeHandler(handler);
    }
}