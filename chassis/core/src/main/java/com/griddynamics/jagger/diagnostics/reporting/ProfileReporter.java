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

package com.griddynamics.jagger.diagnostics.reporting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.agent.model.MethodElement;
import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.diagnostics.thread.sampling.InvocationProfile;
import com.griddynamics.jagger.diagnostics.thread.sampling.MethodProfile;
import com.griddynamics.jagger.diagnostics.thread.sampling.RuntimeGraph;
import com.griddynamics.jagger.diagnostics.visualization.GraphVisualizationHelper;
import com.griddynamics.jagger.dbapi.entity.ProfilingSuT;
import com.griddynamics.jagger.monitoring.reporting.AbstractMonitoringReportProvider;
import com.griddynamics.jagger.util.SerializationUtils;
import edu.uci.ics.jung.graph.Graph;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Alexey Kiselyov
 *         Date: 26.07.11
 */
public class ProfileReporter extends AbstractMonitoringReportProvider<String> {
    private Logger log = LoggerFactory.getLogger(ProfileReporter.class);

    private boolean enable;
    private int maxHotSpots;
    private int maxCallersInGraph;
    private int maxCallTreeDepth;
    private int callGraphImageWidth;
    private int callGraphImageHeight;
    private boolean renderGraph;
    private DatabaseService databaseService;

    @Override
    public void clearCache() {
        super.clearCache();
    }

    public boolean isRenderGraph() {
        return this.renderGraph;
    }

    public void setRenderGraph(boolean renderGraph) {
        this.renderGraph = renderGraph;
    }

    public int getMaxHotSpots() {
        return this.maxHotSpots;
    }

    public void setMaxHotSpots(int maxHotSpots) {
        this.maxHotSpots = maxHotSpots;
    }

    public int getMaxCallersInGraph() {
        return this.maxCallersInGraph;
    }

    public void setMaxCallersInGraph(int maxCallersInGraph) {
        this.maxCallersInGraph = maxCallersInGraph;
    }

    public int getMaxCallTreeDepth() {
        return this.maxCallTreeDepth;
    }

    public void setMaxCallTreeDepth(int maxCallTreeDepth) {
        this.maxCallTreeDepth = maxCallTreeDepth;
    }

    public int getCallGraphImageWidth() {
        return this.callGraphImageWidth;
    }

    public void setCallGraphImageWidth(int callGraphImageWidth) {
        this.callGraphImageWidth = callGraphImageWidth;
    }

    public int getCallGraphImageHeight() {
        return this.callGraphImageHeight;
    }

    public void setCallGraphImageHeight(int callGraphImageHeight) {
        this.callGraphImageHeight = callGraphImageHeight;
    }

    @Override
    public JRDataSource getDataSource(String id, String sessionId) {
        if (!enable) {
            return new JRBeanCollectionDataSource(Collections.emptySet());
        }
    
        Map<String, List<SysUnderTestDTO>> sysUnderTests = loadData(sessionId);
    
        Map<String, String> monitoringMap = loadMonitoringMap();

        List<SysUnderTestDTO> data = null;
        String taskId = null;

        Long longId = Long.valueOf(id);
        Map<Long,TaskData> taskDataMap = databaseService.getTaskData(Arrays.asList(longId));
        if (taskDataMap.keySet().contains(longId)) {
            taskId = taskDataMap.get(longId).getTaskId();
        }

        if (taskId != null) {

             data = sysUnderTests.get(taskId);

            if (data == null) {
                data = sysUnderTests.get(relatedMonitoringTask(taskId, monitoringMap));
            }

            // required after monitoring moved to metrics
            if (data == null) {
                data = sysUnderTests.get(parentOf(taskId));
            }
        }

        return new JRBeanCollectionDataSource(data);
    }

    @Deprecated
    private Map<String, List<SysUnderTestDTO>> loadData(String sessionId) {
        Map<String, List<SysUnderTestDTO>> result = Maps.newTreeMap();

        //todo JFG-722 We should delete all queries from reporting-part jagger
        @SuppressWarnings("unchecked")
        List<ProfilingSuT> profilingSuTListFull = (List<ProfilingSuT>) getHibernateTemplate()
                .find("from ProfilingSuT where sessionId=? and taskData_id is not null order by taskData, sysUnderTestUrl", sessionId);

        for (ProfilingSuT profilingSuT : profilingSuTListFull) {
            String taskId = profilingSuT.getTaskData().getTaskId();
            List<SysUnderTestDTO> sysUnderTestDTOs = result.get(taskId);
            if (sysUnderTestDTOs == null) {
                sysUnderTestDTOs = Lists.newArrayList();
                result.put(taskId, sysUnderTestDTOs);
            }

            String currentSysUnderTestUrl = profilingSuT.getSysUnderTestUrl();

            SysUnderTestDTO sysUnderTestDTO = new SysUnderTestDTO();
            sysUnderTestDTO.setSysUnderTestUrl(currentSysUnderTestUrl);
            sysUnderTestDTOs.add(sysUnderTestDTO);

            RuntimeGraph profiler = SerializationUtils.fromString(profilingSuT.getContext());
            List<MethodProfile> profiles = profiler.getSelfTimeHotSpots(maxHotSpots);

            for (MethodProfile profile : profiles) {

                Graph<MethodProfile, InvocationProfile> neighborhood =
                        profiler.getNeighborhood(profile.getMethod(), maxCallersInGraph, maxCallTreeDepth);

                List<MethodElement> traceback = Lists.newArrayList();
                assembleTraceBack(profile, neighborhood, traceback, maxCallTreeDepth, 0);
                traceback.remove(0);

                HotSpotDTO dto = new HotSpotDTO();
                dto.setSysUnderTestUrl(currentSysUnderTestUrl);
                dto.setMethodId(profile.getMethod().toString());
                dto.setInStackRatio(profile.getInStackRatio());
                dto.setOnTopRatio(profile.getOnTopRatio());
                dto.setTraceback(traceback);

                if (renderGraph) {
                    Map<MethodProfile, Paint> customPainter = Maps.newHashMap();
                    customPainter.put(profile, Color.WHITE);

                    Image image = GraphVisualizationHelper.renderGraph(
                            neighborhood,
                            callGraphImageWidth, callGraphImageHeight,
                            GraphVisualizationHelper.GraphLayout.KK,
                            GraphVisualizationHelper.ColorTheme.LIGHT,
                            customPainter);

                    dto.setInvocationGraphImage(image);
                }

                sysUnderTestDTO.getHotSpotDTOs().add(dto);
            }
        }
        return result;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static class SysUnderTestDTO extends MethodElement {
        private String sysUnderTestUrl;
        private List<HotSpotDTO> hotSpotDTOs = Lists.newLinkedList();

        public String getSysUnderTestUrl() {
            return sysUnderTestUrl;
        }

        public void setSysUnderTestUrl(String sysUnderTestUrl) {
            this.sysUnderTestUrl = sysUnderTestUrl;
        }

        public List<HotSpotDTO> getHotSpotDTOs() {
            return hotSpotDTOs;
        }

        public void setHotSpotDTOs(List<HotSpotDTO> hotSpotDTOs) {
            this.hotSpotDTOs = hotSpotDTOs;
        }
    }

    public static class HotSpotDTO extends MethodElement {
        private String sysUnderTestUrl;
        private Image invocationGraphImage;
        private List<MethodElement> traceback;

        public Image getInvocationGraphImage() {
            return invocationGraphImage;
        }

        public void setInvocationGraphImage(Image invocationGraphImage) {
            this.invocationGraphImage = invocationGraphImage;
        }

        public List<MethodElement> getTraceback() {
            return traceback;
        }

        public void setTraceback(List<MethodElement> traceback) {
            this.traceback = traceback;
        }

        public String getSysUnderTestUrl() {
            return sysUnderTestUrl;
        }

        public void setSysUnderTestUrl(String sysUnderTestUrl) {
            this.sysUnderTestUrl = sysUnderTestUrl;
        }
    }

    private void assembleTraceBack(MethodProfile pivot, Graph<MethodProfile, InvocationProfile> neighborhood, List<MethodElement> bag, int maxDepth, int depth) {
        bag.add(assembleMethodElement(pivot, depth));

        if (depth < maxDepth) {
            Collection<MethodProfile> callers = neighborhood.getPredecessors(pivot);

            if (callers != null) {
                List<MethodProfile> callersList = Lists.newArrayList(callers);
                Collections.sort(callersList, new Comparator<MethodProfile>() {
                    @Override
                    public int compare(MethodProfile p1, MethodProfile p2) {
                        return -Double.compare(p1.getInStackRatio(), p2.getInStackRatio());
                    }
                });

                for (MethodProfile caller : callersList) {
                    assembleTraceBack(caller, neighborhood, bag, maxDepth, depth + 1);
                }
            }
        }
    }

    private static MethodElement assembleMethodElement(MethodProfile profile, int offset) {
        MethodElement dto = new MethodElement();
        dto.setMethodId(StringUtils.repeat("     ", offset) + profile.getMethod().toString());
        dto.setInStackRatio(profile.getInStackRatio());
        dto.setOnTopRatio(profile.getOnTopRatio());

        return dto;
    }

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

}
