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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.coordinator.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class QueueRunner<C extends Command<R>, R extends Serializable> implements AsyncRunner<CommandQueue<C, R>, QueueResult<R>> {
    private static final Logger log = LoggerFactory.getLogger(QueueRunner.class);

    private final AsyncRunner<C, R> delegate;

    /* package */QueueRunner(AsyncRunner<C, R> delegate) {
        this.delegate = delegate;
    }


    private void run(final Iterator<C> iterator, final List<R> results, final AsyncCallback<QueueResult<R>> originalCallback) {
        if (!iterator.hasNext()) {
            originalCallback.onSuccess(new QueueResult<R>(results));
            return;
        }

        final C command = iterator.next();
        log.debug("Going to execute command {}", command);
        AsyncCallback<R> callback = new AsyncCallback<R>() {
            @Override
            public void onSuccess(R result) {
                log.debug("Command {} Successfully executed", command);
                results.add(result);
                run(iterator, results, originalCallback);
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.debug("Failed during {} execution", command);
                originalCallback.onFailure(throwable);
            }
        };

        delegate.run(command, callback);
    }

    @Override
    public void run(CommandQueue<C, R> command, AsyncCallback<QueueResult<R>> callback) {
        List<R> list = Lists.newLinkedList();
        run(command.getCommands().iterator(), list, callback);
    }

}
