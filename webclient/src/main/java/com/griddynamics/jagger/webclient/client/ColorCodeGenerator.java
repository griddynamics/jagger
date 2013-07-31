package com.griddynamics.jagger.webclient.client;

import java.util.Arrays;
import java.util.List;

public class ColorCodeGenerator {
    private static int counter;
    private static List<String> hexCodes = Arrays.asList(
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
        int index = counter ++;
        return hexCodes.get(index % hexCodes.size());
    }
}
