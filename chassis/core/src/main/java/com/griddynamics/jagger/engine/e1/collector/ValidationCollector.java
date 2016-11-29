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
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.RESULT;

public class ValidationCollector extends ScenarioCollector<Object, Object, Object> {
    private static final Logger log = LoggerFactory.getLogger(ValidationCollector.class);

    private final ResponseValidator<Object, Object, Object> validator;
    private int invoked = 0;
    private int failed = 0;

    public ValidationCollector(String sessionId, String taskId, NodeContext kernelContext, ResponseValidator<Object, Object, Object> validator) {
        super(sessionId, taskId, kernelContext);
        this.validator = validator;
    }

    @Override
    public void flush() {
        log.debug("Going to store validation result in key-value storage");

        Namespace namespace = namespace();

        KeyValueStorage keyValueStorage = kernelContext.getService(KeyValueStorage.class);

        keyValueStorage.put(namespace, RESULT, ValidationResult.create(validator.getName(), invoked, failed));

        log.debug("invoked {} failed {}", invoked, failed);
    }

    private Namespace namespace() {
        return Namespace.of(sessionId, taskId, "ValidationCollector",
                kernelContext.getId().toString());
    }

    @Override
    public void onStart(Object query, Object endpoint) {
        invoked++;
    }

    @Override
    public void onSuccess(Object query, Object endpoint, Object result, long duration) {
        boolean success = validator.validate(query, endpoint, result, duration);
        if (!success) {
            failed++;
        }
    }

    @Override
    public void onFail(Object query, Object endpoint, InvocationException e) {
    }

    @Override
    public void onError(Object query, Object endpoint, Throwable error) {
    }
}
