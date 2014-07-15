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

    public static String getLatencyMetricName(double latencyKey) {
        return "Latency " + latencyKey + " %";
    }
}