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

package com.griddynamics.jagger.coordinator.async;

import com.griddynamics.jagger.coordinator.Command;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Utilities, mostly factory methods for asynchronous runners.
 *
 * @author Mairbek Khadikov
 */
public class Async {
    private Async() {

    }

    public static <C extends Command<R>, R extends Serializable> BlockingRunner<C, R> sync(AsyncRunner<C, R> runner) {
        return new BlockingRunner<C, R>(runner);
    }

    public static <R extends Serializable> CompositeAsyncCallback<R> compose(Iterable<AsyncCallback<R>> callbacks) {
    	return new CompositeAsyncCallback<R>(callbacks);
    }

    public static <R extends Serializable> CompositeAsyncCallback compose(AsyncCallback<R>... callbacks) {
    	return new CompositeAsyncCallback<R>(Arrays.asList(callbacks));
    }

    public static <C extends Command<R>, R extends Serializable> QueueRunner<C, R> queueRunner(AsyncRunner<C, R> runner) {
        return new QueueRunner<C, R>(runner);
    }

    public static <C extends Command<R>, R extends Serializable> CommandQueue<C, R> commandQueue(Iterable<C> commands) {
        return new CommandQueue<C, R>(commands);
    }

    public static <R extends Serializable> MemoCallback<R> memoCallback() {
        return new MemoCallback<R>();
    }

    public static AsyncCallback doNothing() {
        return DoNothingCallback.INSTANCE;
    }


    private static enum DoNothingCallback implements AsyncCallback<Serializable> {
        INSTANCE;

        @Override
        public void onSuccess(Serializable r) {
            // do nothing
        }

        @Override
        public void onFailure(Throwable throwable) {
            // do nothing
        }
    }
}
