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

package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CalibrationInfoCollector extends ScenarioCollector<Serializable, Serializable, Serializable> {
    private final Logger log = LoggerFactory.getLogger(CalibrationInfoCollector.class);

    private List<CalibrationInfo<Serializable, Serializable, Serializable>> buffer = Lists.newLinkedList();
    private Map<Pair<Object, Object>, Throwable> errors = Maps.newHashMap();

    public CalibrationInfoCollector(String sessionId, String taskId, NodeContext kernelContext) {
        super(sessionId, taskId, kernelContext);
    }

    @Override
    public void flush() {
        log.debug("Going to flush calibration info \n{}", buffer);
        LogWriter logWriter = kernelContext.getService(LogWriter.class);
        String logOwner = taskId + "/" + "Calibration";

        for (CalibrationInfo<Serializable, Serializable, Serializable> info : buffer) {
            logWriter.log(sessionId, logOwner, "kernel", info);
        }

    }

    @Override
    public void onStart(Serializable query, Serializable endpoint) {

    }

    @Override
    public void onSuccess(Serializable query, Serializable endpoint, Serializable result, long duration) {
        CalibrationInfo<Serializable, Serializable, Serializable> info = CalibrationInfo.create(query, endpoint, result);
        log.debug("Calibration: {}", info);
        buffer.add(info);
    }

    @Override
    public void onFail(Serializable query, Serializable endpoint, InvocationException e) {
        handleError(query, endpoint, e);
    }

    @Override
    public void onError(Serializable query, Serializable endpoint, Throwable error) {
        handleError(query, endpoint, error);
    }

    private void handleError(Object query, Object endpoint, Throwable e) {
        errors.put(Pair.of(query, endpoint), e);
    }

    public Map<Pair<Object, Object>, Throwable> getErrors() {
        return errors;
    }
}
