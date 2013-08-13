package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
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

    private final String TEST_DESCRIPTION = "testDescription";
    private final String TEST_NAME = "testName";
    private final String TEST_METRIC = "testMetric";
    private final String SESSION_HEADER = "Session ";
    private final String SESSION_DATA_SUFFIX = "_data";

    private Label title = new Label();
    private ListGrid grid = new ListGrid(){
        @Override
        protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
            String fieldName = this.getFieldName(colNum);
            if (fieldName.startsWith(SESSION_HEADER)){
                String text = record.getAttribute(fieldName+ SESSION_DATA_SUFFIX);

                Label label = new Label(text);
                label.setAutoHeight();
                label.setWidth100();
                return label;
            }
            return null;
        }
    };

    private ListGridRecord[] EMPTY_DATA = new ListGridRecord[0];
    private HashMap<MetricNameDto, MetricDto> cache;

    public HashMap<MetricNameDto, MetricDto> getCachedMetrics() {
        return cache;
    }

    public ListGrid getGrid() {
        return grid;
    }

    public ListGridRecord[] getEmptyListGrid() {
        return EMPTY_DATA;
    }

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
        grid.setWrapCells(true);
        grid.setFixedRecordHeights(false);
        grid.setShowRecordComponents(true);
        grid.setShowRecordComponentsByCell(true);

        List<ListGridField> fields = new ArrayList<ListGridField>(chosenSessions.size()+3);

        ListGridField field = new ListGridField(TEST_DESCRIPTION, "Test Description");
        fields.add(field);

        field = new ListGridField(TEST_NAME, "Name");
        fields.add(field);

        field = new ListGridField(TEST_METRIC, "Metric");
        field.setAutoFitWidth(true);
        fields.add(field);

        //sort sessions by number create
        SortedSet<SessionDataDto> sortedSet = new TreeSet<SessionDataDto>(new Comparator<SessionDataDto>() {
            @Override
            public int compare(SessionDataDto o, SessionDataDto o2) {
                return (Long.parseLong(o.getSessionId()) - Long.parseLong(o2.getSessionId())) > 0 ? 1 : -1;
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
        title.setContents(titleString.substring(0, titleString.length()-1));

        grid.setFields(fields.toArray(new ListGridField[]{}));

        grid.setGroupByField(TEST_DESCRIPTION, TEST_NAME);
        grid.freezeField(TEST_NAME);
        grid.freezeField(TEST_NAME);

        grid.hideField(TEST_NAME);
        grid.hideField(TEST_DESCRIPTION);

        ScrollPanel scrollPanel = new ScrollPanel(grid);
        add(scrollPanel);
        cache = new HashMap<MetricNameDto, MetricDto>();

        EMPTY_DATA = new ListGridRecord[]{new InformationRecord(chosenSessions){

            @Override
            public String getInformationName() {
                return "Comment";
            }

            @Override
            public String getInfo(SessionDataDto session) {
                return session.getComment();
            }
        }};

        grid.setData(EMPTY_DATA);
        grid.setShowAllRecords(true);
    }


    public Record generateRecord(MetricDto dto) {
        return new MetricRecord(dto);
    }

    private class MetricRecord extends ListGridRecord{
        public MetricRecord(MetricDto dto){
            String description = dto.getMetricName().getTests().getDescription();
            setAttribute(TEST_DESCRIPTION, ((description==null|| "".equals(description) ? "Empty description" : description)));
            setAttribute(TEST_NAME, dto.getMetricName().getTests().getTaskName());
            setAttribute(TEST_METRIC, dto.getMetricName().getName());
            for (MetricValueDto value : dto.getValues()){
                setAttribute(SESSION_HEADER+value.getSessionId()+ SESSION_DATA_SUFFIX, value.getValueRepresentation());
            }
        }
    }

    private abstract class InformationRecord extends ListGridRecord{
        public InformationRecord(Set<SessionDataDto> sessions){
            setAttribute(TEST_DESCRIPTION,"Sessions information");
            setAttribute(TEST_NAME,"Common information");
            setAttribute(TEST_METRIC, getInformationName());
            for (SessionDataDto sessionDataDto : sessions){
                setAttribute(SESSION_HEADER + sessionDataDto.getSessionId() + SESSION_DATA_SUFFIX, getInfo(sessionDataDto));
            }
        }

        public abstract String getInformationName();

        public abstract String getInfo(SessionDataDto session);
    }
}
