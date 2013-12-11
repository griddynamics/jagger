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

package com.griddynamics.jagger.coordinator;

import com.google.common.util.concurrent.SettableFuture;
import com.griddynamics.jagger.coordinator.async.*;
import com.griddynamics.jagger.util.Futures;
import com.griddynamics.jagger.util.Timeout;

import java.io.Serializable;
import java.util.concurrent.Future;

public abstract class AbstractRemoteExecutor implements RemoteExecutor {

    @Override
    public <C extends Command<R>, R extends Serializable> Future<R> run(C command, NodeCommandExecutionListener<C> listener) {
        final SettableFuture<R> future = SettableFuture.create();
        run(command, listener, FutureAsyncCallback.create(future));
        return future;
    }

    @Override
    public <C extends Command<R>, R extends Serializable> R runSyncWithTimeout(C command, NodeCommandExecutionListener<C> listener, long millis) {
        return runSyncWithTimeout(command, listener, new Timeout(millis,"no_name"));
    }

    @Override
    public <C extends Command<R>, R extends Serializable> R runSyncWithTimeout(C command, NodeCommandExecutionListener<C> listener, Timeout millis) {
        Future<R> future = run(command, listener);
        return Futures.get(future, millis);
    }
}
