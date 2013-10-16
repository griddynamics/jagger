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
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MetricLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SuccessRateCollector<Q, R, E> extends MetricCollector<Q, R, E> {
    private final String name;
    private long startTime = 0;

    public SuccessRateCollector(String sessionId, String taskId, NodeContext kernelContext, String name)
    {
        super(sessionId, taskId, kernelContext,new SimpleMetricCalculator(),name);
        this.name = name;
    }

    @Override
    public void flush() {
    }

    @Override
    public void onStart(Object query, Object endpoint) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onSuccess(Object query, Object endpoint, Object result, long duration) {
        log(1);
    }

    @Override
    public void onFail(Object query, Object endpoint, InvocationException e) {
        log(0);
    }

    @Override
    public void onError(Object query, Object endpoint, Throwable error) {
        log(0);
    }

    private void log(long result) {
        String METRIC_MARKER = "METRIC";
        LogWriter logWriter = kernelContext.getService(LogWriter.class);
        logWriter.log(sessionId, taskId + File.separatorChar + METRIC_MARKER + File.separatorChar + name, kernelContext.getId().getIdentifier(),
                new MetricLogEntry(startTime, name, result));
    }
}