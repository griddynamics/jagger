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

package com.griddynamics.jagger.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ConfigurableExecutor implements ExecutorService {
    private static final Logger log = LoggerFactory.getLogger(ConfigurableExecutor.class);

    private final Object lock = new Object();

    private int corePoolSize;
    private int maximumPoolSize;

    private ThreadPoolExecutor delegate;
    private String nameFormat;

    private ThreadPoolExecutor delegate() {
        if (delegate == null) {
            synchronized (lock) {
                if (delegate == null) {
                    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(nameFormat).setUncaughtExceptionHandler(ExceptionLogger.INSTANCE).build();
                    delegate = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 180, TimeUnit.SECONDS,
                            new SynchronousQueue<Runnable>(), threadFactory);

                }
            }
        }
        return delegate;
    }

    @Required
    public void setNameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
    }

    @Required
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;

        log.info("corePoolSize={}", corePoolSize);
    }

    @Required
    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;

        log.info("maximumPoolSize={}", maximumPoolSize);
    }

    @Override
    public void shutdown() {
        delegate().shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate().shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate().isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate().isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return delegate().awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return delegate().submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return delegate().submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate().submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return delegate().invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return delegate().invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return delegate().invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate().invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        try {
            delegate().execute(command);
        } catch (RejectedExecutionException e) {
            log.warn("Command {} rejected. Pool size: core {} max {}. Active threads {}" +
                    "\n Exception {}", new Object[]{command, corePoolSize, maximumPoolSize, delegate().getActiveCount(), Throwables.getStackTraceAsString(e)});
        }
    }
}
