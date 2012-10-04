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

import com.google.common.base.Equivalence;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.util.Eq;

public class ConsistencyValidatorProvider<Q, E, R> implements KernelSideObjectProvider<ConsistencyValidator<Q, E, R>> {
    private Equivalence<Q> queryEq = (Equivalence<Q>) Equivalence.equals();
    private Equivalence<E> endpointEq = (Equivalence<E>) Eq.alwaysTrue(); // ignore endpoints by default
    private Equivalence<R> resultEq = (Equivalence<R>) Equivalence.equals();

    @Override
    public ConsistencyValidator<Q, E, R> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new ConsistencyValidator<Q, E, R>(taskId, kernelContext, sessionId, queryEq, endpointEq, resultEq);
    }

    public void setQueryEq(Equivalence<Q> queryEq) {
        this.queryEq = queryEq;
    }

    public void setEndpointEq(Equivalence<E> endpointEq) {
        this.endpointEq = endpointEq;
    }

    public void setResultEq(Equivalence<R> resultEq) {
        this.resultEq = resultEq;
    }
}
