package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.util.MonitoringIdUtils;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static com.griddynamics.jagger.dbapi.util.CommonUtils.addAllNullSafe;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class ColorCodeGenerator {

    private static AtomicInteger counter = new AtomicInteger(0);
    private static ConcurrentMap<String, Integer> sessionsMap = new ConcurrentHashMap<>();
    private static final ImmutableList<String> COLORS_HEX_CODES = ImmutableList.copyOf(generateColors());

    public static String getHexColorCode(String metricId, String sessionId) {
        return getHexColorCode(singletonList(metricId), sessionId);
    }

    public static String getHexColorCode(String metricId, List<String> synonyms, String sessionId) {
        List<String> metricIds = newArrayList(metricId);
        addAllNullSafe(metricIds, synonyms);
        return getHexColorCode(metricIds, sessionId);
    }

    private static String getHexColorCode(List<String> metricIds, String sessionId) {
        List<String> colorIds = new ArrayList<>();

        if (isNotEmpty(metricIds)) {
            // Search if metricId or its synonyms already has color
            for (String metricId : metricIds) {
                MonitoringIdUtils.MonitoringId monitoringId = MonitoringIdUtils.splitMonitoringMetricId(metricId);
                String colorId = (monitoringId != null) ? (monitoringId.getMonitoringName() + sessionId) : (metricId + sessionId);
                colorIds.add(colorId);

                // Color found
                if (sessionsMap.containsKey(colorId)) {
                    Integer indexInColorsHexCodes = sessionsMap.get(colorId);
                    colorIds.forEach(colourId -> sessionsMap.put(colourId, indexInColorsHexCodes));
                    return COLORS_HEX_CODES.get(indexInColorsHexCodes);
                }
            }
        }

        // Color was not set before
        int indexInColorsHexCodes = counter.getAndIncrement() % COLORS_HEX_CODES.size();
        colorIds.forEach(colourId -> sessionsMap.put(colourId, indexInColorsHexCodes));

        return COLORS_HEX_CODES.get(indexInColorsHexCodes);
    }

    private static List<String> generateColors() {
        List<String> colors = new ArrayList<>();

        // These vars are needed for equal distribution of colors
        final BigDecimal brightnessSteps = new BigDecimal(2);
        final BigDecimal saturationSteps = new BigDecimal(2);
        final BigDecimal hueSteps = new BigDecimal(15);

        final BigDecimal brightnessStep = new BigDecimal(0.5).divide(brightnessSteps, 2, RoundingMode.HALF_UP);
        final BigDecimal saturationStep = new BigDecimal(0.6).divide(saturationSteps, 2, RoundingMode.HALF_UP);
        final BigDecimal hueStep = ONE.divide(hueSteps, 8, RoundingMode.HALF_UP);

        for (BigDecimal brightness = ONE; brightness.compareTo(new BigDecimal("0.5")) == 1; ) {
            for (BigDecimal saturation = ONE; saturation.compareTo(new BigDecimal("0.4")) >= 0; ) {
                for (BigDecimal hue = ZERO; hue.compareTo(ONE) < 1; ) {
                    Color hsbColor = Color.getHSBColor(hue.floatValue(), saturation.floatValue(), brightness.floatValue());
                    colors.add(getHexCodeOfColor(hsbColor));

                    hue = hue.add(hueStep);
                }
                saturation = saturation.subtract(saturationStep);
            }
            brightness = brightness.subtract(brightnessStep);
        }
        return colors;
    }

    private static String getHexCodeOfColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()).toUpperCase();
    }
}
