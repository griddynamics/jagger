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

/**
 * Class is used in chassis, web UI server and web UI client
 * to use it in web UI client - keep it simple (use only standard java libraries)
 */
public class MonitoringIdUtils {

    /**
     * used to separate monitoring plot name and agent id in MetricNameDto.metricName/SessionNameDto.metricName
     * note: '|' == '%7C' in while link processing
     */
    public static final String AGENT_NAME_SEPARATOR = "|";

    // not all special characters are escaped!
    // only required for agent Ids generation
    public static String getEscapedStringForRegex(String input) {
        return input.
                replace("[","\\[").replace("]","\\]").
                replace("(", "\\(").replace(")","\\)").
                replace("|","\\|");
    }


    // keep functionality of following functions dependent
    // direct
    public static String getMonitoringMetricId(String monitoringName, String agentName){
        return monitoringName + AGENT_NAME_SEPARATOR + agentName + AGENT_NAME_SEPARATOR;
    }
    // backwards
    public static MonitoringId splitMonitoringMetricId(String monitoringMetricId) {
        String splitString[] = monitoringMetricId.split("\\" + AGENT_NAME_SEPARATOR); // escape special char for regex
        if (splitString.length > 1) {
            return new MonitoringId(splitString[0],splitString[1]);
        }
        return null;
    }

    public static class MonitoringId {
        private String monitoringName;
        private String agentName;

        public MonitoringId(String monitoringName, String agentName) {
            this.monitoringName = monitoringName;
            this.agentName = agentName;
        }

        public String getAgentName() {
            return agentName;
        }

        public String getMonitoringName() {
            return monitoringName;
        }
    }

}
