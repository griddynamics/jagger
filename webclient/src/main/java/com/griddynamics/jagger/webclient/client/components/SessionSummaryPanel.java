package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.WorkloadTaskDataService;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 14.03.13
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class SessionSummaryPanel extends VerticalPanel{

    private HashMap<String, WorkloadTaskDataDto> cache = new HashMap<String, WorkloadTaskDataDto>();
    private final Panel testPanel = new VerticalPanel();

    public SessionSummaryPanel(SessionDataDto sessionData){
        initStyle();
        initData(sessionData);
    }

    private void initStyle(){
        addStyleName(JaggerResources.INSTANCE.css().summaryPanel());
        setWidth("1350px");
    }

    private void initData(SessionDataDto sessionData){
        Label name = new Label("Session #"+sessionData.getSessionId());
        name.setStyleName(JaggerResources.INSTANCE.css().sessionNameHeader());
        add(name);

        Grid summaryGrid = new Grid(7, 2);
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

        Label commentTitle = new Label("Comment");
        commentTitle.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        summaryGrid.setWidget(6, 0, commentTitle);
        summaryGrid.setWidget(6, 1, new HTMLPanel(sessionData.getComment()));

        add(summaryGrid);

        Label testPanelName = new Label("Tests");
        testPanelName.addStyleName(JaggerResources.INSTANCE.css().testNameHeader());
        add(testPanelName);

        add(testPanel);
    }

    public void updateTests(Collection<TaskDataDto> tests) {

        Set<TaskDataDto> testsToLoad = new HashSet<TaskDataDto>(tests.size());
        final List<WorkloadTaskDataDto> testsLoaded = new ArrayList<WorkloadTaskDataDto>(tests.size());

        for (TaskDataDto taskDataDto : tests){
            if (!cache.containsKey(taskDataDto.getTaskName())){
                testsToLoad.add(taskDataDto);
            }else{
                testsLoaded.add(cache.get(taskDataDto.getTaskName()));
            }
        }

        WorkloadTaskDataService.Async.getInstance().getWorkloadTaskData(testsToLoad, new AsyncCallback<Set<WorkloadTaskDataDto>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(Set<WorkloadTaskDataDto> workloadTaskDataDtos) {
                for (WorkloadTaskDataDto workloadTaskDataDto : workloadTaskDataDtos){
                    testsLoaded.add(workloadTaskDataDto);
                    cache.put(workloadTaskDataDto.getName(), workloadTaskDataDto);
                }

                Collections.sort(testsLoaded, new Comparator<WorkloadTaskDataDto>() {
                    @Override
                    public int compare(WorkloadTaskDataDto o1, WorkloadTaskDataDto o2) {
                        Integer rank1 = Integer.parseInt(o1.getTaskId().substring(5));
                        Integer rank2 = Integer.parseInt(o2.getTaskId().substring(5));
                        return rank1.compareTo(rank2);
                    }
                });

                testPanel.clear();
                for (WorkloadTaskDataDto workloadTaskDataDto : testsLoaded){
                    testPanel.add(new TestPanel(workloadTaskDataDto));
                }
            }
        });
    }
}
