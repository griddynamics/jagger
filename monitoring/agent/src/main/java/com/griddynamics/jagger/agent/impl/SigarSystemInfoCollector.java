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

package com.griddynamics.jagger.agent.impl;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.agent.model.CpuData;
import com.griddynamics.jagger.agent.model.DisksData;
import com.griddynamics.jagger.agent.model.SystemInfoCollector;
import com.griddynamics.jagger.agent.model.TcpData;
import org.apache.commons.lang.StringUtils;
import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User: vshulga
 * Date: 7/5/11
 * Time: 12:41 PM
 * <p/>
 * Implementation of general info gathering with hyperic Sigar.
 * Collector could be used for getting cpu info, cpu load, memory info, network info, tcp info.
 */
public class SigarSystemInfoCollector implements SystemInfoCollector {

    private final static Logger logger = LoggerFactory.getLogger(SigarSystemInfoCollector.class);

    private Set<String> interfaceNames;

    private static String cpuTemplate = "CPU model %s with %d mhz frequency; with %d cores";
    private Sigar sigar;

    public SigarSystemInfoCollector() {
    }

    public void setInterfaceNames(String commaSeparatedInterfaceNames) {
        this.interfaceNames = new HashSet<String>(Arrays.asList(StringUtils.split(commaSeparatedInterfaceNames, ", ")));
    }

    public void setSigar(Sigar sigar) {
        this.sigar = sigar;
    }

    public List<String> getCPUInfo() {
        ArrayList<String> result = new ArrayList<String>();
        try {
            CpuInfo[] cpuInfoList = sigar.getCpuInfoList();
            for (CpuInfo cpuInfo : cpuInfoList) {
                String cpu = String.format(cpuTemplate, cpuInfo.getModel(), cpuInfo.getMhz(), cpuInfo.getTotalCores());
                result.add(cpu);
            }
        } catch (Exception e) {
            logger.warn("exception during getCPUInfo", e);
        }
        logger.trace("getCPUInfo: {}", result);
        return result;
    }

    public Map<String, String> getCPULoadInfo() {
        Map<String, String> result = Maps.newHashMap();
        try {
            result = sigar.getCpu().toMap();
        } catch (Exception e) {
            logger.warn("exception during getCPULoadInfo", e);
        }
        logger.trace("getCPULoadInfo: {}", result);
        return result;
    }


    public Map<String, String> getMemInfo() {
        Map<String, String> result = Maps.newHashMap();
        try {
            result = sigar.getMem().toMap();
        } catch (Exception e) {
            logger.warn("exception during getMemInfo", e);
        }
        logger.trace("getMemInfo: {}", result);
        return result;
    }

    public Map<String, String> getNetworkInfo() {
        Map<String, String> result = Maps.newHashMap();
        try {
            result = toMap(sigar.getNetStat());
        } catch (Exception e) {
            logger.warn("exception during getNetworkInfo", e);
        }
        logger.trace("getNetworkInfo: {}", result);
        return result;
    }

    private static Map<String, String> toMap(NetStat netStat) {
        Map<String, String> result = Maps.newHashMap();
        result.put("allInboundTotal", "" + netStat.getAllInboundTotal());
        result.put("allOutboundTotal", "" + netStat.getAllOutboundTotal());
        result.put("tcpBound", "" + netStat.getTcpBound());
        result.put("tcpClose", "" + netStat.getTcpClose());
        result.put("tcpCloseWait", "" + netStat.getTcpCloseWait());
        result.put("tcpClosing", "" + netStat.getTcpClosing());
        result.put("tcpEstablished", "" + netStat.getTcpEstablished());
        result.put("tcpFinWait1", "" + netStat.getTcpFinWait1());
        result.put("tcpFinWait2", "" + netStat.getTcpFinWait2());
        result.put("tcpIdle", "" + netStat.getTcpIdle());
        result.put("tcpInboundTotal", "" + netStat.getTcpInboundTotal());
        result.put("tcpLastAck", "" + netStat.getTcpLastAck());
        result.put("tcpListen", "" + netStat.getTcpListen());
        result.put("tcpOutboundTotal", "" + netStat.getTcpOutboundTotal());
        result.put("tcpStates", "" + netStat.getTcpStates());
        result.put("tcpSynRecv", "" + netStat.getTcpSynRecv());
        result.put("tcpSynSent", "" + netStat.getTcpSynSent());
        result.put("tcpTimeWait", "" + netStat.getTcpTimeWait());
        return result;
    }

   @Override
   public double[] getLoadAverage() {
       try {
           return sigar.getLoadAverage();
       } catch (SigarException e) {
           logger.warn("Exception during load average polling", e);
       }
       return new double[] {0, 0, 0};
   }

    @Override
    public TcpData getTcpData() {
        TcpData data = new TcpData();
        try {
            NetStat stat = sigar.getNetStat();
            data.setTcpBound(stat.getTcpBound());
            data.setTcpEstablished(stat.getTcpEstablished());
            data.setTcpIdle(stat.getTcpIdle());
            data.setTcpListen(stat.getTcpListen());
            data.setTcpSynchronizedReceived(stat.getTcpSynRecv());

            long inboundBytes = 0;
            long outboundBytes = 0;
            for(String netInterface : sigar.getNetInterfaceList()) {
                for(String mask : interfaceNames) {
                    if(netInterface.matches(mask)) {
                        inboundBytes += sigar.getNetInterfaceStat(netInterface).getRxBytes();
                        outboundBytes += sigar.getNetInterfaceStat(netInterface).getTxBytes();
                    }
                }
            }
            data.setTcpInboundTotal(inboundBytes);
            data.setTcpOutboundTotal(outboundBytes);

            logger.debug("getTcpData: {}", data);
        } catch (SigarException e) {
            logger.warn("Exception during getTcpData", e);
        }
        return data;
    }

    @Override
    public CpuData getCpuData() {
        CpuData data = new CpuData();
        try {
            CpuPerc cpuPerc = sigar.getCpuPerc();

            double value = cpuPerc.getIdle();
            data.setCpuStateIdle(Double.isNaN(value) ? 0 : value);

            value = cpuPerc.getSys();
            data.setCpuStateSys(Double.isNaN(value) ? 0 : value);

            value = cpuPerc.getUser();
            data.setCpuStateUser(Double.isNaN(value) ? 0 : value);

            value = cpuPerc.getWait();
            data.setCpuStateWait(Double.isNaN(value) ? 0 : value);


            value = cpuPerc.getCombined();
            data.setCpuStateCombined(Double.isNaN(value) ? 0 : value);

            logger.debug("getCpuData: {}", data);
        } catch (SigarException e) {
            logger.warn("Exception during getCpuData", e);
        }
        return data;
    }

    @Override
    public DisksData getDisksData() {
        DisksData data = new DisksData();
        try {
            long disksReadBytesTotal = 0;
            long disksQueueTotal = 0;
            long disksSvcTimeTotal = 0;
            long disksWriteBytesTotal = 0;

            FileSystem[] devices = sigar.getFileSystemList();
            for (FileSystem dev : devices) {
                if(FileSystem.TYPE_LOCAL_DISK == dev.getType()) {
                    DiskUsage disk = sigar.getDiskUsage(dev.getDirName());
                    disksReadBytesTotal += disk.getReadBytes();
                    disksWriteBytesTotal += disk.getWriteBytes();

                    double value = disk.getQueue();
                    disksQueueTotal += Double.isNaN(value) ? 0 : value;

                    value = disk.getServiceTime();
                    disksSvcTimeTotal += Double.isNaN(value) ? 0 : value;
                }
            }
            data.setDisksQueueTotal(disksQueueTotal);
            data.setDisksReadBytesTotal(disksReadBytesTotal);
            data.setDisksSvcTimeTotal(disksSvcTimeTotal);
            data.setDisksWriteBytesTotal(disksWriteBytesTotal);

            logger.debug("getDisksData: {}", data);
        } catch (SigarException e) {
            logger.warn("Exception during getDisksData", e);
        }
        return data;
    }
}
