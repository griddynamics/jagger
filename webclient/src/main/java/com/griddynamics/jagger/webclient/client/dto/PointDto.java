package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PointDto implements Serializable {
    private double x;
    private double y;

    public PointDto() {
    }

    public PointDto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointDto)) return false;

        PointDto pointDto = (PointDto) o;

        if (Double.compare(pointDto.x, x) != 0) return false;
        if (Double.compare(pointDto.y, y) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = new Double(x).hashCode() >> 13 ^ new Double(y).hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PointDto{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
