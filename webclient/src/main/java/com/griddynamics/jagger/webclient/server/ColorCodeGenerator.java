package com.griddynamics.jagger.webclient.server;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class ColorCodeGenerator {
    private static final List<String> hexCodes = new ArrayList<String>();
    private static AtomicInteger counter = new AtomicInteger(-1);

    static {
        hexCodes.addAll(Arrays.asList(
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
                "#D2691E"
        ));
    }

    protected ColorCodeGenerator() {
    }

    public static String getHexColorCode() {
        counter.compareAndSet(hexCodes.size()-1, -1);
        return hexCodes.get(counter.incrementAndGet());
    }
}
