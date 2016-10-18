package com.griddynamics.jagger.dbapi.util;


import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.griddynamics.jagger.dbapi.util.ColorCodeGenerator.getHexColorCode;
import static com.griddynamics.jagger.dbapi.util.PlotPointShapeGenerator.generatePointShape;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class DataProcessingUtil {

    protected DataProcessingUtil() {
    }

    public static List<PointDto> convertFromRawDataToPointDto(Collection<Object[]> rawData, int xIdx, int yIdx) {
        if (rawData == null) {
            throw new IllegalArgumentException("rawData is null");
        }
        if (rawData.isEmpty()) {
            return Collections.emptyList();
        }

        List<PointDto> pointDtoList = new ArrayList<PointDto>(rawData.size());
        for (Object[] raw : rawData) {
            double x = round((Long) raw[xIdx] / 1000.0D);
            double y = round((Double) raw[yIdx]);
            pointDtoList.add(new PointDto(x, y));
        }
        return pointDtoList;
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
        List<PointDto> list = new ArrayList<PointDto>();

        List<SummaryMetricValueDto> metricList = new ArrayList<SummaryMetricValueDto>();
        for(SummaryMetricValueDto value: metricDto.getValues()) {
            metricList.add(value);
        }

        Collections.sort(metricList, new Comparator<SummaryMetricValueDto>() {

            @Override
            public int compare(SummaryMetricValueDto o1, SummaryMetricValueDto o2) {
                return  o2.getSessionId() < o1.getSessionId() ? 1 : -1;
            }
        });

        for (SummaryMetricValueDto value: metricList) {
            double temp = Double.parseDouble(value.getValue());
            list.add(new PointDto(value.getSessionId(), temp));
        }

        String legend = metricDto.getMetricName().getMetricDisplayName();

        return new PlotSingleDto(
                list,
                legend,
                getHexColorCode(metricDto.getMetricName().getMetricName(),
                        metricDto.getMetricName().getMetricNameSynonyms(),
                        "ss"),
                generatePointShape(metricDto.getMetricName().getMetricName(),
                        metricDto.getMetricName().getMetricNameSynonyms(),
                        "ss"));
    }
}

