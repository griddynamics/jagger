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

package com.griddynamics.jagger.dbapi.parameter;


import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

public enum DefaultMonitoringParameters implements MonitoringParameter {
    MEM_RAM(StandardMetricsNamesUtil.MON_MEM_RAM, "RAM, MiB", false, MonitoringParameterLevel.BOX),
    MEM_TOTAL(StandardMetricsNamesUtil.MON_MEM_TOTAL, "Total memory, MiB", false, MonitoringParameterLevel.BOX),
    MEM_USED(StandardMetricsNamesUtil.MON_MEM_USED, "Memory used, MiB", false, MonitoringParameterLevel.BOX),
    MEM_ACTUAL_USED(StandardMetricsNamesUtil.MON_MEM_ACTUAL_USED, "Memory actual used, MiB", false, MonitoringParameterLevel.BOX),
    MEM_FREE_PERCENT(StandardMetricsNamesUtil.MON_MEM_FREE_PRCNT, "Memory free, %", false, MonitoringParameterLevel.BOX),
    MEM_ACTUAL_FREE(StandardMetricsNamesUtil.MON_MEM_ACTUAL_FREE, "Memory actual free, MiB", false, MonitoringParameterLevel.BOX),
    MEM_FREE(StandardMetricsNamesUtil.MON_MEM_FREE, "Memory free, MiB", false, MonitoringParameterLevel.BOX),

    TCP_ESTABLISHED(StandardMetricsNamesUtil.MON_TCP_EST, "Tcp established connections, count", false, MonitoringParameterLevel.BOX),
    TCP_LISTEN(StandardMetricsNamesUtil.MON_TCP_LISTEN, "TCP listen, count", false, MonitoringParameterLevel.BOX),
    TCP_SYNCHRONIZED_RECEIVED(StandardMetricsNamesUtil.MON_SYNC_RECEIVED, "TCP synchronized received, count", false, MonitoringParameterLevel.BOX),
    TCP_INBOUND_TOTAL(StandardMetricsNamesUtil.MON_INBOUND_TOTAL, "TCP inbound total, KiB", true, MonitoringParameterLevel.BOX),
    TCP_OUTBOUND_TOTAL(StandardMetricsNamesUtil.MON_OUTBOUND_TOTAL, "TCP outbound total, KiB", true, MonitoringParameterLevel.BOX),

    DISKS_READ_BYTES_TOTAL(StandardMetricsNamesUtil.MON_DISK_READ_BYTES, "Disks read bytes total, KiB", true, MonitoringParameterLevel.BOX),
    DISKS_WRITE_BYTES_TOTAL(StandardMetricsNamesUtil.MON_DISK_WRITE_BYTES, "Disks write bytes total, KiB", true, MonitoringParameterLevel.BOX),

    DISKS_SERVICE_TIME_TOTAL(StandardMetricsNamesUtil.MON_DISK_SERVICE_TIME, "Disks service time", false, MonitoringParameterLevel.BOX),
    DISKS_AVERAGE_QUEUE_SIZE_TOTAL(StandardMetricsNamesUtil.MON_DISK_QUEUE_SIZE_TOTAL, "Disks queue", false, MonitoringParameterLevel.BOX),

    CPU_STATE_USER_PERC(StandardMetricsNamesUtil.MON_CPU_USER, "CPU user, %", false, MonitoringParameterLevel.BOX),
    CPU_STATE_SYSTEM_PERC(StandardMetricsNamesUtil.MON_CPU_SYS_PRCNT, "CPU system, %", false, MonitoringParameterLevel.BOX),
    CPU_STATE_IDLE_PERC(StandardMetricsNamesUtil.MON_CPU_IDLE_PRCNT, "CPU idle, %", false, MonitoringParameterLevel.BOX),
    CPU_STATE_IDLE_WAIT(StandardMetricsNamesUtil.MON_CPU_WAIT, "CPU wait, %", false, MonitoringParameterLevel.BOX),
    CPU_STATE_COMBINED(StandardMetricsNamesUtil.MON_CPU_COMBINED, "CPU combined, %", false, MonitoringParameterLevel.BOX),

    CPU_LOAD_AVERAGE_1(StandardMetricsNamesUtil.MON_CPULA_1, "CPU load average for the past 1 minute, %", false, MonitoringParameterLevel.BOX),
    CPU_LOAD_AVERAGE_5(StandardMetricsNamesUtil.MON_CPULA_5, "CPU load average for the past 5 minutes, %", false, MonitoringParameterLevel.BOX),
    CPU_LOAD_AVERAGE_15(StandardMetricsNamesUtil.MON_CPULA_15, "CPU load average for the past 15 minutes, %", false, MonitoringParameterLevel.BOX),

    JMX_GC_MINOR_TIME(StandardMetricsNamesUtil.MON_GC_MINOR_TIME, "All GC minor time", true, MonitoringParameterLevel.SUT),
    JMX_GC_MINOR_UNIT(StandardMetricsNamesUtil.MON_GC_MINOR_UNIT, "All GC minor unit", true, MonitoringParameterLevel.SUT),
    JMX_GC_MAJOR_TIME(StandardMetricsNamesUtil.MON_GC_MAJOR_TIME, "All GC major time", true, MonitoringParameterLevel.SUT),
    JMX_GC_MAJOR_UNIT(StandardMetricsNamesUtil.MON_GC_MAJOR_UNIT, "All GC major unit", true, MonitoringParameterLevel.SUT),

    HEAP_MEMORY_INIT(StandardMetricsNamesUtil.MON_HEAP_INIT, "Heap init memory", false, MonitoringParameterLevel.SUT),
    HEAP_MEMORY_USED(StandardMetricsNamesUtil.MON_HEAP_USED, "Heap used memory", false, MonitoringParameterLevel.SUT),
    HEAP_MEMORY_COMMITTED(StandardMetricsNamesUtil.MON_HEAP_COMMITTED, "Heap committed memory, MiB", false, MonitoringParameterLevel.SUT),
    HEAP_MEMORY_MAX(StandardMetricsNamesUtil.MON_HEAP_MAX, "Heap max memory, MiB", false, MonitoringParameterLevel.SUT),

    NON_HEAP_MEMORY_INIT(StandardMetricsNamesUtil.MON_NONHEAP_INIT, "Non heap init memory, MiB", false, MonitoringParameterLevel.SUT),
    NON_HEAP_MEMORY_USED(StandardMetricsNamesUtil.MON_NONHEAP_USED, "Non heap used memory, MiB", false, MonitoringParameterLevel.SUT),
    NON_HEAP_MEMORY_COMMITTED(StandardMetricsNamesUtil.MON_NONHEAP_COMMITTED, "Non heap committed memory, MiB", false, MonitoringParameterLevel.SUT),
    NON_HEAP_MEMORY_MAX(StandardMetricsNamesUtil.MON_NONHEAP_MAX, "Non heap max memory, MiB", false, MonitoringParameterLevel.SUT),

    OPEN_FILE_DESCRIPTOR_COUNT(StandardMetricsNamesUtil.MON_FILE_DESCRIPTORS, "Count of open file descriptors", false, MonitoringParameterLevel.SUT),

    THREAD_COUNT(StandardMetricsNamesUtil.MON_THREAD_COUNT, "Live threads", false, MonitoringParameterLevel.SUT),
    THREAD_PEAK_COUNT(StandardMetricsNamesUtil.MON_THREAD_PEAK_COUNT, "Peak thread count", false, MonitoringParameterLevel.SUT);

    private String id = "";
    private String description;
    private boolean isCumulativeCounter;
    private MonitoringParameterLevel level;

    DefaultMonitoringParameters(String id, String description, boolean isCumulativeCounter, MonitoringParameterLevel level) {
        this.id = id;
        this.description = description;
        this.isCumulativeCounter = isCumulativeCounter;
        this.level = level;
    }

    /**
     * @return true if monitoring parameter is a continuously growing counter (like total transferred bytes)
     * Such parameter should be differentiated in plots. False otherwise.
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
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
