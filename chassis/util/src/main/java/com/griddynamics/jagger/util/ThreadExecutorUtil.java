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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * User: vshulga
 * Date: 6/11/11
 * Time: 9:44 PM
 */
public final class ThreadExecutorUtil {
    private static final Logger log = LoggerFactory.getLogger(ThreadExecutorUtil.class);

    private Executor executor;
    private Set<String> launchedTasks;

    private static class InstanceHolder {
        private static ThreadExecutorUtil instance = new ThreadExecutorUtil();
    }

    private ThreadExecutorUtil() {
        this.executor = Executors.newFixedThreadPool(4, new DaemonThreadFactory());
        this.launchedTasks = new HashSet<String>();
    }

    public static ThreadExecutorUtil getInstance() {
        return InstanceHolder.instance;
    }

    public boolean addTask(Runnable task, final Object identifier) {
        log.info("Task {} added to thread executor.", identifier);
        final String taskIdentifier = generateTaskIdentifierEntry(task.getClass(), identifier);
        if (!launchedTasks.contains(taskIdentifier)) {
            launchedTasks.add(taskIdentifier);
            executor.execute(task);
            log.info("addTask {} return true", identifier);
            return true;
        }
        log.info("addTask {} return false", identifier);
        return false;
    }

    private String generateTaskIdentifierEntry(Class clazz, Object identifier) {
        return clazz.getName() + identifier;
    }

    public synchronized void removeTask(Class clazz, Object identifier) {
        launchedTasks.remove(generateTaskIdentifierEntry(clazz, identifier));
    }

    private class DaemonThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }

    }

    public void addTask(Runnable task) {
        executor.execute(task);
    }
}
