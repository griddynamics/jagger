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
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.storage.fs.logging.MetricLogEntry;

import java.io.File;

/** Collects number of pass and fail invokes
 * @author Dmitry Latnikov
 * @n
 * @par Details:
 * @details Collects number of pass and fail invokes. @n
 * Success rate and number of fails are calculated from this data. @n
 * Calculation is provided by aggregators: @n
 * @li @ref SuccessRateAggregatorProvider @n
 * @li @ref SuccessRateFailsAggregatorProvider @n
 *
 * @n
 * @par Result example:
 * @image html jagger_success_rate_default_agg.png "Success rate data with default accumulative aggregators"
 * @n
 * @n
 * @image html jagger_success_rate_avg_agg.png "Success rate data with aggregator: average on interval"
 * @n
 */
public class SuccessRateCollector<Q, R, E> extends MetricCollector<Q, R, E> {
    private final String name;
    private long startTime = 0;

    /** Default constructor */
    public SuccessRateCollector(String sessionId, String taskId, NodeContext kernelContext, String name)
    {
        super(sessionId, taskId, kernelContext,new SimpleMetricCalculator(),name);
        this.name = name;
    }

    /** Method is not used for this collector => disabled */
    @Override
    public void flush() {
    }

    /** Method is called before invoke to save invoke start time. Later is used for logging */
    @Override
    public void onStart(Object query, Object endpoint) {
        startTime = System.currentTimeMillis();
    }

    /** Method is called when invoke was successful */
    @Override
    public void onSuccess(Object query, Object endpoint, Object result, long duration) {
        log(1);
    }

    /** Method is called when invoke failed */
    @Override
    public void onFail(Object query, Object endpoint, InvocationException e) {
        log(0);
    }

    /** Method is called when some error occurred */
    @Override
    public void onError(Object query, Object endpoint, Throwable error) {
        log(0);
    }

    private void log(long result) {
        LogWriter logWriter = kernelContext.getService(LogWriter.class);
        logWriter.log(sessionId, taskId + File.separatorChar + METRIC_MARKER + File.separatorChar + name, kernelContext.getId().getIdentifier(),
                new MetricLogEntry(startTime, name, result));
    }
}