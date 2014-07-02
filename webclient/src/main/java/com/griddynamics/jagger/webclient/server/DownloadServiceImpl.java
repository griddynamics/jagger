package com.griddynamics.jagger.webclient.server;

import com.google.common.base.Joiner;
import com.griddynamics.jagger.dbapi.csv.PlotToCsvGenerator;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.PlotNode;
import com.griddynamics.jagger.webclient.client.DownloadService;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class DownloadServiceImpl implements DownloadService {

    private PlotProviderService plotProviderService;

    private MetricDataService metricDataService;

    private InMemoryFileStorage fileStorage;

    private Logger log = LoggerFactory.getLogger(DownloadServiceImpl.class);

    @Required
    public void setPlotProviderService(PlotProviderService plotProviderService) {
        this.plotProviderService = plotProviderService;
    }

    @Required
    public void setMetricDataService(MetricDataService metricDataService) {
        this.metricDataService = metricDataService;
    }

    @Required
    public void setFileStorage(InMemoryFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public String createPlotCsvFile(MetricNode metricNode) throws RuntimeException {
        try {
            // getPlotData :
            Set<MetricNode> metricNodeSet = Collections.singleton(metricNode);
            PlotIntegratedDto plot;

            if (metricNode instanceof PlotNode) {
                // processing metric plots

                Map<MetricNode, PlotIntegratedDto> plotsMap = plotProviderService.getPlotData(metricNodeSet);
                plot = plotsMap.get(metricNode);
            } else {
                // processing trends plots
                // second param in getMetrics is false because we don't need decisions for metrics in CsvFile
                Map<MetricNode, SummaryIntegratedDto> map = metricDataService.getMetrics(metricNodeSet, false);

                SummaryIntegratedDto summaryInDto = map.get(metricNode);
                if (summaryInDto == null) {
                    throw new RuntimeException("could not find summary data for " + metricNode.toString());
                }
                plot = summaryInDto.getPlotIntegratedDto();
            }

            if (plot == null) {
                throw new RuntimeException("could not find plot data for " + metricNode.toString());
            }

            String sessionsPrefix = getSessionPrefix(metricNode.getMetricNameDtoList());

            String fileKey = sessionsPrefix + '-' + plot.getPlotHeader().replaceAll(",", "_");

            if (fileStorage.exists(fileKey)) {
                // return same object
                return fileKey;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // create csv file
            PlotToCsvGenerator.generateCsvFile(plot, byteArrayOutputStream);

            byte[] fileInBytes = byteArrayOutputStream.toByteArray();

            fileStorage.store(fileKey, fileInBytes);

            return fileKey;
        } catch (Exception e) {
            log.error("Errors while creating csv file for " + metricNode, e);
            throw new RuntimeException("Errors while creating csv file for " + metricNode, e);
        }
    }

    private String getSessionPrefix(List<MetricNameDto> metricNameDtoList) {

        Set<String> sessionIds = new HashSet<String>();

        for (MetricNameDto metricNameDto : metricNameDtoList) {
            sessionIds.addAll(metricNameDto.getTest().getSessionIds());
        }

        Joiner joiner = Joiner.on("_");
        return joiner.join(sessionIds);
    }
}
