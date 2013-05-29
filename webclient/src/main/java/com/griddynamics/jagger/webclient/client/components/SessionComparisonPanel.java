package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.data.MetricRankingProvider;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.AutoFitWidthApproach;
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
public class SessionComparisonPanel extends VerticalPanel{

    private Label title = new Label();
    private ListGrid grid = new ListGrid();

    private ListGridRecord[] EMPTY_DATA = new ListGridRecord[0];
    private HashMap<MetricNameDto, MetricDto> cache;

    public SessionComparisonPanel(Set<SessionDataDto> chosenSessions){
        init(chosenSessions);
    }

    private void init(Set<SessionDataDto> chosenSessions){
        //init title
        title.setStyleName(JaggerResources.INSTANCE.css().sessionNameHeader());

        grid.setCanEdit(false);
        grid.setShowAllRecords(true);
        grid.setShowResizeBar(true);
        grid.setRedrawOnResize(true);
        grid.setBorder("1px solid blue");
        grid.setWidth("97%");
        grid.setHeight("80%");
        grid.setCanCollapseGroup(false);

        List<ListGridField> fields = new ArrayList<ListGridField>(chosenSessions.size()+3);

        ListGridField field = new ListGridField("testDescription", "Test Description");
        fields.add(field);

        field = new ListGridField("testName", "Name");
        fields.add(field);

        field = new ListGridField("testMetric", "Metric");
        field.setAutoFitWidth(true);
        fields.add(field);

        //sort sessions by number create
        SortedSet<SessionDataDto> sortedSet = new TreeSet<SessionDataDto>(new Comparator<SessionDataDto>() {
            @Override
            public int compare(SessionDataDto o, SessionDataDto o2) {
                return o.getName().compareTo(o2.getName());
            }
        });
        sortedSet.addAll(chosenSessions);


        StringBuilder titleText = new StringBuilder("Comparison of sessions : ");

        for (SessionDataDto dto : sortedSet){
            titleText.append(dto.getSessionId()+",");
            field = new ListGridField(dto.getName(), dto.getName());
            field.setAutoFitWidthApproach(AutoFitWidthApproach.VALUE);
            field.setCanGroupBy(false);
            field.setSortByDisplayField(false);
            fields.add(field);
        }

        String titleString = titleText.toString();
        title.setText(titleString.substring(0, titleString.length()-1));

        grid.setFields(fields.toArray(new ListGridField[]{}));

        grid.setGroupByField("testDescription", "testName");
        grid.freezeField("testName");
        grid.freezeField("testMetric");

        grid.hideField("testName");
        grid.hideField("testDescription");

        ScrollPanel scrollPanel = new ScrollPanel(grid);
        add(scrollPanel);
        cache = new HashMap<MetricNameDto, MetricDto>();
    }

    public void updateMetrics(Set<MetricNameDto> dto){
        if (dto.size()==0){
            grid.setData(EMPTY_DATA);
            grid.refreshFields();
            return;
        }

        final ArrayList<MetricNameDto> notLoaded = new ArrayList<MetricNameDto>();
        final ArrayList<MetricDto> loaded = new ArrayList<MetricDto>();

        for (MetricNameDto metricName : dto){
            if (!cache.containsKey(metricName)){
                notLoaded.add(metricName);
            }else{
                MetricDto metric = cache.get(metricName);
                loaded.add(metric);
            }
        }
        MetricDataService.Async.getInstance().getMetrics(notLoaded, new AsyncCallback<List<MetricDto>>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(List<MetricDto> result) {

                loaded.addAll(result);

                MetricRankingProvider.sortMetrics(loaded);

                RecordList list = new RecordList();
                for (MetricDto metric : loaded){
                    MetricRecord record = new MetricRecord(metric);
                    cache.put(metric.getMetricName(), metric);
                    list.add(record);
                }

                grid.setData(list);
                grid.setShowAllRecords(true);
                grid.refreshFields();
                grid.redraw();
            }
        });
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
