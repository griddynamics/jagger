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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.removePattern;

/**
 * Class is used in chassis, web UI server and web UI client
 * to use it in web UI client - keep it simple (use only standard java libraries)
 */
public class StandardMetricsNamesUtil {
    public static final String THROUGHPUT_TPS = "Throughput, tps";
    public static final String THROUGHPUT = "Throughput";
    public static final String LATENCY_SEC = "Latency, sec";
    public static final String LATENCY_STD_DEV_SEC = "Latency std dev, sec";
    public static final String LATENCY = "Latency";
    public static final String LATENCY_PERCENTILE_REGEX = "Latency\\s\\S+\\s%";
    public static final String ITERATIONS_SAMPLES = "Iterations, samples";
    public static final String SUCCESS_RATE = "Success rate";
    public static final String DURATION_SEC = "Duration, sec";
    public static final String TIME_LATENCY_PERCENTILE = "Time Latency Percentile";
    public static final String VIRTUAL_USERS = "Virtual users";

    // aggregators ids
    public static final String SUCCESS_RATE_AGGREGATOR_OK_ID = "Success rate";
    public static final String SUCCESS_RATE_AGGREGATOR_FAILED_ID = "Number of fails";

    public static final String THROUGHPUT_ID = "throughput";
    public static final String LATENCY_ID = "avgLatency";
    public static final String LATENCY_STD_DEV_ID = "stdDevLatency";
    public static final String SUCCESS_RATE_ID = "successRate";
    public static final String SUCCESS_RATE_OK_ID = "successRate-Success rate";
    public static final String SUCCESS_RATE_FAILED_ID = "successRate-Number of fails";
    public static final String DURATION_ID = "duration";
    public static final String ITERATION_SAMPLES_ID = "samples";

    public static final String VIRTUAL_USERS_ID = "Jagger.Threads";

    // standard monitoring metric names
    public static final String MON_CPULA_1 = "mon_cpula_1";
    public static final String MON_CPULA_5 = "mon_cpula_5";
    public static final String MON_CPULA_15 = "mon_cpula_15";

    public static final String MON_GC_MINOR_TIME = "mon_gc_minor_time";
    public static final String MON_GC_MAJOR_TIME = "mon_gc_major_time";
    public static final String MON_GC_MINOR_UNIT = "mon_gc_minor_unit";
    public static final String MON_GC_MAJOR_UNIT = "mon_gc_major_unit";

    public static final String MON_MEM_RAM = "mon_mem_ram";
    public static final String MON_MEM_TOTAL = "mon_mem_total";
    public static final String MON_MEM_USED = "mon_mem_used";
    public static final String MON_MEM_ACTUAL_USED = "mon_mem_actual_used";
    public static final String MON_MEM_FREE_PRCNT = "mon_mem_free_prcnt";
    public static final String MON_MEM_ACTUAL_FREE = "mon_mem_actual_free";
    public static final String MON_MEM_FREE = "mon_mem_free";

    public static final String MON_TCP_EST = "mon_tcp_est";
    public static final String MON_TCP_LISTEN = "mon_tcp_listen";
    public static final String MON_SYNC_RECEIVED = "mon_sync_received";
    public static final String MON_INBOUND_TOTAL = "mon_inbound_total";
    public static final String MON_OUTBOUND_TOTAL = "mon_outbound_total";

    public static final String MON_DISK_READ_BYTES = "mon_disk_read_bytes";
    public static final String MON_DISK_WRITE_BYTES = "mon_disk_write_bytes";

    public static final String MON_DISK_SERVICE_TIME = "mon_disk_service_time";
    public static final String MON_DISK_QUEUE_SIZE_TOTAL = "mon_disk_queue_size_total";

    public static final String MON_CPU_USER = "mon_cpu_user";
    public static final String MON_CPU_SYS_PRCNT = "mon_cpu_sys_prcnt";
    public static final String MON_CPU_IDLE_PRCNT = "mon_cpu_idle_prcnt";
    public static final String MON_CPU_WAIT = "mon_cpu_wait";
    public static final String MON_CPU_COMBINED = "mon_cpu_combined";

    public static final String MON_HEAP_INIT = "mon_heap_init";
    public static final String MON_HEAP_USED = "mon_heap_used";
    public static final String MON_HEAP_COMMITTED = "mon_heap_committed";
    public static final String MON_HEAP_MAX = "mon_heap_max";

    public static final String MON_NONHEAP_INIT = "mon_nonheap_init";
    public static final String MON_NONHEAP_USED = "mon_nonheap_used";
    public static final String MON_NONHEAP_COMMITTED = "mon_nonheap_committed";
    public static final String MON_NONHEAP_MAX = "mon_nonheap_max";

    public static final String MON_THREAD_COUNT = "mon_thread_count";
    public static final String MON_THREAD_PEAK_COUNT = "mon_thread_peak_count";

    public static final String MON_FILE_DESCRIPTORS = "mon_file_descriptors";


    public static String getLatencyMetricName(double latencyKey) {
        return "Latency " + latencyKey + " %";
    }

    public static Double parseLatencyPercentileKey(String metricName) {
        return Double.parseDouble(metricName.substring(
                metricName.indexOf("Latency ") + "Latency ".length(),
                metricName.indexOf(" %")
        ));
    }

    public static class IdContainer {
        private final String scenarioId;
        private final String stepId;
        private final String metricId;

        public IdContainer(String scenarioId, String stepId, String metricId) {
            this.scenarioId = scenarioId;
            this.stepId = stepId;
            this.metricId = metricId;
        }

        public String getScenarioId() {
            return scenarioId;
        }

        public String getStepId() {
            return stepId;
        }

        public String getMetricId() {
            return metricId;
        }
    }

    public static final String USER_SCENARIO_ID = "US_";
    public static final String US_STEP_ID = "_STNN";
    public static final String US_METRIC_ID = "METR_";
    public static final String IS_SCENARIO_REGEXP = "^.*" + USER_SCENARIO_ID + ".*" + US_STEP_ID + "\\d+.*";

    public static String generateScenarioStepId(String scenarioId, String stepId, Integer stepIndex) {
        // both scenario and scenario steps will have same format of ids
        // scenario: US_[scenarioId]_STNN0_[scenarioId]
        // step:     US_[scenarioId]_STNN[1...N]_[stepId]
        return USER_SCENARIO_ID + scenarioId + US_STEP_ID + stepIndex + "_" + stepId + "_";
    }

    public static String generateScenarioId(String scenarioId) {
        return generateScenarioStepId(scenarioId, scenarioId, 0);
    }

    public static String generateMetricId(String id, String metricId) {
        return id + US_METRIC_ID + metricId + "_";
    }

    public static String generateMetricDisplayName(String displayName, String metricDisplayName) {
        return displayName + " " + metricDisplayName;
    }

    public static Boolean isBelongingToScenario(String metricNodeId) {
        return metricNodeId.matches(IS_SCENARIO_REGEXP);
    }

    public static IdContainer getIdsFromGeneratedIdForScenarioComponents(String generatedId) {
        String userScenarioRegex = "^.*" + USER_SCENARIO_ID + "(.*)" + US_STEP_ID + "(\\d+)_(.*)_" + US_METRIC_ID + "(.*)_(-.*)?$";
        Pattern pattern = Pattern.compile(userScenarioRegex);
        Matcher matcher = pattern.matcher(generatedId);
        if (matcher.matches()) {
            String scenarioId = matcher.group(1);
            String stepId = matcher.group(3);
            String metricId = matcher.group(4);
            return new IdContainer(scenarioId, stepId, metricId);
        }
        return null;
    }

    public static String extractDisplayNameFromGenerated(String generatedDisplayName) {
       if (generatedDisplayName.matches("^.*(" + ITERATIONS_SAMPLES + "|Latency, sec|" + SUCCESS_RATE + ").*")) {
           return removePattern(generatedDisplayName, " (Latency, sec|" + ITERATIONS_SAMPLES + "|" + SUCCESS_RATE + ") \\[.*\\]");
       }
       return null;
    }

    public static String generateScenarioRegexp(String scenarioId) {
        return "(^.*" + USER_SCENARIO_ID + scenarioId + US_STEP_ID + ".*$)|(^.*" + scenarioId + ".*(-sum|-Success rate|-Number of fails).*$)";
    }

    public static String generateScenarioStepRegexp(String scenarioId, String stepId) {
        return "^.*" + USER_SCENARIO_ID + scenarioId + US_STEP_ID + "\\d+_" + stepId + "_" + US_METRIC_ID + ".*$";
    }
}
