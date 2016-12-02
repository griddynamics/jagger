/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.util.statistics.percentiles;

import java.util.ArrayList;

// This class is a derivation of hist4j https://github.com/flaptor/hist4j

/**
 * This class implements a histogram that adapts to an unknown data distribution.
 * It keeps a more or less constant resolution throughout the data range by increasing
 * the resolution where the data is more dense.  For example, if the data has such
 * such a distribution that most of the values lie in the 0-5 range and only a few are
 * in the 5-10 range, the histogram would adapt and assign more counting buckets to
 * the 0-5 range and less to the 5-10 range.
 * This implementation provides a method to obtain the accumulative density function
 * for a given data point, and a method to obtain the data point that splits the
 * data set at a given percentile.
 * @author Jorge Handl
 */
public class AdaptiveHistogram {

    private long totalCount;     // total number of data points
    private HistogramNode root;  // root of the tree

    /**
     * Class constructor.
     */
    public AdaptiveHistogram() {
        root = null;
        reset();
    }

    /**
     * Erases all data from the histogram.
     */
    public void reset() {
        if (null != root) {
            root.reset();
            root = null;
        }
        totalCount = 0;
    }

    /**
     * Adds a data point to the histogram.
     * @param value the data point to add.
     */
    public void addValue(double value) {
        totalCount++;
        if (null == root) {
            root = new HistogramDataNode();
        }
        root = root.addValue(this, value);
    }

    /**
     * Returns the number of data points stored in the same bucket as a given value.
     * @param value the reference data point.
     * @return the number of data points stored in the same bucket as the reference point.
     */
    public long getCount(double value) {
        long count = 0;
        if (null != root) {
            count = root.getCount(value);
        }
        return count;
    }

    /**
     * Returns the cumulative density function for a given data point.
     * @param value the reference data point.
     * @return the cumulative density function for the reference point.
     */
    public long getAccumCount(double value) {
        long count = 0;
        if (null != root) {
            count = root.getAccumCount(value);
        }
        return count;
    }

    /**
     * Returns the data point that splits the data set at a given percentile.
     * @param percentile the percentile at which the data set is split.
     * @return the data point that splits the data set at the given percentile.
     */
    public Double getValueForPercentile(double percentile) {
        long targetAccumCount = (long)(totalCount * percentile/100);
        double value = 0;
        if (null != root) {
            value = root.getValueForAccumCount(new long[]{0, targetAccumCount});
        }
        return value;
    }

    /**
     * This method is used by the internal data structure of the histogram to get the
     * limit of data points that should be counted at one bucket.
     * @return the limit of data points to store a one bucket.
     */
    protected int getCountPerNodeLimit() {
        int limit = (int) (totalCount / 10);
        if (0 == limit) {
            limit = 1;
        }
        return limit;
    }

    /**
     * Auxiliary interface for inline functor object.
     */
    protected interface ValueConversion {
        /**
         * This method should implement the conversion function.
         * @param value the input value.
         * @return the resulting converted value.
         */
        double convertValue(double value);
    }

    /**
     * Return a table representing the data in this histogram.
     * Each element is a table cell containing the range limit values and the count for that range.
     */
    public ArrayList<Cell> toTable() {
        ArrayList<Cell> table = new ArrayList<Cell>();
        if (null != root) {
            root.toTable(table);
        }
        return table;
    }

}
