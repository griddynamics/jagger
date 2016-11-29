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

import com.google.common.collect.Lists;
import com.griddynamics.jagger.ForeverRunning;
import com.griddynamics.jagger.Terminable;

import java.util.List;
import java.util.concurrent.Executor;

public class Launches {
    private Launches() {

    }

    public static LaunchTask taskOf(Runnable runnable) {
        Terminable terminable = TerminateNothing.INSTANCE;
        if (runnable instanceof Terminable) {
            terminable = (Terminable) runnable;
        }
        return new DefaultLaunchTask(runnable, terminable);
    }

    public static LaunchManagerBuilder builder() {
        return new LaunchManagerBuilder();
    }

    private static class DefaultLaunchTask extends LaunchTask {
        private final Runnable runnable;

        private DefaultLaunchTask(Runnable runnable, Terminable terminable) {
            this.runnable = runnable;
            toTerminate(terminable);
        }

        @Override
        public void run() {
            runnable.run();
        }

        @Override
        public String toString() {
            return "DefaultLaunchTask{" +
                    "runnable=" + runnable +
                    '}';
        }
    }

    private static enum TerminateNothing implements Terminable {
        INSTANCE;


        @Override
        public void terminate() {
            // do nothing
        }
    }

    private static class LaunchGroupTask extends LaunchTask {
        private final List<LaunchTask> launchTasks;

        public LaunchGroupTask(List<LaunchTask> launchTasks) {
            this.launchTasks = launchTasks;
        }

        @Override
        public void run() {
            for (LaunchTask launchTask : launchTasks) {
                launchTask.run();
            }
        }

        @Override
        public void setExecutor(Executor executor) {
            super.setExecutor(executor);

            for (LaunchTask launchTask : launchTasks) {
                launchTask.setExecutor(getExecutor());
            }
        }

        @Override
        public void terminate() {
            super.terminate();

            for (LaunchTask launchTask : launchTasks) {
                launchTask.terminate();
            }
        }
    }


    public static class LaunchManagerBuilder {
        private final List<LaunchTask> mainTasks = Lists.newLinkedList();
        private final List<LaunchTask> backgroundTasks = Lists.newLinkedList();

        private LaunchManagerBuilder() {

        }

        public LaunchManagerBuilder addMainTask(LaunchTask task) {
            mainTasks.add(task);
            return this;
        }

        public LaunchManagerBuilder addBackgroundTask(LaunchTask task) {
            backgroundTasks.add(task);
            return this;
        }

        public LaunchManager build() {
            LaunchTask mainTask = taskOf(ForeverRunning.INSTANCE);
            if (!mainTasks.isEmpty()) {
                mainTask = new LaunchGroupTask(mainTasks);
            }
            return new LaunchManager(mainTask, backgroundTasks);
        }
    }
}
