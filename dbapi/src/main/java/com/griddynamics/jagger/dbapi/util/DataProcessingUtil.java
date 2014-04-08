package com.griddynamics.jagger.dbapi.util;


import com.griddynamics.jagger.dbapi.dto.PointDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
}

