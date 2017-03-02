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

package com.griddynamics.jagger.engine.e1.collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.CUMULATIVE_AGGREGATOR_ID;

/** Calculates increment of the value on interval
 * @author amikryukov
 * @n
 * @par Details:
 * @details this is special aggregator for cumulative metrics. Cumulative metrics are always increasing values. @n
 * F.e. calculating number of events. This aggregator is calculating increment of the value on interval @n
 * Example: @n
 * @code
 * ----- interval
 * 2
 * 3                    aggregated value = 2
 * 4
 * ----- interval
 * 5
 * 6                    aggregated value = 1
 * 6
 * ----- interval
 * 6
 * 6                    aggregated value = 0
 * 6
 * ----- interval
 * @endcode
 * @n
 *
 * @ingroup Main_Aggregators_group */
public class CumulativeMetricAggregatorProvider implements MetricAggregatorProvider {

    /** Method is called to provide instance of private class: \b CumulativeMetricAggregator that implements @ref MetricAggregator<C extends Number> and calculates increment of cumulative metric on interval */
    @Override
    public MetricAggregator provide() {
        return new CumulativeMetricAggregator();
    }

    private static class CumulativeMetricAggregator implements MetricAggregator<Number> {

        Logger log = LoggerFactory.getLogger(CumulativeMetricAggregator.class);

        // max value in current interval
        private Double currentValue = null;

        // max value in previous interval or very first value if it is in first interval
        private Double previousValue = null;

        @Override
        public void append(Number calculated) {
            log.debug("append({})", calculated);

            // if true then it is first interval
            if (previousValue == null) {
                // remember very first value
                previousValue = calculated.doubleValue();
            }

            if (currentValue == null) {
                currentValue = calculated.doubleValue();
            } else {
                currentValue = Math.max(currentValue, calculated.doubleValue());
            }
        }

        @Override
        public Number getAggregated() {
            // nothing was appended
            if (currentValue == null) {
                return null;
            }

            Number result = currentValue - previousValue;

            log.debug("getAggregated() = {}", result);
            return result;
        }

        @Override
        public void reset() {
            // that means that next 'append()' will be called for next interval
            previousValue = currentValue;
            currentValue = null;

            log.debug("reset()");
        }

        @Override
        public String getName() {
            return CUMULATIVE_AGGREGATOR_ID;
        }

        @Override
        public String toString() {
            return "CumulativeMetricAggregator{" +
                    "currentValue=" + currentValue +
                    ", previousValue=" + previousValue +
                    '}';
        }
    }
}
