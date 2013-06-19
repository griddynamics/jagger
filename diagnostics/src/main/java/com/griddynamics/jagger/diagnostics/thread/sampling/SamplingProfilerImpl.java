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

package com.griddynamics.jagger.diagnostics.thread.sampling;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.griddynamics.jagger.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class SamplingProfilerImpl implements SamplingProfiler {

    private static final Logger log = LoggerFactory.getLogger(SamplingProfilerImpl.class);
    private int jmxTimeout = 300;

    private ThreadInfoProvider threadInfoProvider;
    private long pollingInterval;
    private List<Pattern> includePatterns;
    private List<Pattern> excludePatterns;
    private ThreadPoolExecutor jmxThreadPoolExecutor = createJMXThreadPoolExecutor();

    public void setJmxTimeout(int jmxTimeout) {
        this.jmxTimeout = jmxTimeout;
    }

    private ThreadPoolExecutor createJMXThreadPoolExecutor() {
        log.debug("Create new JMX thread pool executor.");
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    private Map<String, RuntimeGraph> runtimeGraphs;

    private PollingThread pollingThread;

    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private String identifier = "" + new Random().nextInt(100);

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void startPolling() throws InterruptedException {
        if (isRunning.compareAndSet(false, true)) {

            log.debug("SamplingProfiler {} : Starting profiling", identifier);

            runtimeGraphs = Maps.newConcurrentMap();

            pollingThread = new PollingThread();
            pollingThread.start();
        } else {
            log.error("Polling is already started!!!! Polling will be stopped, cleaned and restarted!");
            stopPolling();
            pollingThread.join();
            isRunning.set(false);
            startPolling();
        }
    }

    @Override
    public void stopPolling() {
        log.debug("SamplingProfiler {} : Stopping profiling", identifier);
        isRunning.set(false);
    }

    private class PollingThread extends Thread {

        public void run() {

            for (String serviceURL : threadInfoProvider.getIdentifiersSuT()) {
                RuntimeGraph runtimeGraph = new RuntimeGraph();
                runtimeGraph.setExcludePatterns(excludePatterns);
                runtimeGraph.setIncludePatterns(includePatterns);
                runtimeGraphs.put(serviceURL, runtimeGraph);
            }
            int timeout = 0;
            while (isRunning.get()) {
                TimeUtils.sleepMillis(timeout);
                timeout = 0;
                Map<String, ThreadInfo[]> threadInfos = null;
                if (jmxThreadPoolExecutor.getActiveCount() == 0) {

                    final SettableFuture<Map<String, ThreadInfo[]>> future = SettableFuture.create();
                    jmxThreadPoolExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            future.set(threadInfoProvider.getThreadInfo());
                        }
                    });
                    try {
                        threadInfos = Futures.makeUninterruptible(future).get(jmxTimeout, TimeUnit.MILLISECONDS);
                    } catch (ExecutionException e) {
                        log.error("Execution failed {}", e);
                        throw Throwables.propagate(e);
                    } catch (TimeoutException e) {
                        log.warn("SamplingProfiler {} : Time is left for collecting through JMX, make pause {} ms and pass out", identifier, jmxTimeout);
                        timeout = jmxTimeout;
                        jmxThreadPoolExecutor.shutdown();
                        jmxThreadPoolExecutor = createJMXThreadPoolExecutor();
                        continue;
                    }
                } else {
                    log.debug("SamplingProfiler {} : jmxThread is busy. pass out", identifier);
                }

                if (threadInfos == null) {
                    log.warn("SamplingProfiler {} : Getting thread info through jxm failed.");
                } else {
                    for (Map.Entry<String, ThreadInfo[]> threadInfosEntry : threadInfos.entrySet()) {
                        if (threadInfosEntry.getValue() == null) {
                            log.debug("SamplingProfiler {} : ThreadInfo[] is null.", identifier);
                            continue;
                        }
                        RuntimeGraph runtimeGraph = runtimeGraphs.get(threadInfosEntry.getKey());
                        for (ThreadInfo info : threadInfosEntry.getValue()) {
                            if (info == null) {
                                log.debug("SamplingProfiler {} : ThreadInfo is null.", identifier);
                                continue;
                            }
                            if (info.getThreadState() == Thread.State.RUNNABLE) {
                                StackTraceElement[] stackTrace = info.getStackTrace();
                                List<Method> callTree = new ArrayList<Method>(stackTrace.length);
                                for (int i = stackTrace.length - 1; i >= 0; i--) {
                                    Method method = new Method(stackTrace[i].getClassName(), stackTrace[i].getMethodName());
                                    callTree.add(method);
                                }
                                runtimeGraph.registerSnapshot(callTree);
                            }
                        }
                    }
                }
                TimeUtils.sleepMillis(pollingInterval);
            }
        }

    }

    private static List<Pattern> toPatterns(List<String> regexps) {
        List<Pattern> patterns = Lists.newArrayList();
        for (String regexp : regexps) {
            patterns.add(Pattern.compile(regexp));
        }
        return patterns;
    }

    @Override
    public Map<String, RuntimeGraph> getRuntimeGraph() {
        return this.runtimeGraphs;
    }

    @Override
    public ThreadInfoProvider getThreadInfoProvider() {
        return this.threadInfoProvider;
    }

    public void setThreadInfoProvider(ThreadInfoProvider threadInfoProvider) {
        this.threadInfoProvider = threadInfoProvider;
    }

    @Override
    public long getPollingInterval() {
        return this.pollingInterval;
    }

    @Override
    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public void setIncludePatterns(List<String> regexps) {
        this.includePatterns = toPatterns(regexps);
    }

    public void setExcludePatterns(List<String> regexps) {
        this.excludePatterns = toPatterns(regexps);
    }
}
