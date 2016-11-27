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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

/**
 * Wraps asynchronous runner. Makes possible to blocks the thread until asynchronous call is finished.
 *
 * @param <C> command type
 */
public class BlockingRunner<C extends Command<R>, R extends Serializable> implements AsyncRunner<C, R> {
    private static final Logger log = LoggerFactory.getLogger(BlockingRunner.class);

    private final AsyncRunner<C, R> delegate;
    private final CountDownLatch latch;

    /*package*/ BlockingRunner(AsyncRunner<C, R> delegate) {
        this.delegate = delegate;
        this.latch = new CountDownLatch(1);
    }

    @Override
    public void run(C command, AsyncCallback<R> asyncCallback) {
        final AsyncCallback<R> originalCallback = asyncCallback;
        log.debug("command scheduled");
        delegate.run(command, new AsyncCallback<R>() {
            @Override
            public void onSuccess(R result) {
            	log.debug("command executed");
                originalCallback.onSuccess(result);
                latch.countDown();
            }

            @Override
            public void onFailure(Throwable throwable) {
            	log.debug("command failed");
                originalCallback.onFailure(throwable);
                latch.countDown();
            }
        });

    }

    public void await() {
        try {
        	log.debug("waiting for execution");
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
