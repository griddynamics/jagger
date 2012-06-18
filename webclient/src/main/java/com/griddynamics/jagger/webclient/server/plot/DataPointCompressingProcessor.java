package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.webclient.client.dto.PointDto;

import java.util.*;

/**
 * Provides facility for clearing given collection from waste data which not pass through threshold
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/6/12
 */
public class DataPointCompressingProcessor {
    private final double relativeThreshold;
    private final int minCountToProcess;

    /**
     * Constructor
     *
     * @param relativeThreshold threshold expressed in percents divided by 100
     * @param minCountToProcess min list size for processing
     */
    public DataPointCompressingProcessor(double relativeThreshold, int minCountToProcess) {
        this.relativeThreshold = relativeThreshold;
        this.minCountToProcess = minCountToProcess;
    }

    /**
     * Clear collection from waste data which not pass through threshold
     *
     * @param source            collection for processing
     * @return compressed collection
     */
    public List<PointDto> process(List<PointDto> source) {
        if (source.size() < minCountToProcess) {
            return new ArrayList<PointDto>(source);
        }

        // Sort by X axis values asc
        Collections.sort(source, new Comparator<PointDto>() {
            @Override
            public int compare(PointDto o1, PointDto o2) {
                return Double.compare(o1.getX(), o2.getX());
            }
        });

        // Search min/max
        Pair<Double, Double> minMaxPair = findMinMax(source);

        double absoluteThreshold = 0;
        // If min & max greater than 0 absolute threshold determines on max basis
        if (minMaxPair.getFirst() >= 0 && minMaxPair.getSecond() >= 0) {
            absoluteThreshold = minMaxPair.getSecond() * relativeThreshold;
        }
        // If min & max lesser than 0 absolute threshold determines on min basis
        else if (minMaxPair.getFirst() <= 0 && minMaxPair.getSecond() <= 0) {
            absoluteThreshold = minMaxPair.getFirst() * relativeThreshold;
        }
        // If min & max of different signs absolute threshold determines on |min|+|max| basis
        else {
            absoluteThreshold = Math.abs((minMaxPair.getSecond() - minMaxPair.getFirst()) * relativeThreshold);
        }

        int i = 1;
        List<PointDto> compressed = new ArrayList<PointDto>();
        compressed.add(source.get(0));
        while (true) {
            // If ith element is the last in source list
            if (i == source.size() - 1) {
                compressed.add(source.get(i));
                break;
            }

            // If 2==1 and 2==3 then 2 throw from compressed list
            double current = source.get(i).getY();
            double previous = compressed.get(compressed.size() - 1).getY();
            double next = source.get(i + 1).getY();
            if (isThresholdExceeded(Pair.of(current, previous), absoluteThreshold) || isThresholdExceeded(Pair.of(current, next), absoluteThreshold)) {
                compressed.add(source.get(i));
            }
            i++;
        }

        return compressed;
    }

    /**
     * Find min and max collection values for one pass
     *
     * @param pointDtoList source list
     * @return min/max pair
     */
    private Pair<Double, Double> findMinMax(List<PointDto> pointDtoList) {
        if (pointDtoList.isEmpty()) {
            return null;
        }
        if (pointDtoList.size() == 1) {
            Double element = pointDtoList.get(0).getY();
            return Pair.of(element, element);
        }
        Iterator<PointDto> iter = pointDtoList.iterator();
        Double min = iter.next().getY();
        Double max = iter.next().getY();

        if (min.compareTo(max) > 0) {
            Double t = min;
            min = max;
            max = t;
        }
        while (iter.hasNext()) {
            Double value = iter.next().getY();
            if (value.compareTo(max) > 0) {
                max = value;
            } else if (value.compareTo(min) < 0) {
                min = value;
            }
        }

        return Pair.of(min, max);
    }

    /**
     * Indicate whether threshold is exceeded
     *
     * @param pair pair for which checking being accomplished
     * @param threshold absolute threshold which takes into account min/max values of given pair
     * @return <code>true</code> if threshold is exceeded, <code>false</code> otherwise
     */
    private boolean isThresholdExceeded(Pair<Double, Double> pair, double threshold) {
        return Double.compare(Math.abs(pair.getFirst() - pair.getSecond()), threshold) > 0;
    }
}
