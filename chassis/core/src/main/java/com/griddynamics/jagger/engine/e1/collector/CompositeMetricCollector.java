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

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.invoker.InvocationException;

/**
 * @author Nikolay Musienko
 *         Date: 22.03.13
 */

public class CompositeMetricCollector<Q, R, E> extends ScenarioCollector<Q, R, E> {
    ScenarioCollector<Q, R, E> diagnosticCollector;
    ScenarioCollector<Q, R, E> metricCollector;

    public CompositeMetricCollector(String sessionId, String taskId, NodeContext kernelContext,
                                    ScenarioCollector<Q, R, E> diagnosticCollector,
                                    ScenarioCollector<Q, R, E> metricCollector) {
        super(sessionId, taskId, kernelContext);
        this.diagnosticCollector=diagnosticCollector;
        this.metricCollector=metricCollector;
    }

    @Override
    public void flush() {
        diagnosticCollector.flush();
        metricCollector.flush();
    }

    @Override
    public void onStart(Q query, E endpoint) {
        diagnosticCollector.onStart(query, endpoint);
        metricCollector.onStart(query, endpoint);
    }

    @Override
    public void onSuccess(Q query, E endpoint, R result, long duration) {
        diagnosticCollector.onSuccess(query, endpoint, result, duration);
        metricCollector.onSuccess(query, endpoint, result, duration);
    }

    @Override
    public void onFail(Q query, E endpoint, InvocationException e) {
        diagnosticCollector.onFail(query, endpoint, e);
        metricCollector.onFail(query, endpoint, e);
    }

    @Override
    public void onError(Q query, E endpoint, Throwable error) {
        diagnosticCollector.onError(query, endpoint, error);
        metricCollector.onError(query, endpoint, error);
    }
}
