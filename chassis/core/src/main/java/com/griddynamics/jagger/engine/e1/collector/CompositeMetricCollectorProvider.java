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

/**
 * @author Nikolay Musienko
 *         Date: 22.03.13
 */
public class CompositeMetricCollectorProvider<Q, R, E>  implements KernelSideInitializableObjectProvider<ScenarioCollector<Q, R, E>> {

    DiagnosticCollectorProvider<Q, R, E> diagnosticCollectorProvider;
    MetricCollectorProvider<Q, R, E> metricCollectorProvider;


    @Override
    public void init(String sessionId, String taskId, NodeContext kernelContext) {
        metricCollectorProvider.init(sessionId, taskId, kernelContext);
    }

    public DiagnosticCollectorProvider getDiagnosticCollectorProvider() {
        return diagnosticCollectorProvider;
    }

    public void setDiagnosticCollectorProvider(DiagnosticCollectorProvider diagnosticCollectorProvider) {
        this.diagnosticCollectorProvider = diagnosticCollectorProvider;
    }

    public MetricCollectorProvider<Q, R, E> getMetricCollectorProvider() {
        return metricCollectorProvider;
    }

    public void setMetricCollectorProvider(MetricCollectorProvider<Q, R, E> metricCollectorProvider) {
        this.metricCollectorProvider = metricCollectorProvider;
    }

    @Override
    public ScenarioCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new CompositeMetricCollector<Q, R, E>(
                sessionId,
                taskId,
                kernelContext,
                diagnosticCollectorProvider.provide(sessionId, taskId, kernelContext),
                metricCollectorProvider.provide(sessionId, taskId, kernelContext)
                );
    }
}
