package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 14.03.13
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */
public class TestPanel extends HorizontalPanel {
    public TestPanel(WorkloadTaskDataDto taskData){
        initStyles();
        loadData(taskData);
    }

    private void initStyles(){
        getElement().getStyle().setProperty("margin", "10px");
    }

    private void loadData(WorkloadTaskDataDto taskData){

        Grid testGrid = new Grid(2, 5);

        //style
        testGrid.setCellSpacing(20);
        testGrid.getRowFormatter().setStyleName(0, "testHeader");

        testGrid.setWidth("1324px");
        HTMLTable.CellFormatter formatter = testGrid.getCellFormatter();

        //edit
        formatter.setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
        formatter.setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        formatter.setWidth(1, 0, "300px");

        formatter.setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_CENTER);
        formatter.setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
        formatter.setWidth(1, 1, "180px");

        formatter.setHorizontalAlignment(1, 2, HasHorizontalAlignment.ALIGN_CENTER);
        formatter.setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);
        formatter.setWidth(1, 2, "150px");

        formatter.setHorizontalAlignment(1, 3, HasHorizontalAlignment.ALIGN_CENTER);
        formatter.setVerticalAlignment(1, 3, HasVerticalAlignment.ALIGN_TOP);
        formatter.setWidth(1, 3, "200px");

        //headers
        Label scenarioNameLabel = new Label("Scenario name");
        testGrid.setWidget(0, 0 , scenarioNameLabel);

        Label clockLabel = new Label("Clock");
        testGrid.setWidget(0, 1, clockLabel);

        Label tpsLabel = new Label("Throughput (TPS)");
        testGrid.setWidget(0, 2, tpsLabel);

        Label latencyLabel = new Label("Latency");
        testGrid.setWidget(0, 3, latencyLabel);

        Label detailsLabel = new Label("Details");
        testGrid.setWidget(0, 4, detailsLabel);

        //info
        Label scenarioName = new Label(taskData.getName());
        testGrid.setWidget(1, 0, scenarioName);

        testGrid.setWidget(1, 1, new Label(taskData.getClock()));

        testGrid.setWidget(1, 2, new Label(taskData.getThroughput().toString()));

        testGrid.setWidget(1, 3, getLatency(taskData));

        testGrid.setWidget(1, 4, getDetailts(taskData));

        add(testGrid);
    }

    private Widget getLatency(WorkloadTaskDataDto taskData){
        VerticalPanel pane = new VerticalPanel();
        List<String> latency = taskData.getLatency();
        if (!latency.isEmpty()){
            for (String latencyValue : latency){
                pane.add(new Label(latencyValue));
            }
        }
        return pane;
    }

    private Widget getDetailts(WorkloadTaskDataDto taskData){

        Map<String, String> customMetrics = taskData.getCustomMetrics();
        int numOfCustomMetrics = 0;
        if (customMetrics != null) {
            numOfCustomMetrics = customMetrics.size();
        }
        Grid detailsGrid = new Grid(7 + numOfCustomMetrics, 2);

        Label samples = new Label("Samples");
        samples.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        detailsGrid.setWidget(0, 0, samples);
        detailsGrid.setWidget(0, 1, new Label(taskData.getSamples().toString()));

        Label termination = new Label("Termination strategy");
        termination.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        detailsGrid.setWidget(1, 0, termination);
        detailsGrid.setWidget(1, 1, new Label(taskData.getTermination()));

        Label duration = new Label("Duration");
        duration.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        detailsGrid.setWidget(2, 0, duration);
        detailsGrid.setWidget(2, 1, new Label(taskData.getDuration()));

        Label avgLatency = new Label("Average Latency (sec)");
        avgLatency.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        detailsGrid.setWidget(3, 0, avgLatency);
        detailsGrid.setWidget(3, 1, new Label(taskData.getAvgLatency().toString()));

        Label stdDev = new Label("StdDev Latency (sec)");
        stdDev.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        detailsGrid.setWidget(4, 0, stdDev);
        detailsGrid.setWidget(4, 1, new Label(taskData.getStdDevLatency().toString()));

        Label failuresCount = new Label("Number of failures");
        failuresCount.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        detailsGrid.setWidget(5, 0, failuresCount);
        detailsGrid.setWidget(5, 1, new Label(taskData.getFailuresCount().toString()));

        Label succesRate = new Label("Success Rate");
        succesRate.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
        detailsGrid.setWidget(6, 0, succesRate);
        detailsGrid.setWidget(6, 1, new Label(taskData.getSuccessRate().toString()));

        if (numOfCustomMetrics > 0 ){
            numOfCustomMetrics = 0;
            for (Map.Entry<String, String> entry : customMetrics.entrySet()) {
                Label label = new Label(entry.getKey());
                label.getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
                detailsGrid.setWidget(7 + numOfCustomMetrics, 0, label);
                detailsGrid.setWidget(7 + numOfCustomMetrics, 1, new Label(entry.getValue()));
                numOfCustomMetrics ++;
            }
        }

        return detailsGrid;
    }
}
