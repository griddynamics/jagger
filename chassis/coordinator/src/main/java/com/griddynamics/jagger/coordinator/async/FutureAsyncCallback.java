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

import com.google.common.util.concurrent.SettableFuture;

import java.io.Serializable;

public class FutureAsyncCallback<R extends Serializable> implements AsyncCallback<R> {

    private final SettableFuture<R> future;

    public static <R extends Serializable> FutureAsyncCallback<R> create(SettableFuture<R> future) {
        return new FutureAsyncCallback<R>(future);
    }

    public static <R extends Serializable> FutureAsyncCallback<R> create() {
        return create(SettableFuture.<R>create());
    }

    private FutureAsyncCallback(SettableFuture<R> future) {
        this.future = future;
    }

    @Override
    public void onSuccess(R result) {
        future.set(result);
    }

    @Override
    public void onFailure(Throwable throwable) {
        future.setException(throwable);
    }

    public SettableFuture<R> getFuture() {
        return future;
    }
}
