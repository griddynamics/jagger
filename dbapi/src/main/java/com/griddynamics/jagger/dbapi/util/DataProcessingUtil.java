package com.griddynamics.jagger.dbapi.util;


import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.griddynamics.jagger.dbapi.util.ColorCodeGenerator.getHexColorCode;
import static com.griddynamics.jagger.dbapi.util.PlotPointShapeGenerator.generatePointShape;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class DataProcessingUtil {

    protected DataProcessingUtil() {
    }

    public static double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static String getMessageFromLastCause(Throwable th) {
        if (th.getCause() != null)
            return getMessageFromLastCause(th.getCause());

        return th.getMessage();
    }

    /**
     * Generate Curve from values in SummarySingleDto object as
     * {(sessionId1, val1),(sessionId2, val2), ... (sessionIdn, valn)}
     *
     * @param metricDto contains values to generate curve
     * @return curve
     */
    public static PlotSingleDto generatePlotSingleDto(SummarySingleDto metricDto) {
        List<PointDto> list = new ArrayList<>();

        List<SummaryMetricValueDto> metricList = new ArrayList<>();
        metricList.addAll(metricDto.getValues());

        Collections.sort(metricList, (o1, o2) -> o2.getSessionId() < o1.getSessionId() ? 1 : -1);

        for (SummaryMetricValueDto value : metricList) {
            double temp = Double.parseDouble(value.getValue());
            list.add(new PointDto(value.getSessionId(), temp));
        }

        String legend = metricDto.getMetricName().getMetricDisplayName();

        return new PlotSingleDto(list, legend,
                getHexColorCode(metricDto.getMetricName().getMetricName(), "ss"),
                generatePointShape(metricDto.getMetricName().getMetricName(), "ss"));
    }
}

