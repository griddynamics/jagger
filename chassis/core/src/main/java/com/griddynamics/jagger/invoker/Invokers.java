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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.scenario.Flushable;
import com.griddynamics.jagger.util.Nothing;
import com.griddynamics.jagger.util.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Useful utility methods, mostly static factories, for ixnvokers.
 *
 * @author Mairbek Khadikov
 */
public class Invokers {
    private static final Logger log = LoggerFactory.getLogger(Invokers.class);

    private Invokers() {

    }

    public static <Q, R, E> Invoker<Q, Nothing, E> listenableInvoker(Invoker<Q, R, E> invoker, LoadInvocationListener<Q, R, E> listener, SystemClock clock) {
        return new ListenableInvoker<Q, R, E>(invoker, listener, clock);
    }

    public static <Q, R, E> CompositeLoadInvocationListener<Q, R, E> composeListeners(Iterable<? extends LoadInvocationListener<Q, R, E>> listeners) {
        return new CompositeLoadInvocationListener(listeners);
    }

    public static <Q, R, E> CompositeLoadInvocationListener<Q, R, E> composeListeners(LoadInvocationListener<Q, R, E>... listeners) {
        return new CompositeLoadInvocationListener<Q, R, E>(newArrayList(listeners));
    }

    public static <Q, R, E> ValidateLoadInvocationListener<Q, R, E> validateListener(Iterable<Validator> validators, Iterable<? extends LoadInvocationListener<Q, R, E>> listeners){
        return new ValidateLoadInvocationListener<Q, R, E>(validators, listeners);
    }

    public static ImmutableList<Flushable> mergeFlushElements(Collection<? extends Flushable>... sources){
        ImmutableList.Builder<Flushable> builder = ImmutableList.builder();

        for (Collection<? extends Flushable> source : sources){
            builder.addAll(source);
        }

        return builder.build();
    }

    public static <Q, R, E> ErrorLoggingListener<Q, R, E> logErrors(
            LoadInvocationListener<Q, R, E> listener) {
        return new ErrorLoggingListener<Q, R, E>(listener);
    }

    /**
     * @return listener that ignores all events.
     */
    @SuppressWarnings("unchecked")
    public static <Q, R, E> LoadInvocationListener<Q, R, E> doNothing() {
        return DoNothing.INSTANCE;
    }

    @SuppressWarnings("rawtypes")
    private enum DoNothing implements LoadInvocationListener {
        INSTANCE;

        @Override
        public void onStart(Object query, Object configuration) {

        }

        @Override
        public void onSuccess(Object query, Object configuration, Object result, long duration) {

        }

        @Override
        public void onFail(Object query, Object configuration, InvocationException e) {

        }

        @Override
        public void onError(Object query, Object configuration, Throwable error) {

        }
    }

    private static class ListenableInvoker<Q, R, E> implements Invoker<Q, Nothing, E> {
        private final Invoker<Q, R, E> invoker;
        private final LoadInvocationListener<Q, R, E> listener;
        private final SystemClock clock;

        private ListenableInvoker(Invoker<Q, R, E> invoker, LoadInvocationListener<Q, R, E> listener, SystemClock clock) {
            this.invoker = Preconditions.checkNotNull(invoker);
            this.listener = Preconditions.checkNotNull(listener);
            this.clock = Preconditions.checkNotNull(clock);
        }

        @Override
        public Nothing invoke(Q query, E endpoint) throws InvocationException {
            LoadInvocationListener<Q, R, E> listener = logErrors(composeListeners(this.listener, LoadInvocationLogger.<Q, R, E>create()));
            listener.onStart(query, endpoint);
            long before = clock.currentTimeMillis();
            try {
                R result = invoker.invoke(query, endpoint);
                long after = clock.currentTimeMillis();
                long duration = after - before;
                listener.onSuccess(query, endpoint, result, duration);
            } catch (InvocationException e) {
                listener.onFail(query, endpoint, e);
            } catch (Throwable throwable) {
                listener.onError(query, endpoint, throwable);
            }
            return Nothing.INSTANCE;
        }
    }
}
