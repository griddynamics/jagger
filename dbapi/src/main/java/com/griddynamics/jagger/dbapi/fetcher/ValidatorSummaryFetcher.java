package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.util.MetricNameUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Component
public class ValidatorSummaryFetcher extends DbMetricDataFetcher<SummarySingleDto> {
    @Override
    protected Set<SummarySingleDto> fetchData(List<MetricNameDto> metricNames) {

        if (metricNames.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<Long> taskIds = new HashSet<Long>();
        Set<String> metricIds = new HashSet<String>();
        for (MetricNameDto metricName : metricNames) {
            taskIds.addAll(metricName.getTaskIds());
            metricIds.add(metricName.getMetricName());
        }

        List<Object[]> validators = entityManager.createNativeQuery(
                "select vr.validator, vr.total, vr.failed, selected.sessionId, selected.id from ValidationResultEntity vr join (" +
                        "select wd.id as wid, wd.sessionId, selected.id from WorkloadData wd join (" +
                        "     select td.taskId, td.sessionId, td.id from TaskData td where td.id in (:ids)" +
                        ") as selected on wd.sessionId=selected.sessionId and wd.taskId=selected.taskId" +
                        ") as selected on vr.workloadData_id=selected.wid and vr.validator in (:names);")
                .setParameter("ids", taskIds)
                .setParameter("names", metricIds)
                .getResultList();

        if (validators.isEmpty()) {
            log.warn("Could not find data for {}", metricNames);
            return Collections.EMPTY_SET;
        }

        Map<Long, Map<String, MetricNameDto>> mappedMetricDtos = MetricNameUtil.getMappedMetricDtos(metricNames);
        Map<MetricNameDto, SummarySingleDto> resultMap = new HashMap<MetricNameDto, SummarySingleDto>();

        for (Object[] mas : validators){

            BigInteger taskId = (BigInteger)mas[4];
            String metricId = (String)mas[0];
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
                metricDto.setValues(new HashSet<SummaryMetricValueDto>());
                resultMap.put(metricNameDto, metricDto);
            }

            SummarySingleDto metricDto = resultMap.get(metricNameDto);

            Integer total = (Integer)mas[1];
            Integer failed = (Integer)mas[2];

            if (total == null || failed == null) continue;
            SummaryMetricValueDto value = new SummaryMetricValueDto();

            BigDecimal percentage = BigDecimal.ZERO;

            if (total != 0) {
                percentage = new BigDecimal(total - failed)
                        .divide(new BigDecimal(total), 3, BigDecimal.ROUND_HALF_UP);
            }
            value.setValue(percentage.toString());
            value.setSessionId(Long.parseLong((String)mas[3]));

            metricDto.getValues().add(value);
        }

        return new HashSet<SummarySingleDto>(resultMap.values());
    }
}
