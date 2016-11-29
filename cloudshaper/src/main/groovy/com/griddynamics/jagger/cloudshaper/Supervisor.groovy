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

package com.griddynamics.jagger.cloudshaper

import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.Future

public class Supervisor {
    public hostConfigurations
    public actionConfigurations
    public supervisionLoopInterval
    public stop = false
    public numberOfExecutionThreads
    public isDryRun

    private SshExecutor sshExecutor;
    private ExecutorService executor;

    public startTime;

    def run = {

        executor = Executors.newFixedThreadPool(numberOfExecutionThreads)
        sshExecutor = new SshExecutor(isDryRun)

        int tick = 0;
        try {

            startTime = new Date()

            println("====== Execute Primary Actions : start")
            while (!stop) {
                tick++
                executeOnAll(tick, "primaryActions")

                Thread.sleep(supervisionLoopInterval)
            }
            println("====== Execute Primary Actions : end\n")

        } finally {

            println("====== Execute Final Actions : start")
            try {
                executeOnAll(tick, "finalActions")
            } finally {
                executor.shutdown()
            }
            println("====== Execute Final Actions : end")

        }
    }

    def runAsync = {
        final supervisor = this;
        new Thread() {
            public void run() {
                supervisor.run()
            }
        }.start()
    }

    def executeOnAll = {
        tick, commandType ->
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>()

        for (host in hostConfigurations) {
            tasks.add( new Callable<Void>() {
                public Void call() {
                    for(conf in actionConfigurations[commandType]) {
                        def context = new CommandContext(tick, host, startTime.getTime(), System.currentTimeMillis())

                        if( conf["trigger"](context) ) {
                            def result = sshExecutor.executeViaSsh(context, conf["command"])
                            if(result != null) {
                                println(result)
                            }
                        }
                    }
                    return null;
                }
            })
        }

        List<Future<Void>> handlers = executor.invokeAll(tasks)

        for(handler in handlers) {
            handler.get();
        }
    }
}
