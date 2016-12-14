package com.griddynamics.jagger.dbapi.model;

import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;

/**
 * Represents legend node
 */
public class LegendNode extends MetricNode {

    private PlotSingleDto line;

    public PlotSingleDto getLine() {
        return line;
    }

    public void setLine(PlotSingleDto attachment) {
        this.line = attachment;
    }
}
