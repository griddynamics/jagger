package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DiagnosticResultEntity;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.ValidationResultEntity;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.util.TimeUtils;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.MetricValueDto;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.DecimalFormat;
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

    private HashMap<String, Pair<String, String>> standardMetrics = new HashMap<String, Pair<String, String>>();

    public MetricDataServiceImpl(){
        standardMetrics.put("Throughput", Pair.of("throughput", "Throughput, tps"));
        standardMetrics.put("Latency", Pair.of("avgLatency", "Latency, sec"));
        standardMetrics.put("Duration", Pair.of("duration", "Duration, sec"));
        standardMetrics.put("Success rate", Pair.of("successRate", "Success rate"));
        standardMetrics.put("Iterations", Pair.of("samples", "Iterations, samples"));
    }

    @Override
    public Set<MetricNameDto> getMetricsNames(Set<TaskDataDto> tests) {
        Long time = System.currentTimeMillis();
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
        log.info("For tasks {} was found {} metrics names for {} ms", new Object[]{tests, set.size(), System.currentTimeMillis() - time});
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
        Long time = System.currentTimeMillis();
        MetricDto dto = new MetricDto();
        dto.setValues(new HashSet<MetricValueDto>());
        dto.setMetricName(metricName);

        if ("Duration".equals(metricName.getName())) {
            List<Object[]> result = entityManager.createNativeQuery("select workload.sessionId, workload.endTime, workload.startTime " +
                    "from WorkloadData as workload inner join (select taskId, sessionId from TaskData where id in (:ids)) as taskData "+
            "on workload.taskId=taskData.taskId and " +
                    "workload.sessionId=taskData.sessionId ")
            .setParameter("ids", metricName.getTests().getIds()).getResultList();

            for (Object [] entry : result) {
                MetricValueDto value = new MetricValueDto();
                Date [] date = new Date[2];
                date[0] = (Date)entry [1];
                date[1] = (Date)entry [2];
                value.setValueRepresentation(TimeUtils.formatDuration(date[0].getTime() - date[1].getTime()));
                value.setValue(String.valueOf( (date[0].getTime() - date[1].getTime()) / 1000));
                value.setSessionId(Long.parseLong(String.valueOf(entry[0])));

                dto.getValues().add(value);
            }
        }
        else if (standardMetrics.containsKey(metricName.getName())){
            //it is a standard metric
            List<Object[]> result = entityManager.createNativeQuery("select workload."+standardMetrics.get(metricName.getName()).getFirst()+", workload.sessionId "+
                                                                    "from WorkloadTaskData as workload " +
                                                                    "inner join (select taskId, sessionId from TaskData where id in (:ids)) as taskData "+
                                                                    "on workload.taskId=taskData.taskId and " +
                                                                       "workload.sessionId=taskData.sessionId ")
                                                                    .setParameter("ids", metricName.getTests().getIds()).getResultList();

            for (Object[] temp : result){
                String metricValue = temp[0].toString();
                long sessionId = Long.parseLong(temp[1].toString());

                MetricValueDto value = new MetricValueDto();
                value.setValue(metricValue);
                value.setSessionId(sessionId);

                dto.getValues().add(value);
            }
        }else{
            if (metricName.getName().matches("Latency .+ %")){
                //it is a latency metric
                Double latencyKey = Double.parseDouble(metricName.getName().split(" ")[1]);
                List<Object[]> latency = entityManager.createQuery("select s.percentileValue, s.workloadProcessDescriptiveStatistics.taskData.id, s.workloadProcessDescriptiveStatistics.taskData.sessionId " +
                                                                        "from  WorkloadProcessLatencyPercentile as s " +
                                                                   "where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                                                                          "and s.percentileKey=:latencyKey ")
                        .setParameter("taskIds", metricName.getTests().getIds())
                        .setParameter("latencyKey", latencyKey)
                        .getResultList();
                for (Object[] temp : latency){
                    MetricValueDto value = new MetricValueDto();
                    value.setValue(String.format("%.3f", (Double)temp[0] / 1000));
                    value.setTestId(Long.parseLong(temp[1].toString()));
                    value.setSessionId(Long.parseLong(temp[2].toString()));
                    dto.getValues().add(value);
                }
            }else{
                //custom metric
                List<Object[]> metrics = entityManager.createQuery("select metric, metric.workloadData.sessionId " +
                                                                   "from DiagnosticResultEntity as metric " +
                                                                   "where metric.name=:name " +
                                                                            "and (metric.workloadData.taskId, metric.workloadData.sessionId) " +
                                                                                "in (select taskData.taskId, taskData.sessionId from TaskData as taskData where taskData.id in (:ids))")
                                                                   .setParameter("ids", metricName.getTests().getIds()).setParameter("name", metricName.getName()).getResultList();

                if (!metrics.isEmpty()){
                    for (Object[] mas : metrics){

                        DiagnosticResultEntity metric = (DiagnosticResultEntity)mas[0];
                        if (metric.getTotal() == null) continue;

                        MetricValueDto value = new MetricValueDto();
                        value.setValue(new DecimalFormat("0.0###").format(metric.getTotal()));

                        value.setSessionId(Long.parseLong(mas[1].toString()));
                        dto.getValues().add(value);
                    }
                }else{
                    List<Object[]> validators = entityManager.createQuery("select metric, metric.workloadData.sessionId " +
                                                                                "from ValidationResultEntity as metric " +
                                                                          "where metric.validator=:name " +
                                                                                   "and (metric.workloadData.taskId, metric.workloadData.sessionId) " +
                                                                                        "in (select taskData.taskId, taskData.sessionId from TaskData as taskData where taskData.id in (:ids))").setParameter("ids", metricName.getTests().getIds()).setParameter("name", metricName.getName()).getResultList();
                    for (Object[] mas : validators){

                        ValidationResultEntity validator = (ValidationResultEntity)mas[0];
                        if (validator.getTotal() == null || validator.getFailed() == null) continue;
                        MetricValueDto value = new MetricValueDto();

                        BigDecimal percentage = BigDecimal.ZERO;

                        if (validator.getTotal() != 0) {
                            percentage = new BigDecimal(validator.getTotal() - validator.getFailed())
                                    .divide(new BigDecimal(validator.getTotal()), 3, BigDecimal.ROUND_HALF_UP);
                        }

                        value.setValue(percentage.toString());

                        value.setSessionId(Long.parseLong(mas[1].toString()));
                        dto.getValues().add(value);
                    }
                }
            }
        }
        dto.setPlotSeriesDtos(generatePlotSeriesDto(dto));

        log.info("For metric name {} was found metric value for {} ms", new Object[]{metricName, System.currentTimeMillis() - time});
        return dto;
    }


    private PlotSeriesDto generatePlotSeriesDto(MetricDto metricDto) {
        double yMinimum = Double.MAX_VALUE;

        //So plot draws as {(0, val0),(1, val1), (2, val2), ... (n, valn)}
        int iter = 0;
        List<PointDto> list = new ArrayList<PointDto>();

        List<MetricValueDto> metricList = new ArrayList<MetricValueDto>();
        for(MetricValueDto value :metricDto.getValues()) {
            metricList.add(value);
        }

        Collections.sort(metricList, new Comparator<MetricValueDto> () {

            @Override
            public int compare(MetricValueDto o1, MetricValueDto o2) {
                if (o2.getSessionId() < o1.getSessionId()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        for (MetricValueDto value: metricList) {
            double temp = Double.parseDouble(value.getValue());
            list.add(new PointDto(value.getSessionId(), temp));
            if (yMinimum == Double.MAX_VALUE || temp < yMinimum)
                yMinimum = temp;
        }

        String legend = metricDto.getMetricName().getName();
        if (standardMetrics.containsKey(legend)) {
            legend = standardMetrics.get(legend).getSecond();
        }
        PlotDatasetDto pdd = new PlotDatasetDto(
                list,
                legend,
                ColorCodeGenerator.getHexColorCode()
        );

        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(metricDto.getMetricName().getTests().getTaskName()).
                append(", ").
                append(metricDto.getMetricName().getName());

        PlotSeriesDto psd = new PlotSeriesDto(
                Arrays.asList(pdd),
                "Sessions" ,
                metricDto.getMetricName().getName(),
                headerBuilder.toString()
        );

        psd.setYAxisMin(yMinimum);

        return psd;
    }

    public Set<MetricNameDto> getCustomMetricsNames(TaskDataDto tests){
        Set<MetricNameDto> metrics;

        List<String> metricNames = entityManager.createNativeQuery("select metric.name from DiagnosticResultEntity as metric " +
                                                                       "where metric.workloadData_id in " +
                                                                       "(select workloadData.id from WorkloadData as workloadData " +
                                                                        "inner join (select id, taskId, sessionId from TaskData where id in (:ids)) as taskData on " +
                                                                        "workloadData.taskId=taskData.taskId and workloadData.sessionId=taskData.sessionId)")
                                                                       .setParameter("ids", tests.getIds()).getResultList();

        List<String> validatorNames = entityManager.createNativeQuery("select metric.validator from ValidationResultEntity as metric " +
                                                                        "where metric.workloadData_id in " +
                                                                        "(select workloadData.id from WorkloadData as workloadData " +
                                                                        "inner join (select id, taskId, sessionId from TaskData where id in (:ids)) as taskData on " +
                                                                        "workloadData.taskId=taskData.taskId and workloadData.sessionId=taskData.sessionId)")
                                                                        .setParameter("ids", tests.getIds()).getResultList();
        metrics = new HashSet<MetricNameDto>(metricNames.size()+validatorNames.size());

        for (String name : metricNames){
            if (name == null) continue;

            MetricNameDto metric = new MetricNameDto();
            metric.setTests(tests);
            metric.setName(name);

            metrics.add(metric);
        }

        for (String name : validatorNames){
            if (name == null) continue;

            MetricNameDto validator = new MetricNameDto();
            validator.setTests(tests);
            validator.setName(name);

            metrics.add(validator);
        }

        return metrics;
    }

    public Set<MetricNameDto> getLatencyMetricsNames(TaskDataDto tests){
        Set<MetricNameDto> latencyNames;

        List<WorkloadProcessLatencyPercentile> latency = entityManager.createQuery(
                "select s from  WorkloadProcessLatencyPercentile as s where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                        "group by s.percentileKey " +
                        "having count(s.id)=:size")
                .setParameter("taskIds", tests.getIds())
                .setParameter("size", (long) tests.getIds().size())
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
        return latencyNames;
    }
}