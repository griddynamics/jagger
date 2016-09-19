package com.griddynamics.jagger.data_migaration;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.entity.MetricDescriptionEntity;
import com.griddynamics.jagger.dbapi.entity.MetricPointEntity;
import com.griddynamics.jagger.dbapi.entity.MetricSummaryEntity;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.griddynamics.jagger.dbapi.util.SessionMatchingSetup.MatchBy.ALL;
import static java.lang.Math.round;
import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

/**
 * Created by aantonenko on 9/19/16.
 */


@Service
public class DataMigrationService extends HibernateDaoSupport {

    private DatabaseService databaseService;

    private DataService dataService;

    public DataMigrationService() {
        this.dataService = new DefaultDataService(databaseService);
    }

    public void migrateOldData() {
        List<SessionDataDto> sessions = databaseService.getSessionInfoService().getBySessionIds(0, Integer.MAX_VALUE, emptySet());
        Set<String> sessionIds = sessions.stream().map(s -> s.getId().toString()).collect(toSet());

        List<TaskDataDto> allTaskData = databaseService.getTaskDataForSessions(sessionIds, new SessionMatchingSetup(false, newHashSet(ALL)));
        List<Long> taskIds = allTaskData.stream().map(TaskDataDto::getId).collect(toList());

        Set<MetricNameDto> metricNames = taskIds.stream().flatMap(taskId -> dataService.getMetrics(taskId).stream())
                .map(MetricEntity::getMetricNameDto)
                .collect(toSet());

        Map<MetricNameDto, SummarySingleDto> summaryByMetricName = databaseService.getSummaryByMetricNameDto(metricNames, false);
        Map<MetricNameDto, List<PlotSingleDto>> plotDataByMetricNameDto = databaseService.getPlotDataByMetricNameDto(metricNames);

        Map<MetricNameDto, MetricDescriptionEntity> metricDescriptions = metricNames.stream()
                .collect(toMap(identity(), this::mapToMetricDescription));

        Set<MetricPointEntity> metricPointEntities = plotDataByMetricNameDto.entrySet().stream()
                .map(entry -> mapToMetricPoints(entry.getKey(), entry.getValue(), metricDescriptions))
                .flatMap(Collection::stream)
                .collect(toSet());

        Set<MetricSummaryEntity> metricSummaryEntities = summaryByMetricName.entrySet().stream()
                .map(entry -> mapToSummaryEntities(entry.getKey(), entry.getValue(), metricDescriptions))
                .flatMap(Collection::stream)
                .collect(toSet());

        persistMetricDescriptions(metricDescriptions.values());
        persistMetricPoints(metricPointEntities);
        persistMetricSummaries(metricSummaryEntities);
    }

    private MetricDescriptionEntity mapToMetricDescription(MetricNameDto metricNameDto) {
        MetricDescriptionEntity descriptionEntity = new MetricDescriptionEntity();
        descriptionEntity.setDisplayName(metricNameDto.getMetricDisplayName());
        descriptionEntity.setMetricId(metricNameDto.getMetricName());
        descriptionEntity.setTaskData(fetchTaskData(metricNameDto.getTest()));
        return descriptionEntity;
    }

    private TaskData fetchTaskData(TaskDataDto taskDataDto) {
        return databaseService.getTaskData(Long.toString(taskDataDto.getId()), taskDataDto.getSessionId());
    }

    private Set<MetricPointEntity> mapToMetricPoints(MetricNameDto metricNameDto, List<PlotSingleDto> plotSingleDtos, Map<MetricNameDto, MetricDescriptionEntity> metricDescriptions) {
        return plotSingleDtos.stream()
                .flatMap(plotSingleDto -> plotSingleDto.getPlotData().stream())
                .map(pointDto -> new MetricPointEntity(round(pointDto.getX()), pointDto.getY(), metricDescriptions.get(metricNameDto)))
                .collect(toSet());
    }

    private Set<MetricSummaryEntity> mapToSummaryEntities(MetricNameDto metricNameDto, SummarySingleDto summarySingleDto, Map<MetricNameDto, MetricDescriptionEntity> metricDescriptions) {
        return summarySingleDto.getValues().stream().map(metricValueDto -> {
            MetricSummaryEntity metricSummaryEntity = new MetricSummaryEntity();
            metricSummaryEntity.setMetricDescription(metricDescriptions.get(metricNameDto));
            metricSummaryEntity.setTotal(Double.valueOf(metricValueDto.getValue()));
            return metricSummaryEntity;
        }).collect(toSet());
    }

    private void persistMetricDescriptions(Collection<MetricDescriptionEntity> metricDescriptions) {
        metricDescriptions.forEach(desc -> getHibernateTemplate().persist(desc));
    }

    private void persistMetricPoints(Set<MetricPointEntity> metricPointEntities) {
        metricPointEntities.forEach(point -> getHibernateTemplate().persist(point));
    }

    private void persistMetricSummaries(Set<MetricSummaryEntity> metricSummaryEntities) {
        metricSummaryEntities.forEach(summary -> getHibernateTemplate().persist(summary));
    }

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
}
