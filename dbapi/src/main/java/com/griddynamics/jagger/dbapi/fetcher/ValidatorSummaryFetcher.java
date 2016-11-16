package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ValidatorSummaryFetcher extends DbMetricDataFetcher<SummarySingleDto> {
    @Override
    protected Set<SummarySingleDto> fetchData(List<MetricNameDto> metricNames) {

        if (metricNames.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Long> taskIds = new HashSet<>();
        Set<String> metricIds = new HashSet<>();
        for (MetricNameDto metricName : metricNames) {
            taskIds.addAll(metricName.getTaskIds());
            metricIds.add(metricName.getMetricName());
        }

        List<Object[]> validators = entityManager.createNativeQuery(
                "SELECT vr.validator, vr.total, vr.failed, selected.sessionId, selected.id FROM ValidationResultEntity vr JOIN (" +
                        "SELECT wd.id AS wid, wd.sessionId, selected.id FROM WorkloadData wd JOIN (" +
                        "     SELECT td.taskId, td.sessionId, td.id FROM TaskData td WHERE td.id IN (:ids)" +
                        ") AS selected ON wd.sessionId=selected.sessionId AND wd.taskId=selected.taskId" +
                        ") AS selected ON vr.workloadData_id=selected.wid AND vr.validator IN (:names);")
                .setParameter("ids", taskIds)
                .setParameter("names", metricIds)
                .getResultList();

        if (validators.isEmpty()) {
            log.warn("Could not find data for {}", metricNames);
            return Collections.emptySet();
        }

        Map<Long, Map<String, MetricNameDto>> mappedMetricDtos = MetricNameUtil.getMappedMetricDtos(metricNames);
        Map<MetricNameDto, SummarySingleDto> resultMap = new HashMap<>();

        for (Object[] mas : validators) {

            BigInteger taskId = (BigInteger) mas[4];
            String metricId = (String) mas[0];
            MetricNameDto metricNameDto;
            try {
                metricNameDto = mappedMetricDtos.get(taskId.longValue()).get(metricId);
                if (metricNameDto == null) {   // means that we fetched data that we had not wanted to fetch
                    continue;
                }
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("could not find appropriate MetricDto : " + taskId);
            }

            if (!resultMap.containsKey(metricNameDto)) {
                SummarySingleDto metricDto = new SummarySingleDto();
                metricDto.setMetricName(metricNameDto);
                metricDto.setValues(new HashSet<>());
                resultMap.put(metricNameDto, metricDto);
            }

            SummarySingleDto metricDto = resultMap.get(metricNameDto);

            Integer total = (Integer) mas[1];
            Integer failed = (Integer) mas[2];

            if (total == null || failed == null) continue;
            SummaryMetricValueDto value = new SummaryMetricValueDto();

            BigDecimal percentage = BigDecimal.ZERO;

            if (total != 0) {
                percentage = new BigDecimal(total - failed)
                        .divide(new BigDecimal(total), 3, BigDecimal.ROUND_HALF_UP);
            }
            value.setValue(percentage.toString());
            value.setSessionId(Long.parseLong((String) mas[3]));

            metricDto.getValues().add(value);
        }

        return new HashSet<>(resultMap.values());
    }
}
