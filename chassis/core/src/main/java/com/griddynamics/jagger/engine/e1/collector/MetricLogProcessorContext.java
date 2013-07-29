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

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Nikolay Musienko
 *         Date: 23.07.13
 */

public class MetricLogProcessorContext {
    Logger log = LoggerFactory.getLogger(MetricLogProcessorContext.class);

    private Map<String, MetricAggregatorContextEntry> metricAggregatorContext = Maps.newHashMap();
    private final MetricAggregatorContextEntry defaultMetricAggregatorContextEntry
            = new MetricAggregatorContextEntry(new SumMetricAggregatorProvider(), false);

    public Map<String, MetricAggregatorContextEntry> getMetricAggregatorContext() {
        return metricAggregatorContext;
    }

    public void setMetricAggregatorContext(Map<String, MetricAggregatorContextEntry> metricAggregatorContext) {
        this.metricAggregatorContext = metricAggregatorContext;
    }

    public MetricAggregatorContextEntry getMetricAggregatorContextEntry(String metricName) {
        MetricAggregatorContextEntry metricAggregatorContextEntry = metricAggregatorContext.get(metricName);
        if (metricAggregatorContextEntry == null) {
            log.warn("Aggregator not found for metric '{}'; Using default aggregator.", metricName);
            metricAggregatorContextEntry = defaultMetricAggregatorContextEntry;
        }
        return metricAggregatorContextEntry;
    }

    public boolean addMetricAggregatorContextEntry(String metricName, MetricAggregatorProvider aggregatorProvider,
                                                   boolean plotData,  boolean override) {
        if (metricAggregatorContext.containsKey(metricName) && !override) {
            return false;
        }
        metricAggregatorContext.put(metricName, new MetricAggregatorContextEntry(aggregatorProvider, plotData));
        return true;
    }

    public class MetricAggregatorContextEntry {
        private MetricAggregatorProvider metricAggregatorProvider;
        private boolean plotData;

        public MetricAggregatorContextEntry(MetricAggregatorProvider metricAggregatorProvider, boolean plotData) {
            this.metricAggregatorProvider = metricAggregatorProvider;
            this.plotData = plotData;
        }

        public MetricAggregatorProvider getMetricAggregatorProvider() {
            return metricAggregatorProvider;
        }

        public boolean getPlotData() {
            return plotData;
        }
    }
}
