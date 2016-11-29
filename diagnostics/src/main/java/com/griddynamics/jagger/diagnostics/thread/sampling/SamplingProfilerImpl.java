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

package com.griddynamics.jagger.diagnostics.thread.sampling;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.griddynamics.jagger.util.TimeUtils;
import com.griddynamics.jagger.util.Timeout;
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
    private Timeout jmxTimeout = new Timeout(300,"");

    private ThreadInfoProvider threadInfoProvider;
    private long pollingInterval;
    private List<Pattern> includePatterns;
    private List<Pattern> excludePatterns;
    private ThreadPoolExecutor jmxThreadPoolExecutor = createJMXThreadPoolExecutor();

    public void setJmxTimeout(Timeout jmxTimeout) {
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

            boolean needReset = true;

            long timeout = 0;
            while (isRunning.get()) {
                TimeUtils.sleepMillis(timeout);
                timeout = jmxTimeout.getValue();
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
                        threadInfos = Futures.makeUninterruptible(future).get(jmxTimeout.getValue(), TimeUnit.MILLISECONDS);
                    } catch (ExecutionException e) {
                        log.error("Execution failed {}", e);
                        throw Throwables.propagate(e);
                    } catch (TimeoutException e) {
                        log.warn("SamplingProfiler {} : timeout. Collection of jmxInfo was not finished in {}. Pass out without jmxInfo",
                                identifier,jmxTimeout.toString());
                        continue;
                    }
                } else {
                    log.warn("SamplingProfiler {} : jmxThread is busy. Pass out without jmxInfo", identifier);
                    continue;
                }

                if (threadInfos == null) {
                    log.warn("SamplingProfiler {} : Getting thread info through jxm failed.");
                } else {

                    for (Map.Entry<String, ThreadInfo[]> threadInfosEntry : threadInfos.entrySet()) {

                        RuntimeGraph runtimeGraph = runtimeGraphs.get(threadInfosEntry.getKey());

                        if (needReset || runtimeGraph == null) {
                            runtimeGraph = new RuntimeGraph();
                            runtimeGraph.setExcludePatterns(excludePatterns);
                            runtimeGraph.setIncludePatterns(includePatterns);
                            runtimeGraphs.put(threadInfosEntry.getKey(), runtimeGraph);
                        }

                        if (threadInfosEntry.getValue() == null) {
                            log.debug("SamplingProfiler {} : ThreadInfo[] is null.", identifier);
                            continue;
                        }
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
                needReset = false;
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
