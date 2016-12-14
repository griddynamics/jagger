package com.griddynamics.jagger.webclient.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;

import java.util.List;


@RemoteServiceRelativePath("rpc/DownloadService")
public interface DownloadService extends RemoteService {

    public static class Async {
        private static final DownloadServiceAsync ourInstance = (DownloadServiceAsync) GWT.create(DownloadService.class);

        public static DownloadServiceAsync getInstance() {
            return ourInstance;
        }
    }

    /**
     * Creates csv file representing plot on server side and send back key for created file
     * @param lines that should be represented in csv file
     * @param plotHeader file key will be created with plotHeader
     * @param xAxisLabel x axis label for lines
     * @return key of created file
     * @throws RuntimeException */
    public String createPlotCsvFile(List<PlotSingleDto> lines, String plotHeader, String xAxisLabel) throws RuntimeException;
}
