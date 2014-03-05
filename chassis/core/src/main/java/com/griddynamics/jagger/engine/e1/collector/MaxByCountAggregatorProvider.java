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


/**
 * Implementation of MetricAggregatorProvider to hold max value of all inputs.
 */
public class MaxByCountAggregatorProvider implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new MaxByCountAggregator();
    }

    private static class MaxByCountAggregator implements MetricAggregator<Number> {


        private Double value = null;
        private int count = 0;

        @Override
        public void append(Number calculated) {

            count ++;

            if (value == null)
                value = calculated.doubleValue();
            else
                value = Math.max(value, calculated.doubleValue());
        }

        @Override
        public Number getAggregated() {
            if (value == null)
                return null;
            return value / count;
        }

        @Override
        public void reset() {
            value = null;
            count = 0;
        }

        @Override
        public String getName() {
            return "max-by-count";
        }

        @Override
        public String toString() {
            return "MaxByCountAggregator{" +
                    "value=" + value +
                    '}';
        }
    }
}
