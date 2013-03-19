package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.TaskDataService;
import com.griddynamics.jagger.webclient.client.TaskDataServiceAsync;
import com.griddynamics.jagger.webclient.client.WorkloadTaskDataService;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 14.03.13
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class SessionPanel extends VerticalPanel{

    public SessionPanel(SessionDataDto sessionData){
        initStyle();
        initData(sessionData);
    }

    private void initStyle(){
    }

    private void initData(SessionDataDto sessionData){

        Label name = new Label("Session #"+sessionData.getSessionId());
        name.setStyleName("sessionNameHeader");
        add(name);

        Grid summaryGrid = new Grid(6, 2);
        summaryGrid.getElement().getStyle().setProperty("margin", "40px");

        Label sessionLabel = new Label("Session id");
        sessionLabel.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        summaryGrid.setWidget(0, 0, sessionLabel);
        summaryGrid.setWidget(0, 1, new Label(sessionData.getSessionId()));

        Label startTimeLabel = new Label("Session start time");
        startTimeLabel.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        summaryGrid.setWidget(1, 0, startTimeLabel);
        summaryGrid.setWidget(1, 1, new Label(sessionData.getStartDate()));

        Label endTimeLabel = new Label("Session end time");
        endTimeLabel.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        summaryGrid.setWidget(2, 0, endTimeLabel);
        summaryGrid.setWidget(2, 1, new Label(sessionData.getEndDate()));

        Label numberTasksLabel = new Label("Number of tasks");
        numberTasksLabel.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        summaryGrid.setWidget(3, 0, numberTasksLabel);
        summaryGrid.setWidget(3, 1, new Label(Integer.toString(sessionData.getTasksExecuted())));

        Label failuresLabel = new Label("Number of task failures");
        failuresLabel.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        summaryGrid.setWidget(4, 0, failuresLabel);
        summaryGrid.setWidget(4, 1, new Label(Integer.toString(sessionData.getTasksFailed())));

        Label activeKernels = new Label("Number of active kernels");
        activeKernels.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        summaryGrid.setWidget(5, 0, activeKernels);
        summaryGrid.setWidget(5, 1, new Label(Integer.toString(sessionData.getActiveKernelsCount())));

        add(summaryGrid);

        Label testPanelName = new Label("Tests");
        testPanelName.addStyleName("testNameHeader");
        add(testPanelName);

        Panel testPanel = createTestPanel(sessionData.getSessionId());
        if (testPanel != null){
            add(testPanel);
        }else{
            Label noTestLabel = new Label("There are no tests ... ");
            add(noTestLabel);
        }
    }

    private VerticalPanel createTestPanel(String sessionId){
        final VerticalPanel testPanel = new VerticalPanel();
        WorkloadTaskDataService.Async.getInstance().getWorkloadTaskData(sessionId, new AsyncCallback<List<WorkloadTaskDataDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(List<WorkloadTaskDataDto> result) {
                for (WorkloadTaskDataDto data : result){
                    testPanel.add(new TestPanel(data));
                }
            }
        });

        return testPanel;
    }
}
