package com.griddynamics.jagger.user.test.configurations.limits.auxiliary;

import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

/**
 * Enum for standard metrics.
 */
public enum JMetricName {

    SUCCESS_RATE_OK,
    SUCCESS_RATE_FAILS,
    THROUGHPUT,
    VIRTUAL_USERS,
    STD_DEV_LATENCY,
    DURATION,
    AVG_LATENCY,
    ITERATION_SAMPLES,
    MON_CPULA_1,
    MON_CPULA_5,
    MON_CPULA_15,
    MON_GC_MINOR_TIME,
    MON_GC_MAJOR_TIME,
    MON_GC_MINOR_UNIT,
    MON_GC_MAJOR_UNIT,
    MON_MEM_RAM,
    MON_MEM_TOTAL,
    MON_MEM_USED,
    MON_MEM_ACTUAL_USED,
    MON_MEM_FREE_PRCNT,
    MON_MEM_ACTUAL_FREE,
    MON_MEM_FREE,
    MON_TCP_EST,
    MON_TCP_LISTEN,
    MON_SYNC_RECEIVED,
    MON_INBOUND_TOTAL,
    MON_OUTBOUND_TOTAL,
    MON_DISK_READ_BYTES,
    MON_DISK_WRITE_BYTES,
    MON_DISK_SERVICE_TIME,
    MON_DISK_QUEUE_SIZE_TOTAL,
    MON_CPU_USER,
    MON_CPU_SYS_PRCNT,
    MON_CPU_IDLE_PRCNT,
    MON_CPU_WAIT,
    MON_CPU_COMBINED,
    MON_HEAP_INIT,
    MON_HEAP_USED,
    MON_HEAP_COMMITTED,
    MON_HEAP_MAX,
    MON_NONHEAP_INIT,
    MON_NONHEAP_USED,
    MON_NONHEAP_COMMITTED,
    MON_NONHEAP_MAX,
    MON_THREAD_COUNT,
    MON_THREAD_PEAK_COUNT,
    MON_FILE_DESCRIPTORS;


    /**
     * Convert {@link JMetricName} to String.
     *
     * @return String value of current metric name.
     */
    public String transformToString() {
        String name = null;
        switch (this) {
            case SUCCESS_RATE_OK:
                name = StandardMetricsNamesUtil.SUCCESS_RATE_OK_ID;
                break;
            case SUCCESS_RATE_FAILS:
                name = StandardMetricsNamesUtil.SUCCESS_RATE_FAILED_ID;
                break;
            case THROUGHPUT:
                name = StandardMetricsNamesUtil.THROUGHPUT_ID;
                break;
            case VIRTUAL_USERS:
                name = StandardMetricsNamesUtil.VIRTUAL_USERS_ID;
                break;
            case STD_DEV_LATENCY:
                name = StandardMetricsNamesUtil.LATENCY_STD_DEV_ID;
                break;
            case DURATION:
                name = StandardMetricsNamesUtil.DURATION_ID;
                break;
            case AVG_LATENCY:
                name = StandardMetricsNamesUtil.LATENCY_ID;
                break;
            case ITERATION_SAMPLES:
                name = StandardMetricsNamesUtil.ITERATION_SAMPLES_ID;
                break;
            case MON_CPULA_1:
                name = StandardMetricsNamesUtil.MON_CPULA_1;
                break;
            case MON_CPULA_5:
                name = StandardMetricsNamesUtil.MON_CPULA_5;
                break;
            case MON_CPULA_15:
                name = StandardMetricsNamesUtil.MON_CPULA_15;
                break;
            case MON_GC_MINOR_TIME:
                name = StandardMetricsNamesUtil.MON_GC_MINOR_TIME;
                break;
            case MON_GC_MAJOR_TIME:
                name = StandardMetricsNamesUtil.MON_GC_MAJOR_TIME;
                break;
            case MON_GC_MINOR_UNIT:
                name = StandardMetricsNamesUtil.MON_GC_MINOR_UNIT;
                break;
            case MON_GC_MAJOR_UNIT:
                name = StandardMetricsNamesUtil.MON_GC_MAJOR_UNIT;
                break;
            case MON_MEM_RAM:
                name = StandardMetricsNamesUtil.MON_MEM_RAM;
                break;
            case MON_MEM_TOTAL:
                name = StandardMetricsNamesUtil.MON_MEM_TOTAL;
                break;
            case MON_MEM_USED:
                name = StandardMetricsNamesUtil.MON_MEM_USED;
                break;
            case MON_MEM_ACTUAL_USED:
                name = StandardMetricsNamesUtil.MON_MEM_ACTUAL_USED;
                break;
            case MON_MEM_FREE_PRCNT:
                name = StandardMetricsNamesUtil.MON_MEM_FREE_PRCNT;
                break;
            case MON_MEM_ACTUAL_FREE:
                name = StandardMetricsNamesUtil.MON_MEM_ACTUAL_FREE;
                break;
            case MON_MEM_FREE:
                name = StandardMetricsNamesUtil.MON_MEM_FREE;
                break;
            case MON_TCP_EST:
                name = StandardMetricsNamesUtil.MON_TCP_EST;
                break;
            case MON_TCP_LISTEN:
                name = StandardMetricsNamesUtil.MON_TCP_LISTEN;
                break;
            case MON_SYNC_RECEIVED:
                name = StandardMetricsNamesUtil.MON_SYNC_RECEIVED;
                break;
            case MON_INBOUND_TOTAL:
                name = StandardMetricsNamesUtil.MON_INBOUND_TOTAL;
                break;
            case MON_OUTBOUND_TOTAL:
                name = StandardMetricsNamesUtil.MON_OUTBOUND_TOTAL;
                break;
            case MON_DISK_READ_BYTES:
                name = StandardMetricsNamesUtil.MON_DISK_READ_BYTES;
                break;
            case MON_DISK_WRITE_BYTES:
                name = StandardMetricsNamesUtil.MON_DISK_WRITE_BYTES;
                break;
            case MON_DISK_SERVICE_TIME:
                name = StandardMetricsNamesUtil.MON_DISK_SERVICE_TIME;
                break;
            case MON_DISK_QUEUE_SIZE_TOTAL:
                name = StandardMetricsNamesUtil.MON_DISK_QUEUE_SIZE_TOTAL;
                break;
            case MON_CPU_USER:
                name = StandardMetricsNamesUtil.MON_CPU_USER;
                break;
            case MON_CPU_SYS_PRCNT:
                name = StandardMetricsNamesUtil.MON_CPU_SYS_PRCNT;
                break;
            case MON_CPU_IDLE_PRCNT:
                name = StandardMetricsNamesUtil.MON_CPU_IDLE_PRCNT;
                break;
            case MON_CPU_WAIT:
                name = StandardMetricsNamesUtil.MON_CPU_WAIT;
                break;
            case MON_CPU_COMBINED:
                name = StandardMetricsNamesUtil.MON_CPU_COMBINED;
                break;
            case MON_HEAP_INIT:
                name = StandardMetricsNamesUtil.MON_HEAP_INIT;
                break;
            case MON_HEAP_USED:
                name = StandardMetricsNamesUtil.MON_HEAP_USED;
                break;
            case MON_HEAP_COMMITTED:
                name = StandardMetricsNamesUtil.MON_HEAP_COMMITTED;
                break;
            case MON_HEAP_MAX:
                name = StandardMetricsNamesUtil.MON_HEAP_MAX;
                break;
            case MON_NONHEAP_INIT:
                name = StandardMetricsNamesUtil.MON_NONHEAP_INIT;
                break;
            case MON_NONHEAP_USED:
                name = StandardMetricsNamesUtil.MON_NONHEAP_USED;
                break;
            case MON_NONHEAP_COMMITTED:
                name = StandardMetricsNamesUtil.MON_NONHEAP_COMMITTED;
                break;
            case MON_NONHEAP_MAX:
                name = StandardMetricsNamesUtil.MON_NONHEAP_MAX;
                break;
            case MON_THREAD_COUNT:
                name = StandardMetricsNamesUtil.MON_THREAD_COUNT;
                break;
            case MON_THREAD_PEAK_COUNT:
                name = StandardMetricsNamesUtil.MON_THREAD_PEAK_COUNT;
                break;
            case MON_FILE_DESCRIPTORS:
                name = StandardMetricsNamesUtil.MON_FILE_DESCRIPTORS;
            default:
                break;

        }
        return name;
    }

}
