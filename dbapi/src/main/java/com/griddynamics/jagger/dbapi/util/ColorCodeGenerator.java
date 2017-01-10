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
                    for (String colourId : colorIds) {
                        sessionsMap.put(colourId, indexInColorsHexCodes);
                    }
                    return COLORS_HEX_CODES.get(indexInColorsHexCodes);
                }
            }
        }

        // Color was not set before
        int indexInColorsHexCodes = counter.getAndIncrement() % COLORS_HEX_CODES.size();
        for (String colourId : colorIds) {
            sessionsMap.put(colourId, indexInColorsHexCodes);
        }

        return COLORS_HEX_CODES.get(indexInColorsHexCodes);
    }

    private static List<String> generateColors() {
        List<String> colors = new ArrayList<>();

        // These vars are needed for equal distribution of colors
        final BigDecimal brightnessSteps = new BigDecimal(2);
        final BigDecimal saturationSteps = new BigDecimal(1);
        final BigDecimal hueSteps = new BigDecimal(15);

        final BigDecimal brightnessStep = new BigDecimal("0.5").divide(brightnessSteps, 2, RoundingMode.HALF_UP);
        final BigDecimal saturationStep = new BigDecimal("0.6").divide(saturationSteps, 2, RoundingMode.HALF_UP);
        final BigDecimal hueStep = ONE.divide(hueSteps, 8, RoundingMode.HALF_UP);

        final BigDecimal brightnessLimit = new BigDecimal("0.5");
        final BigDecimal saturationLimit = new BigDecimal("0.4");

        for (BigDecimal brightness = ONE; brightness.compareTo(brightnessLimit) == 1; ) {
            for (BigDecimal saturation = ONE; saturation.compareTo(saturationLimit) >= 0; ) {
                for (BigDecimal hue = ZERO; hue.compareTo(ONE) < 1; ) {
                    Color hsbColor = Color.getHSBColor(hue.floatValue(), saturation.floatValue(), brightness.floatValue());
                    colors.add(getHexCodeOfColor(hsbColor));

                    hue = hue.add(hueStep);
                }
                saturation = saturation.subtract(saturationStep);
            }
            brightness = brightness.subtract(brightnessStep);
        }
        return shuffle(colors);
    }

    private static List<String> shuffle(List<String> colors) {
        List<String> shuffled = new ArrayList<>(colors.size());

        int counter = 0;
        int localCounter = 0;
        while (shuffled.size() < colors.size()) {
            String colorToInsert = colors.get(localCounter % colors.size());
            if (!shuffled.contains(colorToInsert)) {
                shuffled.add(counter, colorToInsert);
                counter++;
                localCounter += 3;
            } else {
                localCounter++;
            }
        }
        return shuffled;
    }


    private static String getHexCodeOfColor(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()).toUpperCase();
    }
}
