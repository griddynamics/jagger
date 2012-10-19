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

package com.griddynamics.jagger.kernel.agent.impl;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.SettableFuture;
import com.google.common.util.concurrent.Uninterruptibles;
import com.griddynamics.jagger.agent.model.SystemInfo;
import com.griddynamics.jagger.agent.model.SystemUnderTestInfo;
import com.griddynamics.jagger.kernel.agent.model.MonitoringInfoService;
import com.griddynamics.jagger.kernel.agent.model.SystemUnderTestService;
import com.griddynamics.jagger.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Service aggregates logic for general system monitoring and specific JVM monitoring.
 *
 * @author vshulga
 */
public class MonitoringInfoServiceImpl implements MonitoringInfoService, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(MonitoringInfoServiceImpl.class);
    private static final int JMX_TIMEOUT = 300;

    private List<SystemUnderTestService> systemUnderTestServices = new ArrayList<SystemUnderTestService>();

    private ThreadPoolExecutor jmxThreadPoolExecutor;

    public void setSystemUnderTestServices(List<SystemUnderTestService> systemUnderTestServices) {
        this.systemUnderTestServices = systemUnderTestServices;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jmxThreadPoolExecutor =
                    new ThreadPoolExecutor(systemUnderTestServices.size(), systemUnderTestServices.size(),
                            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public SystemInfo getSystemInfo() {
        long startTime = System.currentTimeMillis();
        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setTime(startTime);

        log.debug("start collecting SuT info through jmx on kernel agent");
        if (jmxThreadPoolExecutor.getActiveCount() == 0) {
            Collection<SystemUnderTestRunnable> systemUnderTestRunnables = new ArrayList<SystemUnderTestRunnable>(systemUnderTestServices.size());
            for (SystemUnderTestService systemUnderTestService : systemUnderTestServices) {
                if (systemUnderTestService.isEnabled()) {
                    SystemUnderTestRunnable systemUnderTestRunnable = new SystemUnderTestRunnable(systemUnderTestService);
                    jmxThreadPoolExecutor.submit(systemUnderTestRunnable);
                    systemUnderTestRunnables.add(systemUnderTestRunnable);
                }
            }
            Map<String, SystemUnderTestInfo> jmxInfo = new HashMap<String, SystemUnderTestInfo>();
            try {
                for (SystemUnderTestRunnable systemUnderTestRunnable : systemUnderTestRunnables) {
                    jmxInfo.putAll(Uninterruptibles.getUninterruptibly(systemUnderTestRunnable.getFuture(), JMX_TIMEOUT, TimeUnit.MILLISECONDS));
                }
                systemInfo.setSysUnderTest(jmxInfo);
            } catch (ExecutionException e) {
                log.error("Execution failed {}", e);
                throw Throwables.propagate(e);
            } catch (TimeoutException e) {
                log.warn("Time is left for collecting through JMX, make pause {} ms and pass out without jmxInfo", JMX_TIMEOUT);
                TimeUtils.sleepMillis(JMX_TIMEOUT);
            }
        } else {
            log.warn("jmxThread is busy. pass out");
        }

        log.debug("finish collecting SuT info through jmx on agent: time {} ms", System.currentTimeMillis() - startTime);
        return systemInfo;
    }

    private static class SystemUnderTestRunnable implements Runnable {

        private SystemUnderTestService systemUnderTestService;
        private SettableFuture<Map<String, SystemUnderTestInfo>> future;

        private SystemUnderTestRunnable(SystemUnderTestService systemUnderTestService) {
            this.systemUnderTestService = systemUnderTestService;
            future = SettableFuture.create();
        }

        @Override
        public void run() {
            future.set(systemUnderTestService.getInfo());
        }

        public SettableFuture<Map<String, SystemUnderTestInfo>> getFuture() {
            return future;
        }
    }

}
