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

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideInitializableObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;

import java.util.List;

/**
 * @author Nikolay Musienko
 *         Date: 18.03.13
 */

public class MetricCollectorProvider<Q, R, E> implements KernelSideInitializableObjectProvider<ScenarioCollector<Q, R, E>> {
    private MetricCalculator<R> metricCalculator;
    private String name;
    private List<MetricDescriptionEntry> aggregators;

    @Override
    public void init(String sessionId, String taskId, NodeContext kernelContext) {
        KeyValueStorage storage = kernelContext.getService(KeyValueStorage.class);
        storage.put(Namespace.of(
                sessionId, taskId, "metricAggregatorProviders"),
                name,
                aggregators
        );
    }

    @Override
    public ScenarioCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new MetricCollector<Q, R, E>(sessionId, taskId, kernelContext, metricCalculator, name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public MetricCalculator<R> getMetricCalculator() {
        return metricCalculator;
    }

    public String getName() {
        return name;
    }

    public void setAggregators(List<MetricDescriptionEntry> aggregators) {
        this.aggregators = aggregators;
    }

    public void setMetricCalculator(MetricCalculator<R> metricCalculator) {
        this.metricCalculator = metricCalculator;
    }

    public List<MetricDescriptionEntry> getAggregators() {
        return aggregators;
    }

    public static final class MetricDescriptionEntry {
        private boolean needPlotData;
        private MetricAggregatorProvider metricAggregatorProvider;

        public MetricDescriptionEntry(MetricAggregatorProvider metricAggregatorProvider, boolean needPlotData) {
            this.needPlotData = needPlotData;
            this.metricAggregatorProvider = metricAggregatorProvider;
        }

        public MetricDescriptionEntry() {
        }

        public boolean isNeedPlotData() {
            return needPlotData;
        }

        public void setNeedPlotData(boolean needPlotData) {
            this.needPlotData = needPlotData;
        }

        public MetricAggregatorProvider getMetricAggregatorProvider() {
            return metricAggregatorProvider;
        }

        public void setMetricAggregatorProvider(MetricAggregatorProvider metricAggregatorProvider) {
            this.metricAggregatorProvider = metricAggregatorProvider;
        }
    }
}
