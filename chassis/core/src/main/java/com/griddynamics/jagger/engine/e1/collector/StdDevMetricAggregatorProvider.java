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

import com.google.common.collect.Lists;

import java.util.List;

import static com.griddynamics.jagger.util.StandardMetricsNamesUtil.STANDARD_DEVIATION_AGGREGATOR_ID;

/** Calculates standard deviation on the interval
 * @author Nikolay Musienko
 *
 * @ingroup Main_Aggregators_group */
public class StdDevMetricAggregatorProvider implements MetricAggregatorProvider {

    /** Method is called to provide instance of private class: \b StdDevMetricAggregator that implements @ref MetricAggregator<C extends Number> and calculates standard deviation on interval */
    @Override
    public MetricAggregator provide() {
        return new StdDevMetricAggregator();
    }

    private static  class StdDevMetricAggregator implements MetricAggregator<Number> {

        List<Number> points = null;


        private double getMean() {
            double sum = 0;

            for (Number d : points) {
                sum += d.doubleValue();
            }
            return sum / points.size();

        }

        @Override
        public void append(Number calculated) {
            if (points == null)
                points = Lists.newLinkedList();

            points.add(calculated);
        }

        @Override
        public Number getAggregated() {
            if (points == null)
                return null;

            double mean = getMean();
            double sum = 0;

            for (Number d : points) {
                sum += (d.doubleValue() - mean) * (d.doubleValue() - mean);
            }

            return Math.sqrt(sum / points.size());
        }

        @Override
        public void reset() {
            points = null;
        }

        @Override
        public String getName() {
            return STANDARD_DEVIATION_AGGREGATOR_ID;
        }

        @Override
        public String toString() {
            return "StdDevMetricAggregator{" +
                    "points=" + points +
                    '}';
        }
    }
}
