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

/**
 * Composes several {@link LoadInvocationListener} instances into one.
 *
 * @author Mairbek Khadikov
 */
public class CompositeLogLoadInvocationListener<Q, R, E>
        implements LoadInvocationListener<Q, R, E> {

    private LoadInvocationLogger loggerListener = LoadInvocationLogger.<Q, R, E>create();

    private final Iterable<LoadInvocationListener<Q, R, E>> listeners;

    public CompositeLogLoadInvocationListener(Iterable<LoadInvocationListener<Q, R, E>> listeners) {
        this.listeners = listeners;
    }

    public void onStart(Q query, E endpoint) {
        loggerListener.onStart(query, endpoint);
        for (LoadInvocationListener<Q, R, E> listener : listeners) {
            listener.onStart(query, endpoint);
        }
    }

    public void onSuccess(Q query, E endpoint, R result, long duration) {
        loggerListener.onSuccess(query, endpoint, result, duration);
        for (LoadInvocationListener<Q, R, E> listener : listeners) {
            listener.onSuccess(query, endpoint, result, duration);
        }
    }

    public void onFail(Q query, E endpoint, InvocationException e) {
        loggerListener.onFail(query, endpoint, e);
        for (LoadInvocationListener<Q, R, E> listener : listeners) {
            listener.onFail(query, endpoint, e);
        }
    }

    public void onError(Q query, E endpoint, Throwable error) {
        loggerListener.onError(query, endpoint, error);
        for (LoadInvocationListener<Q, R, E> listener : listeners) {
            listener.onError(query, endpoint, error);
        }
    }
}
