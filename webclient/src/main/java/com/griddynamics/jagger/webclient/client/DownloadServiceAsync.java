package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.SummaryTableDto;

import java.util.List;

public interface DownloadServiceAsync {

    void createPlotCsvFile(List<PlotSingleDto> lines, String plotHeader, String xAxisLabel, AsyncCallback<String> async);
    void createSummaryTableScvFile(SummaryTableDto summaryTableDto, AsyncCallback<String> async);
}
