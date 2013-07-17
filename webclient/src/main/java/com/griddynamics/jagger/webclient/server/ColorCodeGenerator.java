package com.griddynamics.jagger.webclient.server;

import com.google.common.collect.ImmutableList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class ColorCodeGenerator {
    private static AtomicInteger counter = new AtomicInteger(0);
    private static final ImmutableList<String> hexCodes = ImmutableList.of(
        "#000000",
        "#FF0000",
        "#800000",
        "#FF4500",
        "#808000",
        "#00FF00",
        "#008000",
        "#00FFFF",
        "#008080",
        "#0000FF",
        "#000080",
        "#FF00FF",
        "#800080",
        "#D2691E");

    protected ColorCodeGenerator() {
    }

    public static String getHexColorCode() {
        int index = counter.getAndIncrement();
        return hexCodes.get(index % hexCodes.size());
    }
}
