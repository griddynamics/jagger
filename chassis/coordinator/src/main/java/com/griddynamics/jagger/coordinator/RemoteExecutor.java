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

import com.griddynamics.jagger.coordinator.async.AsyncCallback;
import com.griddynamics.jagger.util.Timeout;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * Implementation of this interface is able to execute command on remote node.
 *
 * @author Mairbek Khadikov
 */
public interface RemoteExecutor extends Serializable {
    /**
     * Runs asynchronously command on remote node.
     *
     * @param command  command to be scheduled
     * @param listener listener that executes remotely
     * @param callback callback that is called when execution is done
     * @param <C>      type of command
     * @param <R>      type of result
     */
    <C extends Command<R>, R extends Serializable> void run(C command, NodeCommandExecutionListener<C> listener, AsyncCallback<R> callback);

    <C extends Command<R>, R extends Serializable> R runSyncWithTimeout(C command, NodeCommandExecutionListener<C> listener, long millis);

    <C extends Command<R>, R extends Serializable> R runSyncWithTimeout(C command, NodeCommandExecutionListener<C> listener, Timeout millis);

    <C extends Command<R>, R extends Serializable> Future<R> run(C command, NodeCommandExecutionListener<C> listener);
}
