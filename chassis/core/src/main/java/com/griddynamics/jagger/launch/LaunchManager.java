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

package com.griddynamics.jagger.launch;

import com.griddynamics.jagger.DaemonThreadFactory;
import com.griddynamics.jagger.Terminable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LaunchManager {
    private static final Logger log = LoggerFactory.getLogger(LaunchManager.class);

    private final LaunchTask mainTask;
    private final List<LaunchTask> backgroundTasks;
    private final ExecutorService executor;

    private Throwable error;
    private CountDownLatch latch;

    public LaunchManager(LaunchTask mainTask, List<LaunchTask> backgroundTasks) {
        this.mainTask = mainTask;
        this.backgroundTasks = backgroundTasks;
        this.executor = Executors.newCachedThreadPool(DaemonThreadFactory.INSTANCE);
    }

    public int launch() {
        log.info("Going to launch all tasks");
        latch = new CountDownLatch(1);

        log.debug("Executing background tasks");
        for (LaunchTask task : backgroundTasks) {
            log.debug("Executing task", task);
            execute(task, false);
        }

        log.debug("Executing main tasks");
        execute(mainTask, true);

        log.debug("Waiting for main task to be done");
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.debug("Launch finished");

        log.debug("Terminating background tasks");
        terminateBackgroundTasks();

        executor.shutdown();

        int result = 0;
        if (error != null) {
            log.error("Error during launch", error);
            result = 1;
        }

        return result;
    }

    private void terminateBackgroundTasks() {
        for (Terminable terminable : backgroundTasks) {
            try {
                terminable.terminate();
            } catch (Throwable t) {
                log.warn("Cannot terminate {}. Exception : {}", terminable, t);
            }
        }
    }

    private void execute(LaunchTask task, boolean b) {
        task.setExecutor(new InternalExecutor(executor));

        executor.execute(new InternalRunner(task, b));
    }

    private class InternalRunner implements Runnable {
        private final Runnable delegate;
        private final boolean main;

        public InternalRunner(Runnable delegate, boolean main) {
            this.delegate = delegate;
            this.main = main;
        }

        @Override
        public void run() {
            try {
                delegate.run();
                if (main) {
                    latch.countDown();
                }
            } catch (Throwable t) {
                error = t;
                latch.countDown();
            }
        }
    }

    private class InternalExecutor implements Executor {
        private final Executor delegate;

        public InternalExecutor(Executor delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(Runnable command) {
            delegate.execute(new InternalRunner(command, false));
        }
    }
}
