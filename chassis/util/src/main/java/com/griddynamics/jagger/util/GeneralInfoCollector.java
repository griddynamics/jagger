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

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Information collector about environment on particular node
 * @author Dmitry Latnikov
 * @n
 * @par Details:
 * @details
 */
public class GeneralInfoCollector {
    private static final Logger log = LoggerFactory.getLogger(GeneralInfoCollector.class);

    private Sigar sigar = new Sigar();

    public GeneralNodeInfo getGeneralNodeInfo() {
        long startTime = System.currentTimeMillis();
        GeneralNodeInfo generalInfo = new GeneralNodeInfo();
        log.debug("start collecting general information about system");

        // System
        generalInfo.setSystemTime(System.currentTimeMillis());
        generalInfo.setOsName(System.getProperty("os.name"));
        generalInfo.setOsVersion(System.getProperty("os.version"));
        generalInfo.setJaggerJavaVersion(System.getProperty("java.version"));

        // CPU
        try {
            CpuInfo[] cpuInfoList = sigar.getCpuInfoList();
            if (cpuInfoList.length != 0) {
                generalInfo.setCpuModel(cpuInfoList[0].getModel());
                generalInfo.setCpuMHz(cpuInfoList[0].getMhz());
                generalInfo.setCpuTotalCores(cpuInfoList[0].getTotalCores());
                generalInfo.setCpuTotalSockets(cpuInfoList[0].getTotalSockets());
            }
        }
        catch (UnsatisfiedLinkError e) {
            log.warn("exception during getCPUGeneralInfo. Check if '-Djava.library.path=./lib/native' is set for Sigar usage",e);
        }
        catch (Exception e) {
            log.warn("exception during getCPUGeneralInfo", e);
        }

        // Memory
        try {
            Mem systemMemory = sigar.getMem();
            generalInfo.setSystemRAM(systemMemory.getRam());
        }
        catch (UnsatisfiedLinkError e) {
            log.warn("exception during getCPUGeneralInfo. Check if '-Djava.library.path=./lib/native' is set for Sigar usage",e);
        }
        catch (Exception e) {
            log.warn("exception during getMem", e);
        }

        log.debug("finish collecting general information about system: time {} ms", System.currentTimeMillis() - startTime);
        return generalInfo;
    }

}
