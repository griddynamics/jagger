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

package com.griddynamics.jagger.agent.model;

import java.util.List;
import java.util.Map;

/**
 * User: vshulga
 * Date: 7/5/11
 * Time: 12:21 PM
 */
public interface SystemInfoCollector {

    List<String> getCPUInfo();

    Map<String, String> getCPULoadInfo();

    Map<String, String> getMemInfo();

    Map<String, String> getNetworkInfo();

    int getTcpBound();

    int getTcpListen();

    int getTcpEstablished();

    int getTcpIdle();

    int getTcpSynchronizedReceived();

    double getCPUStateSys();

    double getCPUStateUser();

    double getCPUStateWait();

    double getCPUStateIdle();

    long getTCPInboundTotal();

    long getTCPOutboundTotal();

    long getDisksReadBytesTotal();

    long getDisksWriteBytesTotal();

    double[] getLoadAverage();
}
