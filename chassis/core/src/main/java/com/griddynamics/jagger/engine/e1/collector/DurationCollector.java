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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.storage.fs.logging.DurationLogEntry;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.*;

/**
 * Collects duration of the invocation.
 *
 * @author Mairbek Khadikov
 */
public class DurationCollector extends ScenarioCollector {
    public static final String DURATION_MARKER = "DURATION";
    private static final Logger log = LoggerFactory.getLogger(DurationCollector.class);

    private double totalDuration = 0;
    private double totalDurationSqr = 0;

    public DurationCollector(String sessionId, String taskId, NodeContext kernelContext) {
        super(sessionId, taskId, kernelContext);
    }

    @Override
    public void onStart(Object query, Object endpoint) {
    }

    @Override
    public void onSuccess(Object query, Object endpoint, Object result, long duration) {
        rememberDuration(duration);
    }

    @Override
    public void onFail(Object query, Object endpoint, InvocationException e) {
    }

    @Override
    public void onError(Object query, Object endpoint, Throwable error) {
    }

    private void rememberDuration(long duration) {
        Long endTime = System.currentTimeMillis();

        double durationSeconds = (double) duration / 1000;
        totalDuration = totalDuration + durationSeconds;
        totalDurationSqr = totalDurationSqr + (durationSeconds * durationSeconds);

        LogWriter logWriter = kernelContext.getService(LogWriter.class);
        long startTime = endTime - duration;
        logWriter.log(sessionId, taskId + File.separatorChar + DURATION_MARKER, kernelContext.getId().getIdentifier(),
                new DurationLogEntry(startTime, duration));
    }

    @Override
    public void flush() {
        Namespace namespace = Namespace.of(sessionId, taskId, "DurationCollector", kernelContext.getId().toString());
        kernelContext.getService(KeyValueStorage.class).put(namespace, TOTAL_DURATION, totalDuration);
        log.debug("saved total_duration for namespace {}", namespace);
        kernelContext.getService(KeyValueStorage.class).put(namespace, TOTAL_SQR_DURATION, totalDurationSqr);
        log.debug("saved total_sqr_duration for namespace {}", namespace);
    }

    private static final long serialVersionUID = 8230019294210626531L;
}
