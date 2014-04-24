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

package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.services.JaggerPlace;
import com.griddynamics.jagger.engine.e1.services.ServicesInitializable;
import com.griddynamics.jagger.util.JavaSystemClock;
import com.griddynamics.jagger.util.SystemClock;

public class QueryPoolScenarioFactory<Q, R, E> implements ScenarioFactory<Q, R, E>, ServicesInitializable {
    private InvokerWrapper<Q, R, E> invokerWrapper;
    private QueryPoolLoadBalancer<Q, E> loadBalancer;
    private SystemClock systemClock = new JavaSystemClock();

    private Iterable<Q> queryProvider;
    private Iterable<E> endpointProvider;

    private int calibrationSamplesCount = -1;

    @Override
    public Scenario<Q, R, E> get(NodeContext nodeContext) {
        Invoker<Q, R, E> invoker = invokerWrapper.getInvoker(nodeContext);

        if (endpointProvider!=null) loadBalancer.setEndpointProvider(getEndpointProvider());
        if (queryProvider!=null)    loadBalancer.setQueryProvider(getQueryProvider());
        return new QueryPoolScenario<Q, R, E>(invoker, loadBalancer.provide(), systemClock);
    }

    public void setInvokerWrapper(InvokerWrapper<Q, R, E> invokerWrapper) {
        this.invokerWrapper = invokerWrapper;
    }

    // need for old configuration
    @Deprecated
    public void setInvokerClazz(Class<Invoker<Q, R, E>> invokerClazz) {
        InvokerWrapper<Q, R, E> wrapper = new InvokerWrapper<Q, R, E>();
        wrapper.setInvokerClazz(invokerClazz);
        setInvokerWrapper(wrapper);
    }

    //@Required
    public void setLoadBalancer(QueryPoolLoadBalancer<Q, E> loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public void setSystemClock(SystemClock systemClock) {
        this.systemClock = systemClock;
    }

    @Override
    public int getCalibrationSamplesCount() {
        if(calibrationSamplesCount != -1){
            return calibrationSamplesCount;
        }
        return loadBalancer.querySize();
    }

    public void setCalibrationSamplesCount(int calibrationSamplesCount) {
        this.calibrationSamplesCount = calibrationSamplesCount;
    }

    public Iterable<Q> getQueryProvider() {
        return queryProvider;
    }

    public void setQueryProvider(Iterable<Q> queryProvider) {
        this.queryProvider = queryProvider;
    }

    public Iterable<E> getEndpointProvider() {
        return endpointProvider;
    }

    public void setEndpointProvider(Iterable<E> endpointProvider) {
        this.endpointProvider = endpointProvider;
    }

    @Override
    public void initServices(String sessionId, String taskId, NodeContext context, JaggerPlace environment) {
        invokerWrapper.initServices(sessionId, taskId, context, environment);
    }
}
