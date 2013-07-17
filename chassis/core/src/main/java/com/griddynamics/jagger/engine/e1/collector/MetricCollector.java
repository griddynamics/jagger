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
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MetricLogEntry;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 18.03.13
 * Time: 16:05
 * To change this template use File | Settings | File Templates.
 */
public class MetricCollector<Q, R, E> extends ScenarioCollector<Q, R, E> {
    private final MetricCalculator<R> metricCalculator;
    private final String name;
    public static final String METRIC_MARKER = "METRIC";


    public MetricCollector(String sessionId, String taskId, NodeContext kernelContext, MetricCalculator metricCalculator, String name) {
        super(sessionId, taskId, kernelContext);
        this.name = name;
        this.metricCalculator=metricCalculator;
    }

    @Override
    public void flush() {
    }

    @Override
    public void onStart(Q query, E endpoint) {
    }

    @Override
    public void onSuccess(Q query, E endpoint, R result, long duration) {
        Long endTime = System.currentTimeMillis();
        LogWriter logWriter = kernelContext.getService(LogWriter.class);
        long startTime = endTime - duration;
        logWriter.log(sessionId, taskId + File.separatorChar + METRIC_MARKER + File.separatorChar + name, kernelContext.getId().getIdentifier(),
                new MetricLogEntry(startTime, name,  metricCalculator.calculate(result)));
    }

    @Override
    public void onFail(Q query, E endpoint, InvocationException e) {
    }

    @Override
    public void onError(Q query, E endpoint, Throwable error) {
    }
}