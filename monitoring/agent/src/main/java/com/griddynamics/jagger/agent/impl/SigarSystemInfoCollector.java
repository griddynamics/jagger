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

import com.google.common.collect.Maps;
import com.griddynamics.jagger.agent.model.SystemInfoCollector;
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

    public int getTcpBound() {
        int result = -1;
        try {
            result = sigar.getNetStat().getTcpBound();
        } catch (Exception e) {
            logger.warn("Exception during getTcpBound", e);
        }
        logger.trace("getTcpBound: {}", result);
        return result;
    }

    public int getTcpListen() {
        int result = -1;
        try {
            result = sigar.getNetStat().getTcpListen();

        } catch (Exception e) {
            logger.warn("Exception during getTcpListen", e);
        }
        logger.trace("getTcpListen: {}", result);
        return result;
    }

    public int getTcpEstablished() {
        int result = -1;
        try {
            result = sigar.getNetStat().getTcpEstablished();

        } catch (Exception e) {
            logger.warn("Exception during getTcpEstablished", e);
        }
        logger.trace("getTcpEstablished: {}", result);
        return result;
    }

    public int getTcpIdle() {
        int result = -1;
        try {
            result = sigar.getNetStat().getTcpIdle();
        } catch (Exception e) {
            logger.warn("Exception during getTcpIdle", e);
        }
        logger.trace("getTcpIdle: {}", result);
        return result;
    }

    public int getTcpSynchronizedReceived() {
        int result = -1;
        try {
            result = sigar.getNetStat().getTcpSynRecv();
        } catch (Exception e) {
            logger.warn("Exception during getTcpSynchronizedReceived", e);
        }
        logger.trace("getTcpSynchronizedReceived: {}", result);
        return result;
    }

    @Override
    public double getCPUStateSys() {
        double result = 0;
        try {
            CpuPerc[] cpuPercList = sigar.getCpuPercList();
            for (CpuPerc cpuPerc : cpuPercList) {
                double value = cpuPerc.getSys();
                result += Double.isNaN(value) ? 0 : value;
            }
            result /= cpuPercList.length;
        } catch (Exception e) {
            logger.warn("Exception during getCPUStateSys", e);
        }
        logger.trace("getCPUStateSys: {}", result);
        return result;
    }

    @Override
    public double getCPUStateUser() {
        double result = 0;
        try {
            CpuPerc[] cpuPercList = sigar.getCpuPercList();
            for (CpuPerc cpuPerc : cpuPercList) {
                double value = cpuPerc.getUser();
                result += Double.isNaN(value) ? 0 : value;
            }
            result /= cpuPercList.length;
        } catch (Exception e) {
            logger.warn("Exception during getCPUStateSys", e);
        }
        logger.trace("getCPUStateUser: {}", result);
        return result;
    }

    @Override
    public double getCPUStateWait() {
        double result = 0;
        try {
            CpuPerc[] cpuPercList = sigar.getCpuPercList();
            for (CpuPerc cpuPerc : cpuPercList) {
                double value = cpuPerc.getWait();
                result += Double.isNaN(value) ? 0 : value;
            }
            result /= cpuPercList.length;
        } catch (Exception e) {
            logger.warn("Exception during getCPUStateWait", e);
        }
        logger.trace("getCPUStateWait: {}", result);
        return result;
    }

    @Override
    public double getCPUStateIdle() {
        double result = 0;
        try {
            CpuPerc[] cpuPercList = sigar.getCpuPercList();
            for (CpuPerc cpuPerc : cpuPercList) {
                double value = cpuPerc.getIdle();
                result += Double.isNaN(value) ? 0 : value;
            }
            result /= cpuPercList.length;
        } catch (Exception e) {
            logger.warn("Exception during getCPUStateIdle", e);
        }
        logger.trace("getCPUStateIdle: {}", result);
        return result;
    }

    @Override
    public long getTCPInboundTotal() {
        try {
            long inboundBytes = 0;
            for(String netInterface : sigar.getNetInterfaceList()) {
                for(String mask : interfaceNames) {
                    if(netInterface.matches(mask)) {
                        inboundBytes += sigar.getNetInterfaceStat(netInterface).getRxBytes();
                    }
                }
            }
            logger.trace("getTCPInboundTotal: {}", inboundBytes);
            return inboundBytes;
        } catch (SigarException e) {
            logger.warn("Exception during network polling", e);
        }
        return -1;
    }

    @Override
    public long getTCPOutboundTotal() {
        try {
            long outboundBytes = 0;
            for(String netInterface : sigar.getNetInterfaceList()) {
                for(String mask : interfaceNames) {
                    if(netInterface.matches(mask)) {
                        outboundBytes += sigar.getNetInterfaceStat(netInterface).getTxBytes();
                    }
                }
            }
            logger.trace("getTCPOutboundTotal: {}", outboundBytes);
            return outboundBytes;
        } catch (SigarException e) {
            logger.warn("Exception during network polling", e);
        }
        return -1;
    }

    @Override
    public long getDisksReadBytesTotal() {
        try {
            long readBytes = 0;
            FileSystem[] devices = sigar.getFileSystemList();
            for (FileSystem dev : devices) {
                if(FileSystem.TYPE_LOCAL_DISK == dev.getType()) {
                    DiskUsage disk = sigar.getDiskUsage(dev.getDirName());
                    readBytes += disk.getReadBytes();
                }
            }
            logger.trace("getDisksReadBytesTotal : {}", readBytes);
            return readBytes;
        } catch (SigarException e) {
            logger.warn("Exception during getting disks information", e);
        }
        return -1;
    }

    @Override
    public long getDisksWriteBytesTotal() {
        try {
            long writeBytes = 0;
            FileSystem[] devices = sigar.getFileSystemList();
            for (FileSystem dev : devices) {
                if(FileSystem.TYPE_LOCAL_DISK == dev.getType()) {
                    DiskUsage disk = sigar.getDiskUsage(dev.getDirName());
                    writeBytes += disk.getWriteBytes();
                }
            }
            logger.trace("getDisksWriteBytesTotal: {}", writeBytes);
            return writeBytes;
        } catch (SigarException e) {
            logger.warn("Exception during getting disks information", e);
        }
        return -1;
    }

    @Override
    public double[] getLoadAverage() {
        try {
            return sigar.getLoadAverage();
        } catch (SigarNotImplementedException e) {
            return new double[] {0, 0, 0};
        } catch (SigarException e) {
            logger.warn("Exception during load average polling", e);
            return new double[] {0, 0, 0};
        }
    }
}
