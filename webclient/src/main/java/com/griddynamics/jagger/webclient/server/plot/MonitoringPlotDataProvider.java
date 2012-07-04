package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadData;
import com.griddynamics.jagger.monitoring.model.MonitoringStatistics;
import com.griddynamics.jagger.monitoring.model.PerformedMonitoring;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.webclient.client.dto.MarkingDto;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import com.griddynamics.jagger.webclient.server.LegendProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/5/12
 */
public class MonitoringPlotDataProvider implements PlotDataProvider, SessionScopePlotDataProvider {
    private static final Logger log = LoggerFactory.getLogger(MonitoringPlotDataProvider.class);
    private static final String IP_ADDRESS_REG_EXP = ".*[\\[\\(](?:\\d{1,3}\\.){3}\\d{1,3}[\\]\\)]";

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;
    private LegendProvider legendProvider;
    private EntityManager entityManager;
    private boolean renderTaskBoundaries;

    //==========Constructors

    public MonitoringPlotDataProvider(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    //==========Getters & Setters

    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    public void setRenderTaskBoundaries(boolean renderTaskBoundaries) {
        this.renderTaskBoundaries = renderTaskBoundaries;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    //==========================
    //==========Contract Methods
    //==========================

    /**
     * Returns list of PlotSeriesDto for given task ID and plot name
     *
     * @param taskId   task ID
     * @param plotName plot name
     * @return list of PlotSeriesDto
     * @see PlotSeriesDto
     */
    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
        DefaultMonitoringParameters[] defaultMonitoringParametersGroup = findDefaultMonitoringParameters(monitoringPlotGroups, plotName);
        List<String> monitoringParametersList = assembleDefaultMonitoringParametersDescriptions(defaultMonitoringParametersGroup);
        log.debug("For plot {} there are exist {} monitoring parameters", plotName, defaultMonitoringParametersGroup);

        TaskData workloadTaskData = entityManager.find(TaskData.class, taskId);

        WorkloadData workloadData = findWorkloadDataBySessionIdAndTaskId(workloadTaskData.getSessionId(), workloadTaskData.getTaskId());

        TaskData monitoringTaskData = findMonitoringTaskDataBySessionIdAndParentId(workloadData.getSessionId(), workloadData.getParentId());

        List<MonitoringStatistics> monitoringStatisticsList = findAllMonitoringStatisticsByMonitoringTaskDataAndDescriptionInList(monitoringTaskData, monitoringParametersList);

        return assemble(composeByBoxIdentifierAndDescription(monitoringStatisticsList, false), plotName, Collections.singleton(taskId));
    }

    /**
     * Returns list of PlotSeriesDto for given session ID and plot name
     *
     * @param sessionId session ID
     * @param plotName  plot name
     * @return list of PlotSeriesDto
     * @see PlotSeriesDto
     */
    @Override
    public List<PlotSeriesDto> getPlotData(String sessionId, String plotName) {
        List<WorkloadData> workloadDataList = findAllWorkloadDataBySessionId(sessionId);
        Map<String, WorkloadData> workloadDataMap = createIndexParentId2WorkloadData(workloadDataList);

        Date startTime = findSessionTasksStartTime(sessionId);

        Map<String, PerformedMonitoring> performedMonitoringMap = createIndexMonitoringId2PerformedMonitoring(findAllPerformedMonitoringBySessionId(sessionId));

        DefaultMonitoringParameters[] defaultMonitoringParametersGroup = findDefaultMonitoringParameters(monitoringPlotGroups, plotName);
        List<String> monitoringParametersList = assembleDefaultMonitoringParametersDescriptions(defaultMonitoringParametersGroup);
        log.debug("For plot {} there are exist {} monitoring parameters", plotName, defaultMonitoringParametersGroup);

        List<MonitoringStatistics> monitoringStatisticsList = findAllMonitoringStatisticsBySessionIdAndDescriptionInList(sessionId, monitoringParametersList);

        List<PlotSeriesDto> plotSeriesDtoList = new ArrayList<PlotSeriesDto>();
        for (Map.Entry<String, Map<String, List<MonitoringStatistics>>> entry : composeByBoxIdentifierAndDescription(monitoringStatisticsList, false).entrySet()) {
            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();
            String boxIdentifier = entry.getKey();

            for (Map.Entry<String, List<MonitoringStatistics>> boxEntry : entry.getValue().entrySet()) {
                String description = boxEntry.getKey();

                List<PointDto> pointDtoList = new ArrayList<PointDto>();
                for (MonitoringStatistics monitoringStatistics : boxEntry.getValue()) {

                    String taskId = monitoringStatistics.getTaskData().getTaskId();
                    WorkloadData workloadData = workloadDataMap.get(performedMonitoringMap.get(taskId).getParentId());
                    long timeOffsetInMillis = workloadData.getStartTime().getTime() - startTime.getTime();

                    double time = DataProcessingUtil.round((timeOffsetInMillis + monitoringStatistics.getTime()) / 1000.0D);
                    double value = DataProcessingUtil.round(monitoringStatistics.getAverageValue());

                    pointDtoList.add(new PointDto(time, value));
                } //for

                PlotDatasetDto plotDatasetDto = new PlotDatasetDto(pointDtoList, description, ColorCodeGenerator.getHexColorCode());
                plotDatasetDtoList.add(plotDatasetDto);
            }

            List<MarkingDto> markings = null;
            if (renderTaskBoundaries && workloadDataList.size() > 1) {
                markings = new ArrayList<MarkingDto>();

                double start = workloadDataList.get(0).getStartTime().getTime();
                for (WorkloadData wd : workloadDataList) {

                    double endTime = wd.getEndTime().getTime();
                    double duration = DataProcessingUtil.round((endTime - start) / 1000.0D);

                    String taskName = (String) entityManager.createQuery("select td.taskName from TaskData as td where td.sessionId=:sessionId and td.taskId=:taskId")
                            .setParameter("sessionId", wd.getSessionId())
                            .setParameter("taskId", wd.getTaskId())
                            .getSingleResult();

                    MarkingDto plotDatasetDto = new MarkingDto(duration, "#a4a4a4", taskName);
                    markings.add(plotDatasetDto);
                }
            }

            plotSeriesDtoList.add(new PlotSeriesDto(plotDatasetDtoList, "Time, sec", "", "Session #" + sessionId + " " + plotName + " on " + boxIdentifier, markings));
        }

        return plotSeriesDtoList;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(Set<Long> taskIds, String plotName) {
        checkNotNull(taskIds, "taskIds is null");
        checkArgument(!taskIds.isEmpty(), "taskIds is empty");
        checkNotNull(plotName, "plotName is null");

        DefaultMonitoringParameters[] defaultMonitoringParametersGroup = findDefaultMonitoringParameters(monitoringPlotGroups, plotName);
        List<String> monitoringParametersList = assembleDefaultMonitoringParametersDescriptions(defaultMonitoringParametersGroup);
        log.debug("For plot {} there are exist {} monitoring parameters", plotName, defaultMonitoringParametersGroup);

        Map<String, Map<String, List<MonitoringStatistics>>> finalComposedMap = new HashMap<String, Map<String, List<MonitoringStatistics>>>();
        for (long taskId : taskIds) {
            TaskData workloadTaskData = entityManager.find(TaskData.class, taskId);

            WorkloadData workloadData = findWorkloadDataBySessionIdAndTaskId(workloadTaskData.getSessionId(), workloadTaskData.getTaskId());

            TaskData monitoringTaskData = findMonitoringTaskDataBySessionIdAndParentId(workloadData.getSessionId(), workloadData.getParentId());

            List<MonitoringStatistics> monitoringStatisticsList = findAllMonitoringStatisticsByMonitoringTaskDataAndDescriptionInList(monitoringTaskData, monitoringParametersList);

            Map<String, Map<String, List<MonitoringStatistics>>> composedMap = composeByBoxIdentifierAndDescription(monitoringStatisticsList, true);

            for (Map.Entry<String, Map<String, List<MonitoringStatistics>>> boxEntry : composedMap.entrySet()) {
                if (!finalComposedMap.containsKey(boxEntry.getKey())) {
                    finalComposedMap.put(boxEntry.getKey(), new HashMap<String, List<MonitoringStatistics>>());
                }

                for (Map.Entry<String, List<MonitoringStatistics>> descrEntry : boxEntry.getValue().entrySet()) {
                    if (!finalComposedMap.get(boxEntry.getKey()).containsKey(descrEntry.getKey())) {
                        finalComposedMap.get(boxEntry.getKey()).put(descrEntry.getKey(), new ArrayList<MonitoringStatistics>());
                    }
                    finalComposedMap.get(boxEntry.getKey()).get(descrEntry.getKey()).addAll(descrEntry.getValue());
                }
            }
        }

        return assemble(finalComposedMap, plotName, taskIds);
    }

    //============================
    //===========Auxiliary Methods
    //============================

    /**
     * Composed monitoring statistics to map: [ boxID -> [ description -> list of monitoringStatistics ] ]
     *
     * @param monitoringStatisticsList
     * @return
     */
    protected Map<String, Map<String, List<MonitoringStatistics>>> composeByBoxIdentifierAndDescription(List<MonitoringStatistics> monitoringStatisticsList, boolean addSessionPrefix) {
        Map<String, Map<String, List<MonitoringStatistics>>> map = new HashMap<String, Map<String, List<MonitoringStatistics>>>();

        for (MonitoringStatistics monitoringStatistics : monitoringStatisticsList) {
            String boxIdentifier = monitoringStatistics.getBoxIdentifier() != null ? monitoringStatistics.getBoxIdentifier() : monitoringStatistics.getSystemUnderTestUrl();
            boxIdentifier = cutBoxUuid(boxIdentifier);

            String description = legendProvider.generatePlotLegend(monitoringStatistics.getTaskData().getSessionId(), monitoringStatistics.getParameterId().getDescription(), addSessionPrefix);

            if (!map.containsKey(boxIdentifier)) {
                map.put(boxIdentifier, new HashMap<String, List<MonitoringStatistics>>());
            }
            Map<String, List<MonitoringStatistics>> descriptionsMap = map.get(boxIdentifier);
            if (!descriptionsMap.containsKey(description)) {
                descriptionsMap.put(description, new ArrayList<MonitoringStatistics>());
            }
            descriptionsMap.get(description).add(monitoringStatistics);
        }
        return map;
    }

    protected List<PlotSeriesDto> assemble(Map<String, Map<String, List<MonitoringStatistics>>> composedMap, String plotName, Set<Long> taskIds) {
        List<PlotSeriesDto> plotSeriesDtoList = new ArrayList<PlotSeriesDto>();
        for (Map.Entry<String, Map<String, List<MonitoringStatistics>>> entry : composedMap.entrySet()) {
            String boxIdentifier = entry.getKey();

            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();
            for (Map.Entry<String, List<MonitoringStatistics>> boxEntry : entry.getValue().entrySet()) {
                String description = boxEntry.getKey();

                List<PointDto> pointDtoList = new ArrayList<PointDto>();
                for (MonitoringStatistics monitoringStatistics : boxEntry.getValue()) {
                    pointDtoList.add(new PointDto(DataProcessingUtil.round(monitoringStatistics.getTime() / 1000.0D), DataProcessingUtil.round(monitoringStatistics.getAverageValue())));
                }

                plotDatasetDtoList.add(new PlotDatasetDto(pointDtoList, description, ColorCodeGenerator.getHexColorCode()));
            }

            plotSeriesDtoList.add(new PlotSeriesDto(plotDatasetDtoList, "Time, sec", "", legendProvider.getPlotHeader(taskIds, plotName + " on " + boxIdentifier)));
        }

        return plotSeriesDtoList;
    }

    /**
     * Uses to cut box UUID for cross session plot comparing
     *
     * @param boxIdentifier
     * @return
     */
    private String cutBoxUuid(String boxIdentifier) {
        if (!boxIdentifier.matches(IP_ADDRESS_REG_EXP)) {
            return boxIdentifier;
        }
        if (boxIdentifier.contains("jmx port")) {
            return boxIdentifier;
        }

        String toCut = boxIdentifier.substring(boxIdentifier.indexOf("-"), boxIdentifier.indexOf("[") - 1);
        return boxIdentifier.replace(toCut, "");
    }

    private DefaultMonitoringParameters[] findDefaultMonitoringParameters(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups, String plotName) {
        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> entry : monitoringPlotGroups.entrySet()) {
            if (entry.getKey().getUpperName().equalsIgnoreCase(plotName)) {
                return entry.getValue();
            }
        }

        throw new IllegalStateException("Appropriate defaultMonitoringParameters array is not found in monitoringPlotGroups for plot name: " + plotName);
    }

    private Map<String, WorkloadData> createIndexParentId2WorkloadData(List<WorkloadData> workloadDataList) {
        Map<String, WorkloadData> workloadDataMap = new HashMap<String, WorkloadData>();
        for (WorkloadData workloadData : workloadDataList) {
            workloadDataMap.put(workloadData.getParentId(), workloadData);
        }

        return workloadDataMap;
    }

    private Map<String, PerformedMonitoring> createIndexMonitoringId2PerformedMonitoring(List<PerformedMonitoring> performedMonitoringList) {
        Map<String, PerformedMonitoring> performedMonitoringMap = new HashMap<String, PerformedMonitoring>();
        for (PerformedMonitoring performedMonitoring : performedMonitoringList) {
            performedMonitoringMap.put(performedMonitoring.getMonitoringId(), performedMonitoring);
        }

        return performedMonitoringMap;
    }

    @SuppressWarnings("unchecked")
    private List<PerformedMonitoring> findAllPerformedMonitoringBySessionId(String sessionId) {

        return entityManager.createQuery("select pm from PerformedMonitoring as pm where pm.sessionId=:sessionId")
                .setParameter("sessionId", sessionId)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<WorkloadData> findAllWorkloadDataBySessionId(String sessionId) {
        return entityManager.createQuery("select wd from WorkloadData as wd where wd.sessionId=:sessionId order by wd.endTime asc")
                .setParameter("sessionId", sessionId)
                .getResultList();
    }

    private WorkloadData findWorkloadDataBySessionIdAndTaskId(String sessionId, String taskId) {
        return (WorkloadData) entityManager.createQuery("select wd from WorkloadData as wd where wd.sessionId=:sessionId and wd.taskId=:taskId")
                .setParameter("sessionId", sessionId)
                .setParameter("taskId", taskId)
                .getSingleResult();
    }

    private Date findSessionTasksStartTime(String sessionId) {
        return (Date) entityManager.createQuery("select min(wd.startTime) from WorkloadData as wd where wd.sessionId=:sessionId")
                .setParameter("sessionId", sessionId)
                .getSingleResult();
    }

    @SuppressWarnings("unchecked")
    private List<MonitoringStatistics> findAllMonitoringStatisticsBySessionIdAndDescriptionInList(String sessionId, List<String> monitoringParametersList) {
        return entityManager.createQuery("select ms from MonitoringStatistics as ms where sessionId=:sessionId " +
                "and ms.parameterId.description in (:descrList) order by ms.taskData.number asc, ms.time asc")
                .setParameter("sessionId", sessionId)
                .setParameter("descrList", monitoringParametersList)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<MonitoringStatistics> findAllMonitoringStatisticsByMonitoringTaskDataAndDescriptionInList(TaskData monitoringTaskData, List<String> monitoringParametersList) {
        return entityManager.createQuery("select ms from MonitoringStatistics as ms where ms.taskData = :monitoringTaskData " +
                "and ms.parameterId.description in (:descrList)")
                .setParameter("monitoringTaskData", monitoringTaskData)
                .setParameter("descrList", monitoringParametersList)
                .getResultList();
    }

    private TaskData findMonitoringTaskDataBySessionIdAndParentId(String sessionId, String parentId) {
        return (TaskData) entityManager.createQuery(
                "select td from TaskData as td where td.sessionId=:sessionId and td.taskId=" +
                        "(select pm.monitoringId from PerformedMonitoring as pm where pm.sessionId=:sessionId and pm.parentId=:parentId)")
                .setParameter("sessionId", sessionId)
                .setParameter("parentId", parentId)
                .getSingleResult();
    }

    private List<String> assembleDefaultMonitoringParametersDescriptions(DefaultMonitoringParameters[] defaultMonitoringParametersGroup) {
        List<String> monitoringParametersList = new ArrayList<String>();
        for (DefaultMonitoringParameters defaultMonitoringParameter : defaultMonitoringParametersGroup) {
            monitoringParametersList.add(defaultMonitoringParameter.getDescription());
        }

        return monitoringParametersList;
    }
}
