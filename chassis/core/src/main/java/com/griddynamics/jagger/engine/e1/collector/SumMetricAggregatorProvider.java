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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nikolay Musienko
 *         Date: 17.07.13
 */

public class SumMetricAggregatorProvider implements MetricAggregatorProvider {

    @Override
    public MetricAggregator provide() {
        return new SumMetricAggregator();
    }

    private static class SumMetricAggregator implements MetricAggregator {

        Logger log = LoggerFactory.getLogger(SumMetricAggregator.class);

        Double sum = null;

        @Override
        public void append(Number calculated) {
            log.debug("append({})", calculated);
            if (sum == null)
                sum = new Double(0);

            sum += calculated.doubleValue();
        }

        @Override
        public Number getAggregated() {
            if (sum == null)
                return null;

            return sum.doubleValue();
        }

        @Override
        public void reset() {
            log.debug("Reset aggregator");
            sum = null;
        }

        @Override
        public String getName() {
            return "sum";
        }

        @Override
        public String toString() {
            return "SumMetricAggregator{" +
                    "log=" + log +
                    ", sum=" + sum +
                    '}';
        }
    }



}