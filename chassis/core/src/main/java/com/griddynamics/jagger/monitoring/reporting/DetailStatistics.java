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

package com.griddynamics.jagger.monitoring.reporting;

import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.monitoring.model.MonitoringStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * User: dkotlyarov
 */
public final class DetailStatistics implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(DetailStatistics.class);

    private final String sessionId;
    private final LinkedHashSet<String> taskIds = new LinkedHashSet<String>();
    private final LinkedHashMap<String, LinkedHashMap<MonitoringParameter, List<MonitoringStatistics>>> globalStatistics = new LinkedHashMap<String, LinkedHashMap<MonitoringParameter, List<MonitoringStatistics>>>();
    private final LinkedHashMap<String, LinkedHashSet<String>> boxIdentifiers = new LinkedHashMap<String, LinkedHashSet<String>>();
    private final LinkedHashMap<String, LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>>> boxStatistics = new LinkedHashMap<String, LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>>>();
    private final LinkedHashMap<String, LinkedHashSet<String>> sutUrls = new LinkedHashMap<String, LinkedHashSet<String>>();
    private final LinkedHashMap<String, LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>>> sutStatistics = new LinkedHashMap<String, LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>>>();

    public DetailStatistics(String sessionId, List<MonitoringStatistics> statistics) {
        log.info("BEGIN: Detail statistics generation");

        this.sessionId = sessionId;

        for (MonitoringStatistics ms : statistics) {
            String taskId = ms.getTaskData().getTaskId();
            MonitoringParameter parameter = ms.getParameterId();
            String boxIdentifier = ms.getBoxIdentifier();
            String systemUnderTestUrl = ms.getSystemUnderTestUrl();

            if (!taskIds.contains(taskId)) {
                taskIds.add(taskId);
            }

            if (boxIdentifier != null) {
                LinkedHashMap<MonitoringParameter, List<MonitoringStatistics>> taskStatistics = globalStatistics.get(taskId);
                if (taskStatistics == null) {
                    taskStatistics = new LinkedHashMap<MonitoringParameter, List<MonitoringStatistics>>();
                    globalStatistics.put(taskId, taskStatistics);
                }

                List<MonitoringStatistics> parameterStatistics = taskStatistics.get(parameter);
                if (parameterStatistics == null) {
                    parameterStatistics = new ArrayList<MonitoringStatistics>(32);
                    taskStatistics.put(parameter, parameterStatistics);
                }

                parameterStatistics.add(ms);

                LinkedHashSet<String> taskBoxIdentifiers = boxIdentifiers.get(taskId);
                if (taskBoxIdentifiers == null) {
                    taskBoxIdentifiers = new LinkedHashSet<String>();
                    boxIdentifiers.put(taskId, taskBoxIdentifiers);
                }

                if (!taskBoxIdentifiers.contains(boxIdentifier)) {
                    taskBoxIdentifiers.add(boxIdentifier);
                }

                LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>> taskBoxStatistics = boxStatistics.get(taskId);
                if (taskBoxStatistics == null) {
                    taskBoxStatistics = new LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>>();
                    boxStatistics.put(taskId, taskBoxStatistics);
                }

                LinkedHashMap<String, List<MonitoringStatistics>> parameterBoxStatistics = taskBoxStatistics.get(parameter);
                if (parameterBoxStatistics == null) {
                    parameterBoxStatistics = new LinkedHashMap<String, List<MonitoringStatistics>>();
                    taskBoxStatistics.put(parameter, parameterBoxStatistics);
                }

                List<MonitoringStatistics> boxBoxStatistics = parameterBoxStatistics.get(boxIdentifier);
                if (boxBoxStatistics == null) {
                    boxBoxStatistics = new ArrayList<MonitoringStatistics>(32);
                    parameterBoxStatistics.put(boxIdentifier, boxBoxStatistics);
                }

                boxBoxStatistics.add(ms);
            }

            if (systemUnderTestUrl != null) {
                LinkedHashSet<String> taskSutUrls = sutUrls.get(taskId);
                if (taskSutUrls == null) {
                    taskSutUrls = new LinkedHashSet<String>();
                    sutUrls.put(taskId, taskSutUrls);
                }

                if (!taskSutUrls.contains(systemUnderTestUrl)) {
                    taskSutUrls.add(systemUnderTestUrl);
                }

                LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>> taskSutStatistics = sutStatistics.get(taskId);
                if (taskSutStatistics == null) {
                    taskSutStatistics = new LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>>();
                    sutStatistics.put(taskId, taskSutStatistics);
                }

                LinkedHashMap<String, List<MonitoringStatistics>> parameterSutStatistics = taskSutStatistics.get(parameter);
                if (parameterSutStatistics == null) {
                    parameterSutStatistics = new LinkedHashMap<String, List<MonitoringStatistics>>();
                    taskSutStatistics.put(parameter, parameterSutStatistics);
                }

                List<MonitoringStatistics> sutSutStatistics = parameterSutStatistics.get(systemUnderTestUrl);
                if (sutSutStatistics == null) {
                    sutSutStatistics = new ArrayList<MonitoringStatistics>(32);
                    parameterSutStatistics.put(systemUnderTestUrl, sutSutStatistics);
                }

                sutSutStatistics.add(ms);
            }
        }

        log.info("END: Detail statistics generation");
    }

    public String getSessionId() {
        return sessionId;
    }

    public Set<String> findTaskIds() {
        return taskIds;
    }

    public List<MonitoringStatistics> findGlobalStatistics(String taskId, MonitoringParameter parameterId) {
        LinkedHashMap<MonitoringParameter, List<MonitoringStatistics>> taskStatistics = globalStatistics.get(taskId);
        if (taskStatistics != null) {
            List<MonitoringStatistics> parameterStatistics = taskStatistics.get(parameterId);
            if (parameterStatistics != null) {
                return parameterStatistics;
            }
        }
        return Collections.emptyList();
    }

    public Set<String> findBoxIdentifiers(String taskId) {
        LinkedHashSet<String> taskBoxIdentifiers = boxIdentifiers.get(taskId);
        if (taskBoxIdentifiers != null) {
            return taskBoxIdentifiers;
        }
        return Collections.emptySet();
    }

    public List<MonitoringStatistics> findBoxStatistics(String taskId, MonitoringParameter parameterId, String boxIdentifier) {
        LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>> taskBoxStatistics = boxStatistics.get(taskId);
        if (taskBoxStatistics != null) {
            LinkedHashMap<String, List<MonitoringStatistics>> parameterBoxStatistics = taskBoxStatistics.get(parameterId);
            if (parameterBoxStatistics != null) {
                List<MonitoringStatistics> boxBoxStatistics = parameterBoxStatistics.get(boxIdentifier);
                if (boxBoxStatistics != null) {
                    return boxBoxStatistics;
                }
            }
        }
        return Collections.emptyList();
    }

    public Set<String> findSutUrls(String taskId) {
        LinkedHashSet<String> taskSutUrls = sutUrls.get(taskId);
        if (taskSutUrls != null) {
            return taskSutUrls;
        }
        return Collections.emptySet();
    }

    public List<MonitoringStatistics> findSutStatistics(String taskId, MonitoringParameter parameterId, String sutUrl) {
        LinkedHashMap<MonitoringParameter, LinkedHashMap<String, List<MonitoringStatistics>>> taskSutStatistics = sutStatistics.get(taskId);
        if (taskSutStatistics != null) {
            LinkedHashMap<String, List<MonitoringStatistics>> parameterSutStatistics = taskSutStatistics.get(parameterId);
            if (parameterSutStatistics != null) {
                List<MonitoringStatistics> sutSutStatistics = parameterSutStatistics.get(sutUrl);
                if (sutSutStatistics != null) {
                    return sutSutStatistics;
                }
            }
        }
        return Collections.emptyList();
    }
}
