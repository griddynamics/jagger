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

import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.griddynamics.jagger.invoker.Invokers.doNothing;
import static com.griddynamics.jagger.invoker.Invokers.emptyListener;

/**
 * Encapsulates algorithm of load testing.
 *
 * @author Mairbek Khadikov
 */
public abstract class Scenario<Q, R, E> {

    // strange program design - listeners look like foreign elements
    private LoadInvocationListener<Q, R, E> listener = doNothing();
    private InvocationListener<Q, R, E> invocationListener = emptyListener();

    @Deprecated
    protected LoadInvocationListener<Q, R, E> getListener() {
        return listener;
    }

    // from 1.2.4 all listeners are wrapped in loadInvocationListener
    @Deprecated
    public void setListener(LoadInvocationListener<Q, R, E> listener) {
        checkNotNull(listener);
        this.listener = Invokers.logErrors(listener);
    }

    protected InvocationListener<Q, R, E> getInvocationListener(){
        return invocationListener;
    }

    public void setInvocationListener(InvocationListener<Q, R, E> invocationListener) {
        checkNotNull(invocationListener);
        this.invocationListener = invocationListener;
    }

    public abstract void doTransaction();

}
