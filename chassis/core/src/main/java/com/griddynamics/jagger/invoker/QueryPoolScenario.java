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

import com.griddynamics.jagger.util.Nothing;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.util.SystemClock;

import java.util.Iterator;

/**
 * Presents a query-pool scenario that uses {@link QueryPoolLoadBalancer} for load
 * balancing.
 *
 * @param <Q> query type
 * @param <R> result type
 * @param <E> endpoint type
 * @author Mairbek Khadikov
 */
public class QueryPoolScenario<Q, R, E> extends Scenario<Q, R, E> {
    private final Invoker<Q, R, E> invoker;
    private final Iterator<Pair<Q, E>> chunks;
    private final SystemClock systemClock;

    public QueryPoolScenario(Invoker<Q, R, E> invoker, Iterator<Pair<Q, E>> chunks, SystemClock systemClock) {
        this.invoker = invoker;
        this.chunks = chunks;
        this.systemClock = systemClock;
    }

    @Override
    public void doTransaction() {
        Pair<Q, E> chunk = chunks.next();
        Q query = chunk.getFirst();
        E endpoint = chunk.getSecond();

        invoker().invoke(query, endpoint);

    }

    private Invoker<Q, Nothing, E> invoker() {
        return Invokers.listenableInvoker(this.invoker, getInvocationListener(), systemClock);
    }

    @Override
    public String toString() {
        return "QueryPoolScenario{" +
                "invoker=" + invoker +
                ", chunks=" + chunks +
                ", systemClock=" + systemClock +
                '}';
    }
}
