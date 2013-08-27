package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessDescriptiveStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.util.TimeUtils;
import com.griddynamics.jagger.webclient.client.WorkloadTaskDataService;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 18.03.13
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public class WorkloadTaskDataServiceImpl implements WorkloadTaskDataService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadTaskDataServiceImpl.class);
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Deprecated
    @Override
    public List<WorkloadTaskDataDto> getWorkloadTaskData(String sessionId) {
        Long time = System.currentTimeMillis();

        List<WorkloadTaskData> datas = entityManager.createQuery("select workloadTaskData from WorkloadTaskData as workloadTaskData where workloadTaskData.sessionId=:sessionId").
                                                                                setParameter("sessionId", sessionId).getResultList();
        List<WorkloadTaskDataDto> dataDtos = new ArrayList<WorkloadTaskDataDto>(datas.size());
        for (WorkloadTaskData data : datas){
            WorkloadTaskDataDto dto = new WorkloadTaskDataDto();

            dto.setSessionId(data.getSessionId());
            dto.setName(data.getScenario().getName());
            dto.setComment(data.getScenario().getComment());
            dto.setVersion(data.getScenario().getVersion());
            dto.setTaskId(data.getTaskId());

            //TODO - rebuild to join query
            List<WorkloadProcessDescriptiveStatistics> latency = entityManager.createQuery(
                    "select s from WorkloadProcessDescriptiveStatistics as s where s.taskData.taskId=:taskId and s.taskData.sessionId=:sessionId")
                                                            .setParameter("taskId", data.getTaskId()).setParameter("sessionId", sessionId).getResultList();

            List<String> latencyValues = new ArrayList<String>(latency.size());
            if (!latency.isEmpty()){
                for(WorkloadProcessLatencyPercentile percentile : latency.get(0).getPercentiles()) {
                    latencyValues.add(String.format("%.0f", percentile.getPercentileKey()) + "% -"+
                            String.format("%.3fs", percentile.getPercentileValue() / 1000));

                }
            }

            dto.setLatency(latencyValues);
            dto.setNumber(data.getNumber());
            dto.setSamples(data.getSamples());
            dto.setClock(data.getClock());
            dto.setClockValue(data.getClockValue());
            dto.setTermination(data.getTermination());
            dto.setKernels(data.getKernels());
            dto.setTotalDuration(data.getTotalDuration());
            dto.setThroughput(data.getThroughput());
            dto.setFailuresCount(data.getFailuresCount());
            dto.setSuccessRate(data.getSuccessRate());
            dto.setAvgLatency(data.getAvgLatency());
            dto.setStdDevLatency(data.getStdDevLatency());
            dto.setDuration(getDuration(data));

            dataDtos.add(dto);
        }

        log.info("For session {} was loaded {} workloadTasks for {} ms", new Object[]{sessionId, dataDtos.size(), System.currentTimeMillis() - time});
        return dataDtos;
    }

    @Override
    public WorkloadTaskDataDto getWorkloadTaskData(String sessionId, long taskId) {
        BigInteger workloadId = (BigInteger)entityManager.createNativeQuery("select " +
                                                                                "workloadTaskData.id from WorkloadTaskData as workloadTaskData " +
                                                                            "left outer join  " +
                                                                                "TaskData as taskData " +
                                                                            "on " +
                                                                                "workloadTaskData.taskId=taskData.taskId and " +
                                                                                "workloadTaskData.sessionId=taskData.sessionId "+
                                                                            "where " +
                                                                                "workloadTaskData.sessionId=:sessionId and " +
                                                                                "taskData.id=:taskId")
                                                                            .setParameter("sessionId", sessionId)
                                                                            .setParameter("taskId", taskId)
                                                                            .getSingleResult();

        List<WorkloadTaskData> datas = entityManager.createQuery("select workloadTaskData from WorkloadTaskData as workloadTaskData where workloadTaskData.id=:id").
                setParameter("id", workloadId.longValue()).getResultList();
            WorkloadTaskData data = datas.iterator().next();
            WorkloadTaskDataDto dto = new WorkloadTaskDataDto();

            dto.setSessionId(data.getSessionId());
            dto.setName(data.getScenario().getName());
            dto.setComment(data.getScenario().getComment());
            dto.setVersion(data.getScenario().getVersion());
            dto.setTaskId(data.getTaskId());

            //TODO - rebuild to join query
            List<WorkloadProcessDescriptiveStatistics> latency = entityManager.createQuery(
                    "select s from WorkloadProcessDescriptiveStatistics as s where s.taskData.taskId=:taskId and s.taskData.sessionId=:sessionId")
                    .setParameter("taskId", data.getTaskId()).setParameter("sessionId", sessionId).getResultList();

            List<String> latencyValues = new ArrayList<String>(latency.size());
            if (!latency.isEmpty()){
                for(WorkloadProcessLatencyPercentile percentile : latency.get(0).getPercentiles()) {
                    latencyValues.add(String.format("%.0f", percentile.getPercentileKey()) + "% -"+
                            String.format("%.3fs", percentile.getPercentileValue() / 1000));

                }
            }

            dto.setLatency(latencyValues);
            dto.setNumber(data.getNumber());
            dto.setSamples(data.getSamples());
            dto.setClock(data.getClock());
            dto.setClockValue(data.getClockValue());
            dto.setTermination(data.getTermination());
            dto.setKernels(data.getKernels());
            dto.setTotalDuration(data.getTotalDuration());
            dto.setThroughput(data.getThroughput());
            dto.setFailuresCount(data.getFailuresCount());
            dto.setSuccessRate(data.getSuccessRate());
            dto.setAvgLatency(data.getAvgLatency());
            dto.setStdDevLatency(data.getStdDevLatency());
            dto.setDuration(getDuration(data));
        return dto;
    }

    @Override
    public Set<WorkloadTaskDataDto> getWorkloadTaskData(Set<TaskDataDto> tests) {
        Long time = System.currentTimeMillis();
        Set<WorkloadTaskDataDto> result = new HashSet<WorkloadTaskDataDto>(tests.size());

        List<Long> ids = new ArrayList<Long>(tests.size());

        for (TaskDataDto taskDataDto : tests){
            ids.add(taskDataDto.getId());
        }

        if (ids.isEmpty()){
            return Collections.EMPTY_SET;
        }

        List<WorkloadTaskData> workloadTaskDatas = entityManager.createNativeQuery("select * " +
                                                                                    "from WorkloadTaskData workloadTaskData " +
                                                                                    "inner join  " +
                                                                                        "(select taskId, sessionId from TaskData where id in (:taskIdList)) taskData " +
                                                                                    "on " +
                                                                                        "workloadTaskData.taskId=taskData.taskId and " +
                                                                                        "workloadTaskData.sessionId=taskData.sessionId", WorkloadTaskData.class)
                                                                                    .setParameter("taskIdList", ids)
                                                                                    .getResultList();

        for (WorkloadTaskData data : workloadTaskDatas){
            WorkloadTaskDataDto dto = new WorkloadTaskDataDto();

            dto.setSessionId(data.getSessionId());
            dto.setName(data.getScenario().getName());
            dto.setComment(data.getScenario().getComment());
            dto.setVersion(data.getScenario().getVersion());
            dto.setTaskId(data.getTaskId());
            dto.setNumber(data.getNumber());
            dto.setSamples(data.getSamples());
            dto.setClock(data.getClock());
            dto.setClockValue(data.getClockValue());
            dto.setTermination(data.getTermination());
            dto.setKernels(data.getKernels());
            dto.setTotalDuration(data.getTotalDuration());
            dto.setThroughput(data.getThroughput());
            dto.setFailuresCount(data.getFailuresCount());
            dto.setSuccessRate(data.getSuccessRate());
            dto.setAvgLatency(data.getAvgLatency());
            dto.setStdDevLatency(data.getStdDevLatency());
            dto.setDuration(getDuration(data));

            List<WorkloadProcessDescriptiveStatistics> latency = entityManager.createNativeQuery("select * " +
                                                                                                 "from WorkloadProcessDescriptiveStatistics s " +
                                                                                                 "inner join " +
                                                                                                    "(select id from TaskData where sessionId=:sessionId and taskId=:taskId) taskData " +
                                                                                                 "on " +
                                                                                                    "s.taskData_id=taskData.id", WorkloadProcessDescriptiveStatistics.class)
                                                                                                 .setParameter("taskId", dto.getTaskId())
                                                                                                 .setParameter("sessionId", dto.getSessionId())
                                                                                                 .getResultList();

            List<String> latencyValues = new ArrayList<String>();
            if (!latency.isEmpty()){
                for(WorkloadProcessLatencyPercentile percentile : latency.get(0).getPercentiles()) {
                    latencyValues.add(String.format("%.0f", percentile.getPercentileKey()) + "% - " +
                            String.format("%.3fs", percentile.getPercentileValue() / 1000));

                }
            }
            Collections.sort(latencyValues, Collections.reverseOrder());
            dto.setLatency(latencyValues);

            //custom metric
            List<Object[]> metrics = entityManager.createNativeQuery("select metric.name, metric.total " +
                    "from DiagnosticResultEntity as metric where metric.workloadData_id=:id")
                    .setParameter("id", data.getId().toString()).getResultList();


            if (!metrics.isEmpty()) {
                Map<String, String> metricsMap = new TreeMap<String, String>();
                for (Object[] objects : metrics) {
                    metricsMap.put(objects[0].toString(), objects[1].toString());
                }
                dto.setCustomMetrics(metricsMap);
            }
            result.add(dto);
        }
        log.info("For tasks ids {} was loaded {} workloadTasks for {} ms", new Object[]{ids, result.size(), System.currentTimeMillis() - time});
        return result;
    }

    private Date[] getStartEndDates(WorkloadTaskData workloadTaskData){
        Date[] dates = new Date[2];
        List<Object[]> datesList =  entityManager.createNativeQuery("select task.startTime, task.endTime from WorkloadData as task where task.taskId=:taskId and task.sessionId=:sessionId")
                            .setParameter("taskId", workloadTaskData.getTaskId()).setParameter("sessionId", workloadTaskData.getSessionId()).getResultList();
        if (datesList.size()==1){
            Object[] element = datesList.iterator().next();
            dates[0] = (Date)element[0];
            dates[1] = (Date)element[1];
        }
        return dates;
    }

    private String getDuration(Date[] startEndTime){
        Long startTime = startEndTime[0].getTime();
        Long endTime = startEndTime[1].getTime();
        return TimeUtils.formatDuration(endTime - startTime);
    }

    private String getDuration(WorkloadTaskData workloadTaskData){
        Date[] dates = getStartEndDates(workloadTaskData);
        return getDuration(dates);
    }
}
