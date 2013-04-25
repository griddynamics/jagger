package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessDescriptiveStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.webclient.client.WorkloadTaskDataService;
import com.griddynamics.jagger.webclient.client.dto.WorkloadTaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<WorkloadTaskDataDto> getWorkloadTaskData(String sessionId) {
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

            dataDtos.add(dto);
        }
        return dataDtos;
    }
}
