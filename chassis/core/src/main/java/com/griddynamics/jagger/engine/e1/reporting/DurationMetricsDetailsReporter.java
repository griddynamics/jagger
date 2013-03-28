package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DurationMetricLatencyPercentile;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DurationMetricStatistics;
import com.griddynamics.jagger.reporting.AbstractMappedReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Pulyaev
 * Date: 25.10.12
 */
public class DurationMetricsDetailsReporter extends AbstractMappedReportProvider<String> {

    private static final Logger log = LoggerFactory.getLogger(DurationMetricsDetailsReporter.class);

    public static class WorkloadProcessDetailsDTO {
        private String key;
        private String value;

        public WorkloadProcessDetailsDTO(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public JRDataSource getDataSource(String key) {
        String[] parts = key.split(":");
        String taskId = parts[0];
        String metricName = parts[1];

        @SuppressWarnings("unchecked")
        List<DurationMetricStatistics> statistics = getHibernateTemplate().find(
                "select s from DurationMetricStatistics s where s.taskData.taskId=? and s.taskData.sessionId=? and name=?",
                taskId, getSessionIdProvider().getSessionId(), metricName);

        if(statistics == null || statistics.isEmpty()) {
            log.error("Data for process [" + key + "] not found");
            return null;
        }
        if(statistics.size() > 1) {
            log.warn("More than one statistics was found for process [{}]. Will take the first one.", key);
        }

        List<WorkloadProcessDetailsDTO> result = new ArrayList<WorkloadProcessDetailsDTO>();
        for(DurationMetricLatencyPercentile percentile : statistics.get(0).getPercentiles()) {
            WorkloadProcessDetailsDTO dto = new WorkloadProcessDetailsDTO(
                    String.format("%.0f", percentile.getPercentileKey()) + "% -",
                    String.format("%.3fs", percentile.getPercentileValue() / 1000));
            result.add(dto);
        }

        return new JRBeanCollectionDataSource(result);
    }

}
