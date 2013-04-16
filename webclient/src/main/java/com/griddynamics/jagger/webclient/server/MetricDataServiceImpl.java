package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.MetricValueDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class MetricDataServiceImpl implements MetricDataService {

    private static final Logger log = LoggerFactory.getLogger(MetricDataServiceImpl.class);
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private HashMap<String, String> standardMetrics = new HashMap<String, String>();

    public MetricDataServiceImpl(){
        standardMetrics.put("Throughput", "throughput");
        standardMetrics.put("Latency", "avgLatency");
        standardMetrics.put("Duration", "totalDuration");
        standardMetrics.put("Success rate", "successRate");
        standardMetrics.put("Iterations", "samples");
    }

    @Override
    public Set<MetricNameDto> getMetricsNames(Set<TaskDataDto> tests) {
        HashSet<MetricNameDto> set = new HashSet<MetricNameDto>();
        for (TaskDataDto taskDataDto : tests){
            for (String standardMetricName : standardMetrics.keySet()){
                MetricNameDto metric = new MetricNameDto();
                metric.setName(standardMetricName);
                metric.setTests(taskDataDto);
                set.add(metric);
            }
            set.addAll(getLatencyMetricsNames(taskDataDto));
            set.addAll(getCustomMetricsNames(taskDataDto));
        }
        return set;
    }


    @Override
    public List<MetricDto> getMetrics(List<MetricNameDto> metricNames) {
        List<MetricDto> result = new ArrayList<MetricDto>(metricNames.size());
        for (MetricNameDto metricName : metricNames){
            result.add(getMetric(metricName));
        }
        return result;
    }

    @Override
    public MetricDto getMetric(MetricNameDto metricName) {

        MetricDto dto = new MetricDto();
        dto.setValues(new HashSet<MetricValueDto>());
        dto.setMetricName(metricName);

        if (standardMetrics.containsKey(metricName.getName())){
            //it is a standard metric
            List<Object[]> result = entityManager.createNativeQuery("SELECT " +
                                                                    "    workload."+ standardMetrics.get(metricName.getName())+", taskData.id, taskData.sessionId " +
                                                                    "FROM " +
                                                                    "    WorkloadTaskData workload " +
                                                                    "        left outer join " +
                                                                    "    (select " +
                                                                    "        id, taskId, sessionId " +
                                                                    "    from " +
                                                                    "        TaskData) as taskData ON taskData.taskId = workload.taskId " +
                                                                    "        and taskData.sessionId = workload.sessionId " +
                                                                    "WHERE " +
                                                                    "    taskData.id in (:ids)").setParameter("ids", metricName.getTests().getIds()).getResultList();

            for (Object[] temp : result){
                String metricValue = temp[0].toString();
                long testId =  ((BigInteger)temp[1]).longValue();
                long sessionId = Long.parseLong(temp[2].toString());

                MetricValueDto value = new MetricValueDto();
                value.setTestId(testId);
                value.setValue(metricValue);
                value.setSessionId(sessionId);

                dto.getValues().add(value);
            }
        }else{
            if (metricName.getName().matches("Latency .+ %")){
                //it is a latency metric
                Double latencyKey = Double.parseDouble(metricName.getName().split(" ")[1]);
                List<Object[]> latency = entityManager.createQuery(
                        "select s.percentileValue, s.workloadProcessDescriptiveStatistics.taskData.id, s.workloadProcessDescriptiveStatistics.taskData.sessionId from  WorkloadProcessLatencyPercentile as s " +
                                "where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                                "      and s.percentileKey=:latencyKey ")
                        .setParameter("taskIds", metricName.getTests().getIds())
                        .setParameter("latencyKey", latencyKey)
                        .getResultList();
                for (Object[] temp : latency){
                    MetricValueDto value = new MetricValueDto();
                    value.setValue(temp[0].toString());
                    value.setTestId(Long.parseLong(temp[1].toString()));
                    value.setSessionId(Long.parseLong(temp[2].toString()));
                    dto.getValues().add(value);
                }
            }else{
                //custom metric

            }
        }

        return dto;
    }

    public Set<MetricNameDto> getCustomMetricsNames(TaskDataDto tests){
        return Collections.EMPTY_SET;
    }

    public Set<MetricNameDto> getLatencyMetricsNames(TaskDataDto tests){
        Set<MetricNameDto> latencyNames = Collections.EMPTY_SET;
        try {
            List<WorkloadProcessLatencyPercentile> latency = entityManager.createQuery(
                    "select s from  WorkloadProcessLatencyPercentile as s where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                            "group by s.percentileKey " +
                            "having count(s.id)=:size")
                    .setParameter("taskIds", tests.getIds())
                    .setParameter("size", (long)tests.getIds().size())
                    .getResultList();

            latencyNames = new HashSet<MetricNameDto>(latency.size());
            if (!latency.isEmpty()){
                for(WorkloadProcessLatencyPercentile percentile : latency) {
                    MetricNameDto dto = new MetricNameDto();
                    dto.setName("Latency "+Double.toString(percentile.getPercentileKey())+" %");
                    dto.setTests(tests);
                    latencyNames.add(dto);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return latencyNames;
    }
}