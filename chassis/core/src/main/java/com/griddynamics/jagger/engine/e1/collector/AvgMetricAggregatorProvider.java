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

/** Calculates average value on interval
 * @author Nikolay Musienko
 * @n
 * @par Details:
 * @details
 * @n
 * @par Usage example in XML:
 * To use this aggregator add @xlink_complex{metric-aggregator-avg} to @xlink_complex{metric-custom} block.
 * @n
 * @dontinclude  tasks-new.conf.xml
 * @skip  begin: following section is used for docu generation - metric-success-rate
 * @until end: following section is used for docu generation - metric-success-rate
 * @n
 * @par Usage example in code:
 * @dontinclude  ProviderOfTestListener.java
 * @skip  begin: following section is used for docu generation - example of metric creation
 * @until end: following section is used for docu generation - example of metric creation
 * @n
 *
 * @ingroup Main_Aggregators_group */
public class AvgMetricAggregatorProvider  implements MetricAggregatorProvider {

    /** Method is called to provide instance of private class: \b AvgMetricAggregator that implements @ref MetricAggregator<C extends Number> and provides averaging */
    @Override
    public MetricAggregator provide() {
        return new AvgMetricAggregator();
    }

    private static class AvgMetricAggregator  implements MetricAggregator<Number> {

        Double sum = null;
        long count = 0;

        @Override
        public void append(Number calculated) {
            if (sum == null)
                sum = new Double(0);

            sum += calculated.doubleValue();
            ++count;
        }

        @Override
        public Number getAggregated() {
            if (sum == null)
                return null;

            if (count == 0) {
                return 0;
            }

            double result = sum / count;

            return Double.valueOf(result);
        }

        @Override
        public void reset() {
            sum = null;
            count = 0;
        }

        @Override
        public String getName() {
            return "avg";
        }

        @Override
        public String toString() {
            return "AvgMetricAggregator{" +
                    "sum=" + sum +
                    ", count=" + count +
                    '}';
        }
    }
}

