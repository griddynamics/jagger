package com.griddynamics.jagger.dbapi.util;

import com.griddynamics.jagger.dbapi.dto.PlotSingleDto.PointShape;
import com.griddynamics.jagger.util.MonitoringIdUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class PlotPointShapeGenerator {

    private static AtomicInteger counter = new AtomicInteger(0);
    private static ConcurrentMap<String, Integer> sessionsMap = new ConcurrentHashMap<>();

    public static PointShape generatePointShape(String metricId, String sessionId) {
        return generatePointShape(Collections.singletonList(metricId), sessionId);
    }

    private static PointShape generatePointShape(List<String> metricIds, String sessionId) {
        List<String> shapeIds = new ArrayList<>();

        if (isNotEmpty(metricIds)) {
            // Search if metricId or its synonyms already has shape
            for (String metricId : metricIds) {
                MonitoringIdUtils.MonitoringId monitoringId = MonitoringIdUtils.splitMonitoringMetricId(metricId);
                String shapeId = (monitoringId != null) ? (monitoringId.getMonitoringName() + sessionId) : (metricId + sessionId);
                shapeIds.add(shapeId);

                // Shape found
                if (sessionsMap.containsKey(shapeId)) {
                    Integer indexOfShape = sessionsMap.get(shapeId);
                    for (String shapeID : shapeIds) {
                        sessionsMap.put(shapeID, indexOfShape);
                    }
                    return PointShape.values()[indexOfShape];
                }
            }
        }

        // Shape was not set before
        int indexOfShape = counter.getAndIncrement() % PointShape.values().length;
        for (String shapeID : shapeIds) {
            sessionsMap.put(shapeID, indexOfShape);
        }
        return PointShape.values()[indexOfShape];
    }
}
