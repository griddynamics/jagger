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

import static com.griddynamics.jagger.agent.model.MonitoringParameterLevel.BOX;
import static com.griddynamics.jagger.agent.model.MonitoringParameterLevel.SUT;

public enum DefaultMonitoringParameters implements MonitoringParameter {
    MEM_RAM("RAM, MiB", false, BOX),
    MEM_TOTAL("Total memory, MiB", false, BOX),
    MEM_USED("Memory used, MiB", false, BOX),
    MEM_ACTUAL_USED("Memory actual used, MiB", false, BOX),
    MEM_FREE_PERCENT("Memory free, %", false, BOX),
    MEM_ACTUAL_FREE("Memory actual free, MiB", false, BOX),
    MEM_FREE("Memory free, MiB", false, BOX),

    TCP_ESTABLISHED("Tcp established connections, count", false, BOX),
    TCP_LISTEN("TCP listen, count", false, BOX),
    TCP_SYNCHRONIZED_RECEIVED("TCP synchronized received, count", false, BOX),
    TCP_INBOUND_TOTAL("TCP inbound total, KiB/sec", true, BOX),
    TCP_OUTBOUND_TOTAL("TCP outbound total, KiB/sec", true, BOX),

    DISKS_READ_BYTES_TOTAL("Disks read bytes total, KiB/sec", true, BOX),
    DISKS_WRITE_BYTES_TOTAL("Disks write bytes total, KiB/sec", true, BOX),

    DISKS_SERVICE_TIME_TOTAL("Disks service time", false, BOX),
    DISKS_AVERAGE_QUEUE_SIZE_TOTAL("Disks queue", false, BOX),

    CPU_STATE_USER_PERC("CPU user, %", false, BOX),
    CPU_STATE_SYSTEM_PERC("CPU system, %", false, BOX),
    CPU_STATE_IDLE_PERC("CPU idle, %", false, BOX),
    CPU_STATE_IDLE_WAIT("CPU wait, %", false, BOX),
    CPU_STATE_COMBINED("CPU combined, %", false, BOX),

    CPU_LOAD_AVERAGE_1("CPU load average for the past 1 minute, %", false, BOX),
    CPU_LOAD_AVERAGE_5("CPU load average for the past 5 minutes, %", false, BOX),
    CPU_LOAD_AVERAGE_15("CPU load average for the past 15 minutes, %", false, BOX),

    JMX_GC_MINOR_TIME("All GC minor time", true, SUT),
    JMX_GC_MINOR_UNIT("All GC minor unit", true, SUT),
    JMX_GC_MAJOR_TIME("All GC major time", true, SUT),
    JMX_GC_MAJOR_UNIT("All GC major unit", true, SUT),

    HEAP_MEMORY_INIT("Heap init memory", false, SUT),
    HEAP_MEMORY_USED("Heap used memory", false, SUT),
    HEAP_MEMORY_COMMITTED("Heap committed memory, MiB", false, SUT),
    HEAP_MEMORY_MAX("Heap max memory, MiB", false, SUT),

    NON_HEAP_MEMORY_INIT("Non heap init memory, MiB", false, SUT),
    NON_HEAP_MEMORY_USED("Non heap used memory, MiB", false, SUT),
    NON_HEAP_MEMORY_COMMITTED("Non heap committed memory, MiB", false, SUT),
    NON_HEAP_MEMORY_MAX("Non heap max memory, MiB", false, SUT),

    OPEN_FILE_DESCRIPTOR_COUNT("Count of open file descriptors", false, SUT);

    private String description;
    private boolean isCumulativeCounter;
    private MonitoringParameterLevel level;

    DefaultMonitoringParameters(String description, boolean isCumulativeCounter, MonitoringParameterLevel level) {
        this.description = description;
        this.isCumulativeCounter = isCumulativeCounter;
        this.level = level;
    }

    /**
     * @return true if monitoring parameter is a continuously growing counter (like total transferred bytes)
     *         Such parameter should be differentiated in plots. False otherwise.
     */
    @Override
    public boolean isCumulativeCounter() {
        return isCumulativeCounter;
    }

    @Override
    public MonitoringParameterLevel getLevel() {
        return level;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
