package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.util.LegendProvider;
import com.griddynamics.jagger.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public abstract class PlotsDbMetricDataFetcher extends DbMetricDataFetcher<Pair<MetricNameDto, List<PlotSingleDto>>> {

    protected LegendProvider legendProvider;

    @Autowired
    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }
}
