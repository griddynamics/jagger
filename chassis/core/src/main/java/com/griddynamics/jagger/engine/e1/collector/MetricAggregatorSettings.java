/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.util.TimeUnits;

/** Class that describes settings of aggregator
 * @details With help of this class you can define: @n
 * @li aggregation interval for metric or number of saved points
 * @li normalisation of metric vs time
 *
 */
public class MetricAggregatorSettings {

    /** Empty settings that not affect data processing. Used when no additional settings required for aggregation */
    public static final MetricAggregatorSettings EMPTY_SETTINGS = new MetricAggregatorSettings();

    /** Interval of time to normalize values by it */
    private TimeUnits normalizationBy = TimeUnits.NONE;
    /** Size of interval in milliseconds to aggregate values on it */
    private int pointInterval = 0;
    /** Maximum number of points on plot */
    private int pointCount = 0;
    /** If false - normalize by time of current aggregation interval, if true - by time elapsed from beginning of the test */
    private boolean normalizeOnFullMeasuredInterval = false;

    /** Getter for normalization interval
     * @return Normalization interval
     */
    public TimeUnits getNormalizationBy() {
        return normalizationBy;
    }

    /** Getter for aggregation interval
     * @return Aggregation interval
     */
    public int getPointInterval() {
        return pointInterval;
    }

    /** Getter for points count
     * @return Points count
     */
    public int getPointCount() {
        return pointCount;
    }

    /** Getter for normalization strategy (false - normalize by current aggregation interval, true - by time from beginning of the test
     * @return false if normalizing by aggregation interval, true - by time from test beginning
     */
    public boolean isNormalizeOnFullMeasuredInterval() {
        return normalizeOnFullMeasuredInterval;
    }

    /** Setter for normalization interval. Aggregated values will be normalized by this interval. @n
     * Use TimeUtils.NONE if normalization not required.
     * @param normalizationBy Normalization interval
     */
    public void setNormalizationBy(TimeUnits normalizationBy) {
        this.normalizationBy = normalizationBy;
    }

    /** Setter for aggregation interval. Aggregator will aggregate values on this interval in milliseconds. @n
     * !Note that pointInterval has higher priority then pointCount
     * @param pointInterval Aggregation interval
     */
    public void setPointInterval(int pointInterval) {
        this.pointInterval = pointInterval;
    }

    /** Setter for points count. Indicates maximum number of points on plot. @n
     * !Note that pointInterval has higher priority then pointCount
     * @param pointCount Points count;
     */
    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    /** Setter for normalization strategy (false - normalize by current aggregation interval, true - by time from beginning of the test
     * @param  normalizeOnFullMeasuredInterval - set false if normalizing by aggregation interval, true - by time from test beginning
     */
    public void setNormalizeOnFullMeasuredInterval(boolean normalizeOnFullMeasuredInterval) {
        this.normalizeOnFullMeasuredInterval = normalizeOnFullMeasuredInterval;
    }
}