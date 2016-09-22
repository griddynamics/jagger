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

package com.griddynamics.jagger.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

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
    public static final String LATENCY_PERCENTILE_REGEX = "Latency\\s\\S+\\s%(-old)?";
    public static final String ITERATIONS_SAMPLES = "Iterations, samples";
    public static final String SUCCESS_RATE = "Success rate";
    public static final String DURATION_SEC = "Duration, sec";
    public static final String TIME_LATENCY_PERCENTILE = "Time Latency Percentile";
    public static final String FAIL_COUNT = "Number of failures";

    //begin: following section is used for docu generation - standard metrics ids
    public static final String THROUGHPUT_ID = "throughput";
    public static final String LATENCY_ID = "avgLatency";
    public static final String LATENCY_STD_DEV_ID = "stdDevLatency";
    public static final String FAIL_COUNT_ID = "failureCount";
    public static final String SUCCESS_RATE_ID = "successRate";
    public static final String DURATION_ID = "duration";
    public static final String ITERATION_SAMPLES_ID = "samples";
    //end: following section is used for docu generation - standard metrics ids

    // ids for standard metrics saved with old model (in WorkloadTaskData, TimeLatencyPercentile, etc)
    public static final String THROUGHPUT_OLD_ID = "throughput-old";
    public static final String LATENCY_OLD_ID = "avgLatency-old";
    public static final String LATENCY_STD_DEV_OLD_ID = "stdDevLatency-old";
    public static final String FAIL_COUNT_OLD_ID = "failureCount-old";
    public static final String SUCCESS_RATE_OLD_ID = "successRate-old";
    public static final String DURATION_OLD_ID = "duration-old";
    public static final String ITERATION_SAMPLES_OLD_ID = "samples-old";

    private static final HashSet<String> NEW_NAMES = newHashSet(THROUGHPUT_ID, LATENCY_ID, LATENCY_STD_DEV_ID, FAIL_COUNT_ID, SUCCESS_RATE_ID, DURATION_ID, ITERATION_SAMPLES_ID);

    public static String getLatencyMetricName(double latencyKey, boolean isOldModel) {
        if (isOldModel) {
            return "Latency " + latencyKey + " %-old";
        } else {
            return "Latency " + latencyKey + " %";
        }
    }

    public static Double parseLatencyPercentileKey(String metricName) {
        return Double.parseDouble(metricName.substring(
                metricName.indexOf("Latency ") + "Latency ".length(),
                metricName.indexOf(" %")
        ));
    }


    public static List<String> getSynonyms(String metricName) {
        if (synonyms.isEmpty()) {
            populateSynonyms();
        }

        // hard coded synonyms
        if (synonyms.containsKey(metricName)) {
            return new ArrayList<>(synonyms.get(metricName));
        }

        // dynamic synonyms for latency percentiles
        List<String> value = getSynonymsForLatencyPercentileMetric(metricName);
        if (value != null) return value;

        return null;
    }

    private static List<String> getSynonymsForLatencyPercentileMetric(String metricName) {
        if (metricName.matches(LATENCY_PERCENTILE_REGEX)) {
            Double value = parseLatencyPercentileKey(metricName);
            String percentileNewModelMetricName = getLatencyMetricName(value, false);
            if (metricName.equals(percentileNewModelMetricName)) {
                return new ArrayList<>(asList(getLatencyMetricName(value, true), TIME_LATENCY_PERCENTILE));
            } else {
                return new ArrayList<>(singletonList(percentileNewModelMetricName));
            }

        }
        return null;
    }

    // Standard metrics can be stored in DB under different ids due to back compatibility
    public static Set<String> getAllVariantsOfMetricName(String metricName) {
        Set<String> result = new HashSet<>();

        result.add(metricName);

        List<String> synonyms = getSynonyms(metricName);
        if (synonyms != null) {
            result.addAll(synonyms);
        }

        return result;
    }

    private static Map<String, List<String>> synonyms = new HashMap<>();

    private static void populateSynonyms() {
        // old to very old and new
        synonyms.put(THROUGHPUT_OLD_ID, asList(THROUGHPUT_ID, THROUGHPUT));
        synonyms.put(LATENCY_OLD_ID, asList(LATENCY_ID, LATENCY));
        synonyms.put(LATENCY_STD_DEV_OLD_ID, singletonList(LATENCY_STD_DEV_ID));
        synonyms.put(FAIL_COUNT_OLD_ID, singletonList(FAIL_COUNT_ID));
        synonyms.put(SUCCESS_RATE_OLD_ID, asList(SUCCESS_RATE_ID, SUCCESS_RATE));
        synonyms.put(DURATION_OLD_ID, asList(DURATION_ID, "Duration"));
        synonyms.put(ITERATION_SAMPLES_OLD_ID, asList(ITERATION_SAMPLES_ID, "Iterations"));

        // new to old and very old :-)
        synonyms.put(THROUGHPUT_ID, asList(THROUGHPUT_OLD_ID, THROUGHPUT));
        synonyms.put(LATENCY_ID, asList(LATENCY_OLD_ID, LATENCY));
        synonyms.put(LATENCY_STD_DEV_ID, singletonList(LATENCY_STD_DEV_OLD_ID));
        synonyms.put(FAIL_COUNT_ID, singletonList(FAIL_COUNT_OLD_ID));
        synonyms.put(SUCCESS_RATE_ID, asList(SUCCESS_RATE_OLD_ID, SUCCESS_RATE));
        synonyms.put(DURATION_ID, asList(DURATION_OLD_ID, "Duration"));
        synonyms.put(ITERATION_SAMPLES_ID, asList(ITERATION_SAMPLES_OLD_ID, "Iterations"));
    }

    public static String getNewMetricNameForOld(String metricName) {
        // metricName is new
        if (NEW_NAMES.contains(metricName)) {
            return metricName;
        }

        // metricName is old
        List<String> synonyms = getSynonyms(metricName);
        if (synonyms != null && NEW_NAMES.contains(synonyms.get(0))) {
            return synonyms.get(0);
        }

        // metricName is LATENCY_PERCENTILE
        if (metricName.matches(LATENCY_PERCENTILE_REGEX)) {
            return getSynonymsForLatencyPercentileMetric(metricName).get(0);
        }

        // metricName is custom metric
        return metricName;
    }
}
