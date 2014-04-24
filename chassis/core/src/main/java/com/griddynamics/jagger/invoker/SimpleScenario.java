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
import com.griddynamics.jagger.util.SystemClock;

/**
 * Is able to do transaction with single query on specified endpoint.
 *
 * @author Mairbek Khadikov
 */
public class SimpleScenario<Q, R, E> extends Scenario<Q, R, E> {
    private final Invoker<Q, R, E> invoker;
    private final Q query;
    private final E endpoint;
    private final SystemClock systemClock;

    public static <Q, R, E> SimpleScenario<Q, R, E> create(
            Invoker<Q, R, E> invoker, Q query, E endpoint, SystemClock systemClock) {
        return new SimpleScenario<Q, R, E>(invoker, query, endpoint, systemClock);
    }

    private SimpleScenario(Invoker<Q, R, E> invoker, Q query, E endpoint, SystemClock systemClock) {
        this.invoker = invoker;
        this.query = query;
        this.endpoint = endpoint;
        this.systemClock = systemClock;
    }

    @Override
    public void doTransaction() {
        invoker().invoke(query, endpoint);
    }

    private Invoker<Q, R, E> invoker() {
        return Invokers.listenableInvoker(this.invoker, getInvocationListener(), systemClock);
    }


    @Override
    public String toString() {
        return "SimpleScenario{" +
                "invoker=" + invoker +
                ", query=" + query +
                ", endpoint=" + endpoint +
                '}';
    }
}
