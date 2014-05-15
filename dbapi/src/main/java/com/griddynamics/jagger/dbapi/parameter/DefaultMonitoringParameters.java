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
package com.griddynamics.jagger.dbapi.parameter;

public enum DefaultMonitoringParameters implements MonitoringParameter {
    MEM_RAM("mon_mem_ram","RAM, MiB", false, MonitoringParameterLevel.BOX),
    MEM_TOTAL("mon_mem_total","Total memory, MiB", false, MonitoringParameterLevel.BOX),
    MEM_USED("mon_mem_used","Memory used, MiB", false, MonitoringParameterLevel.BOX),
    MEM_ACTUAL_USED("mon_mem_actual_used","Memory actual used, MiB", false, MonitoringParameterLevel.BOX),
    MEM_FREE_PERCENT("mon_mem_free_prcnt","Memory free, %", false, MonitoringParameterLevel.BOX),
    MEM_ACTUAL_FREE("mon_mem_actual_free","Memory actual free, MiB", false, MonitoringParameterLevel.BOX),
    MEM_FREE("mon_mem_free","Memory free, MiB", false, MonitoringParameterLevel.BOX),

    TCP_ESTABLISHED("mon_tcp_est","Tcp established connections, count", false, MonitoringParameterLevel.BOX),
    TCP_LISTEN("mon_tcp_listen","TCP listen, count", false, MonitoringParameterLevel.BOX),
    TCP_SYNCHRONIZED_RECEIVED("mon_sync_received","TCP synchronized received, count", false, MonitoringParameterLevel.BOX),
    TCP_INBOUND_TOTAL("mon_inbound_total","TCP inbound total, KiB", true, MonitoringParameterLevel.BOX),
    TCP_OUTBOUND_TOTAL("mon_outbound_total","TCP outbound total, KiB", true, MonitoringParameterLevel.BOX),

    DISKS_READ_BYTES_TOTAL("mon_disk_read_bytes","Disks read bytes total, KiB", true, MonitoringParameterLevel.BOX),
    DISKS_WRITE_BYTES_TOTAL("mon_disk_write_bytes","Disks write bytes total, KiB", true, MonitoringParameterLevel.BOX),

    DISKS_SERVICE_TIME_TOTAL("mon_disk_service_time","Disks service time", false, MonitoringParameterLevel.BOX),
    DISKS_AVERAGE_QUEUE_SIZE_TOTAL("mon_disk_queue_size_total","Disks queue", false, MonitoringParameterLevel.BOX),

    CPU_STATE_USER_PERC("mon_cpu_user","CPU user, %", false, MonitoringParameterLevel.BOX),
    CPU_STATE_SYSTEM_PERC("mon_cpu_sys_prcnt","CPU system, %", false, MonitoringParameterLevel.BOX),
    CPU_STATE_IDLE_PERC("mon_cpu_idle_prcnt","CPU idle, %", false, MonitoringParameterLevel.BOX),
    CPU_STATE_IDLE_WAIT("mon_cpu_wait","CPU wait, %", false, MonitoringParameterLevel.BOX),
    CPU_STATE_COMBINED("mon_cpu_combined","CPU combined, %", false, MonitoringParameterLevel.BOX),

    CPU_LOAD_AVERAGE_1("mon_cpula_1","CPU load average for the past 1 minute, %", false, MonitoringParameterLevel.BOX),
    CPU_LOAD_AVERAGE_5("mon_cpula_5","CPU load average for the past 5 minutes, %", false, MonitoringParameterLevel.BOX),
    CPU_LOAD_AVERAGE_15("mon_cpula_15","CPU load average for the past 15 minutes, %", false, MonitoringParameterLevel.BOX),

    JMX_GC_MINOR_TIME("mon_gc_minor_time","All GC minor time", true, MonitoringParameterLevel.SUT),
    JMX_GC_MINOR_UNIT("mon_gc_minor_unit","All GC minor unit", true, MonitoringParameterLevel.SUT),
    JMX_GC_MAJOR_TIME("mon_gc_major_time","All GC major time", true, MonitoringParameterLevel.SUT),
    JMX_GC_MAJOR_UNIT("mon_gc_major_unit","All GC major unit", true, MonitoringParameterLevel.SUT),

    HEAP_MEMORY_INIT("mon_heap_init","Heap init memory", false, MonitoringParameterLevel.SUT),
    HEAP_MEMORY_USED("mon_heap_used","Heap used memory", false, MonitoringParameterLevel.SUT),
    HEAP_MEMORY_COMMITTED("mon_heap_committed","Heap committed memory, MiB", false, MonitoringParameterLevel.SUT),
    HEAP_MEMORY_MAX("mon_heap_max","Heap max memory, MiB", false, MonitoringParameterLevel.SUT),

    NON_HEAP_MEMORY_INIT("mon_nonheap_init","Non heap init memory, MiB", false, MonitoringParameterLevel.SUT),
    NON_HEAP_MEMORY_USED("mon_nonheap_used","Non heap used memory, MiB", false, MonitoringParameterLevel.SUT),
    NON_HEAP_MEMORY_COMMITTED("mon_nonheap_committed","Non heap committed memory, MiB", false, MonitoringParameterLevel.SUT),
    NON_HEAP_MEMORY_MAX("mon_nonheap_max","Non heap max memory, MiB", false, MonitoringParameterLevel.SUT),

    OPEN_FILE_DESCRIPTOR_COUNT("mon_file_descriptors","Count of open file descriptors", false, MonitoringParameterLevel.SUT);

    private String id = "";
    private String description;
    private boolean isCumulativeCounter;
    private MonitoringParameterLevel level;

    DefaultMonitoringParameters(String description, boolean isCumulativeCounter, MonitoringParameterLevel level) {
        this.description = description;
        this.isCumulativeCounter = isCumulativeCounter;
        this.level = level;
    }

    DefaultMonitoringParameters(String id, String description, boolean isCumulativeCounter, MonitoringParameterLevel level) {
        this.id = id;
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
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return getDescription();
    }

}
