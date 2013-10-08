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
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collects diagnostic information. The metrics calculation algorithm is
 * specified in passed {@link MetricCalculator} implementation.
 * 
 * @author Mairbek Khadikov
 * 
 */
public class DiagnosticCollector<Q, R, E> extends ScenarioCollector<Q, R, E> {
	private static final Logger log = LoggerFactory.getLogger(DiagnosticCollector.class);
	private final MetricCalculator<R> metricCalculator;
    private final String name;

	private double metric = 0;

    public DiagnosticCollector(String sessionId, String taskId, NodeContext kernelContext, MetricCalculator<R> metricCalculator, String name) {
        super(sessionId, taskId, kernelContext);
        this.metricCalculator = metricCalculator;
        this.name = name;
    }

    @Override
    public void flush() {

		Namespace namespace = Namespace.of(sessionId, taskId, "DiagnosticCollector", kernelContext
				.getId().toString());
		kernelContext.getService(KeyValueStorage.class).put(namespace, "metric", DiagnosticResult.create(name, metric));
    }

    @Override
    public void onStart(Q query, E endpoint) {
        // do nothing
    }

    @Override
    public void onSuccess(Q query, E endpoint, R result, long duration) {
		metric += metricCalculator.calculate(result).doubleValue();
    }

    @Override
    public void onFail(Q query, E endpoint, InvocationException e) {
        // do nothing
    }

    @Override
    public void onError(Q query, E endpoint, Throwable error) {
        // do nothing
    }

}
