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

package com.griddynamics.jagger.invoker;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.engine.e1.collector.Validator;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.scenario.Flushable;
import com.griddynamics.jagger.util.Nothing;
import com.griddynamics.jagger.util.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Useful utility methods, mostly static factories, for invokers.
 *
 * @author Mairbek Khadikov
 */
public class Invokers {
    private static final Logger log = LoggerFactory.getLogger(Invokers.class);

    private Invokers() {

    }

    public static <Q, R, E> Invoker<Q, Nothing, E> listenableInvoker(Invoker<Q, R, E> invoker, InvocationListener<Q, R, E> invocationListener, SystemClock clock) {
        return new ListenableInvoker<Q, R, E>(invoker, invocationListener, clock);
    }

    public static <Q, R, E> CompositeLogLoadInvocationListener<Q, R, E> composeAndLogListeners(Iterable<? extends LoadInvocationListener<Q, R, E>> listeners) {
        return new CompositeLogLoadInvocationListener(listeners);
    }

    public static <Q, R, E> CompositeLogLoadInvocationListener<Q, R, E> composeListeners(LoadInvocationListener<Q, R, E>... listeners) {
        return new CompositeLogLoadInvocationListener<Q, R, E>(newArrayList(listeners));
    }

    public static <Q, R, E> ValidateInvocationListener<Q, R, E> validateListener(Iterable<Validator> validators, Iterable<? extends LoadInvocationListener<Q, R, E>> metrics, List<InvocationListener<Q, R, E>> listeners){
        return new ValidateInvocationListener<Q, R, E>(validators, metrics, listeners);
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

    public static <Q, R, E> InvocationListener<Q, R, E> emptyListener() {
        return new InvocationListener<Q, R, E>() {
            @Override
            public void onStart(InvocationInfo<Q, R, E> invocationInfo) {
            }

            @Override
            public void onSuccess(InvocationInfo<Q, R, E> invocationInfo) {
            }

            @Override
            public void onFail(InvocationInfo<Q, R, E> invocationInfo, InvocationException e) {
            }

            @Override
            public void onError(InvocationInfo<Q, R, E> invocationInfo, Throwable error) {
            }
        };
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
        private final SystemClock clock;
        private final InvocationListener invocationListener;

        private ListenableInvoker(Invoker<Q, R, E> invoker, InvocationListener invocationListener, SystemClock clock) {
            this.invoker = Preconditions.checkNotNull(invoker);
            this.clock = Preconditions.checkNotNull(clock);
            this.invocationListener = Preconditions.checkNotNull(invocationListener);
        }

        @Override
        public Nothing invoke(Q query, E endpoint) throws InvocationException {
            InvocationInfo<Q, R, E> invocationInfo = new InvocationInfo<Q, R, E>(query, endpoint);

            invocationListener.onStart(invocationInfo);
            long before = clock.currentTimeMillis();
            try {
                R result = invoker.invoke(query, endpoint);
                long after = clock.currentTimeMillis();
                long duration = after - before;
                invocationInfo.setDuration(duration);
                invocationInfo.setResult(result);

                invocationListener.onSuccess(invocationInfo);
            } catch (InvocationException e) {
                invocationListener.onFail(invocationInfo, e);
            } catch (Throwable throwable) {
                invocationListener.onError(invocationInfo, throwable);
            }
            return Nothing.INSTANCE;
        }
    }
}
