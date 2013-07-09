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

package com.griddynamics.jagger.agent.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import com.griddynamics.jagger.agent.model.*;
import com.griddynamics.jagger.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

import static com.griddynamics.jagger.util.Units.bytesToKiB;
import static com.griddynamics.jagger.util.Units.bytesToMiB;

/**
 * User: vshulga
 * Date: 7/5/11
 * Time: 5:35 PM
 * <p/>
 * Service aggregates logic for general system monitoring and specific JVM monitoring.
 */
public class MonitoringInfoServiceImpl implements MonitoringInfoService {
    private static final Logger log = LoggerFactory.getLogger(MonitoringInfoServiceImpl.class);

    private int jmxTimeout = 300;

    private SystemInfoCollector systemInfoService;
    private SystemUnderTestService systemUnderTestService;
    private ThreadPoolExecutor jmxThreadPoolExecutor =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    public void setSystemInfoService(SystemInfoCollector systemInfoService) {
        this.systemInfoService = systemInfoService;
    }

    public void setSystemUnderTestService(SystemUnderTestService systemUnderTestService) {
        this.systemUnderTestService = systemUnderTestService;
    }

    public void setJmxTimeout(int jmxTimeout) {
        this.jmxTimeout = jmxTimeout;
    }

    @Override
    public SystemInfo getSystemInfo() {
        long startTime = System.currentTimeMillis(), startTimeLog = startTime;
        SystemInfo systemInfo = new SystemInfo();
        log.debug("start collecting box info through sigar on agent");
        Map<MonitoringParameter, Double> sysInfoStringMap = Maps.newHashMap();
        Map<String, String> memInfo = this.systemInfoService.getMemInfo();
        DisksData disksData = systemInfoService.getDisksData();
        CpuData cpuData = systemInfoService.getCpuData();
        TcpData tcpData = systemInfoService.getTcpData();

        sysInfoStringMap.put(DefaultMonitoringParameters.MEM_RAM, Double.parseDouble(memInfo.get("Ram")));
        sysInfoStringMap.put(DefaultMonitoringParameters.MEM_TOTAL, bytesToMiB(memInfo.get("Total")));
        sysInfoStringMap.put(DefaultMonitoringParameters.MEM_USED, bytesToMiB(memInfo.get("Used")));
        sysInfoStringMap.put(DefaultMonitoringParameters.MEM_ACTUAL_USED, bytesToMiB(memInfo.get("ActualUsed")));
        sysInfoStringMap.put(DefaultMonitoringParameters.MEM_ACTUAL_FREE, bytesToMiB(memInfo.get("ActualFree")));
        sysInfoStringMap.put(DefaultMonitoringParameters.MEM_FREE, bytesToMiB(memInfo.get("Free")));
        sysInfoStringMap.put(DefaultMonitoringParameters.MEM_FREE_PERCENT, Double.valueOf(memInfo.get("FreePercent")));

        sysInfoStringMap.put(DefaultMonitoringParameters.TCP_ESTABLISHED, tcpData.getTcpEstablished());
        sysInfoStringMap.put(DefaultMonitoringParameters.TCP_LISTEN, tcpData.getTcpListen());
        sysInfoStringMap.put(DefaultMonitoringParameters.TCP_SYNCHRONIZED_RECEIVED, tcpData.getTcpSynchronizedReceived());
        sysInfoStringMap.put(DefaultMonitoringParameters.TCP_INBOUND_TOTAL, bytesToKiB(tcpData.getTcpInboundTotal()));
        sysInfoStringMap.put(DefaultMonitoringParameters.TCP_OUTBOUND_TOTAL, bytesToKiB(tcpData.getTcpOutboundTotal()));

        sysInfoStringMap.put(DefaultMonitoringParameters.DISKS_READ_BYTES_TOTAL, bytesToKiB(disksData.getDisksReadBytesTotal()));
        sysInfoStringMap.put(DefaultMonitoringParameters.DISKS_WRITE_BYTES_TOTAL, bytesToKiB(disksData.getDisksWriteBytesTotal()));
        sysInfoStringMap.put(DefaultMonitoringParameters.DISKS_AVERAGE_QUEUE_SIZE_TOTAL, disksData.getDisksQueueTotal());
        sysInfoStringMap.put(DefaultMonitoringParameters.DISKS_SERVICE_TIME_TOTAL, disksData.getDisksSvcTimeTotal());

        sysInfoStringMap.put(DefaultMonitoringParameters.CPU_STATE_USER_PERC, cpuData.getCpuStateUser() * 100);
        sysInfoStringMap.put(DefaultMonitoringParameters.CPU_STATE_SYSTEM_PERC, cpuData.getCpuStateSys() * 100);
        sysInfoStringMap.put(DefaultMonitoringParameters.CPU_STATE_IDLE_PERC, cpuData.getCpuStateIdle() * 100);
        sysInfoStringMap.put(DefaultMonitoringParameters.CPU_STATE_IDLE_WAIT, cpuData.getCpuStateWait() * 100);
        sysInfoStringMap.put(DefaultMonitoringParameters.CPU_STATE_COMBINED, cpuData.getCpuStateCombined() * 100);

        log.debug("finish collecting box info through sigar on agent: time {} ms", System.currentTimeMillis() - startTimeLog);
        startTimeLog = System.currentTimeMillis();
        log.debug("start collecting LoadAverage info on agent");
        double[] loadAverage = systemInfoService.getLoadAverage();
        log.debug("finish collecting LoadAverage info on agent: time {} ms", System.currentTimeMillis() - startTimeLog);
        sysInfoStringMap.put(DefaultMonitoringParameters.CPU_LOAD_AVERAGE_1, loadAverage[0]);
        sysInfoStringMap.put(DefaultMonitoringParameters.CPU_LOAD_AVERAGE_5, loadAverage[1]);
        sysInfoStringMap.put(DefaultMonitoringParameters.CPU_LOAD_AVERAGE_15, loadAverage[2]);

        systemInfo.setTime(startTime);
        systemInfo.setSysInfo(sysInfoStringMap);
        startTimeLog = System.currentTimeMillis();
        log.debug("start collecting SuT info through jmx on agent");
        if (jmxThreadPoolExecutor.getActiveCount() == 0) {
            final SettableFuture<Map<String, SystemUnderTestInfo>> future = SettableFuture.create();
            jmxThreadPoolExecutor.submit(new Thread() {
                @Override
                public void run() {
                    future.set(systemUnderTestService.getInfo());
                }
            });
            Map<String, SystemUnderTestInfo> jmxInfo;
            try {
                jmxInfo = Futures.makeUninterruptible(future).get(jmxTimeout, TimeUnit.MILLISECONDS);
                systemInfo.setSysUnderTest(jmxInfo);
            } catch (ExecutionException e) {
                log.error("Execution failed {}", e);
                throw Throwables.propagate(e);
            } catch (TimeoutException e) {
                log.warn("Time is left for collecting through JMX, make pause {} ms and pass out without jmxInfo", jmxTimeout);
                TimeUtils.sleepMillis(jmxTimeout);
            }
        } else {
            log.warn("jmxThread is busy. pass out");
        }

        log.debug("finish collecting SuT info through jmx on agent: time {} ms", System.currentTimeMillis() - startTimeLog);
        return systemInfo;
    }

}
