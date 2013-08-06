package com.griddynamics.jagger.webclient.client.data;

import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.smartgwt.client.data.RecordList;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 */
public class MetricFullData {

    private HashMap<String, MetricDto> metrics;
    private RecordList recordList;

    {
        metrics = new HashMap<String, MetricDto>();
        recordList = new RecordList();
    }

    public HashMap<String, MetricDto> getMetrics() {
        return metrics;
    }

    public void setMetrics(HashMap<String, MetricDto> metrics) {
        this.metrics = metrics;
    }

    public RecordList getRecordList() {
        return recordList;
    }

    public void setRecordList(RecordList recordList) {
        this.recordList = recordList;
    }

    public void clear(){
        metrics.clear();
        recordList = new RecordList();
    }
}
