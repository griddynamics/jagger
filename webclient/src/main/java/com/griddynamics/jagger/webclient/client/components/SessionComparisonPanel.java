package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 26.03.13
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class SessionComparisonPanel extends VerticalPanel implements SessionPanel{

    private Label title = new Label();
    private ListGrid grid = new ListGrid();

    private HashMap<MetricNameDto, MetricDto> cache;

    public SessionComparisonPanel(Set<SessionDataDto> chosenSessions){
        init(chosenSessions);
    }

    private void init(Set<SessionDataDto> chosenSessions){
        add(title);

        //init title
        title.setStyleName("sessionNameHeader");
        title.setWidth("1350px");
        StringBuilder titleText = new StringBuilder("Comparison of sessions : ");
        for (SessionDataDto session : chosenSessions){
            titleText.append(session.getSessionId()+",");
        }
        String titleString = titleText.toString();
        title.setText(titleString.substring(0, titleString.length()-1));

        grid.setCanEdit(false);
        grid.setShowAllRecords(true);
        grid.setWidth("1350px");
        grid.setHeight("50%");

        List<ListGridField> fields = new ArrayList<ListGridField>(chosenSessions.size()+3);

        ListGridField field = new ListGridField("testDescription", "Test Description");
        fields.add(field);

        field = new ListGridField("testName", "Name");
        fields.add(field);

        field = new ListGridField("testMetric", "Metric");
        field.setWidth("300px");
        fields.add(field);

        for (SessionDataDto dto : chosenSessions){
            field = new ListGridField(dto.getName(), dto.getName());
            field.setWidth("150px");
            fields.add(field);
        }

        grid.setFields(fields.toArray(new ListGridField[]{}));

        grid.setGroupByField("testDescription", "testName");
        grid.freezeField("testName");
        grid.freezeField("testMetric");

        grid.hideField("testName");
        grid.hideField("testDescription");

        add(grid);
        cache = new HashMap<MetricNameDto, MetricDto>();
    }

    public void updateMetrics(Set<MetricNameDto> dto){
        final ArrayList<MetricRecord> records = new ArrayList<MetricRecord>(dto.size());
        ArrayList<MetricNameDto> notLoaded = new ArrayList<MetricNameDto>();
        for (MetricNameDto metricName : dto){
            if (!cache.containsKey(metricName)){
                notLoaded.add(metricName);
            }else{
                MetricDto metric = cache.get(metricName);
                records.add(new MetricRecord(metric));
            }
        }
        MetricDataService.Async.getInstance().getMetrics(notLoaded, new AsyncCallback<List<MetricDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(List<MetricDto> result) {
                for (MetricDto metric : result){
                    cache.put(metric.getMetricName(), metric);
                    records.add(new MetricRecord(metric));
                }
                grid.setData(records.toArray(new MetricRecord[]{}));
            }
        });
    }

    @Override
    public void update(Set<TaskDataDto> tests) {
        //To change body of implemented methods use File | Settings | File Templates.

    }

    @Override
    public void addTest(TaskDataDto test) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeTest(TaskDataDto test) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private class MetricRecord extends ListGridRecord{
        public MetricRecord(MetricDto dto){
            String description = dto.getMetricName().getTests().getDescription();
            setAttribute("testDescription", ((description==null|| "".equals(description) ? "Empty description" : description)));
            setAttribute("testName", dto.getMetricName().getTests().getTaskName());
            setAttribute("testMetric", dto.getMetricName().getName());
            for (MetricValueDto value : dto.getValues()){
                setAttribute("Session "+value.getSessionId(), value.getValue());
            }
        }
    }
}
