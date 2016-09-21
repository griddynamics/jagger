package com.griddynamics.jagger.data_migaration;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.entity.MetricDescriptionEntity;
import com.griddynamics.jagger.dbapi.entity.MetricPointEntity;
import com.griddynamics.jagger.dbapi.entity.MetricSummaryEntity;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import org.apache.commons.collections.MapUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.collect.Sets.newHashSet;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.DURATION;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.LATENCY;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.LATENCY_PERCENTILE;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.METRIC;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.MONITORING;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.SESSION_SCOPE_MONITORING;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.SESSION_SCOPE_TG;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.STANDARD_METRICS;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.TEST_GROUP_METRIC;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.THROUGHPUT;
import static com.griddynamics.jagger.dbapi.dto.MetricNameDto.Origin.VALIDATOR;
import static com.griddynamics.jagger.dbapi.util.SessionMatchingSetup.MatchBy.ALL;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Created by aantonenko on 9/19/16.
 */

@SuppressWarnings("unchecked")
public class DataMigrationService extends HibernateDaoSupport {

    private DatabaseService databaseService;

    private DataService dataService;

    public void migrateOldData() {
        // Following fetches are needed for obtaining all metricNames which are used for obtaining summary and plot data
        List<SessionDataDto> sessions = databaseService.getSessionInfoService().getBySessionIds(0, Integer.MAX_VALUE, emptySet());
        Set<String> sessionIds = sessions.stream().map(SessionDataDto::getSessionId).collect(toSet());

        List<TaskDataDto> allTaskData = databaseService.getTaskDataForSessions(sessionIds, new SessionMatchingSetup(false, newHashSet(ALL)));
        Set<Long> taskIds = allTaskData.stream().map(TaskDataDto::getIds).flatMap(Collection::stream).collect(toSet());

        Set<MetricNameDto> metricNames = taskIds.stream().flatMap(taskId -> dataService.getMetrics(taskId).stream())
                .map(MetricEntity::getMetricNameDto)
                .collect(toSet());

        Set<MetricNameDto> metricNamesForSummaries = metricNames.stream().filter(metricNamesForSummariesPredicate()).collect(toSet());
        Set<MetricNameDto> metricNamesForPlots = metricNames.stream().filter(metricNamesForPlotsPredicate()).collect(toSet());

        // Fetch summary and plot data
        Map<MetricNameDto, SummarySingleDto> summaryByMetricName = databaseService.getSummaryByMetricNameDto(metricNamesForSummaries, false);
        Map<MetricNameDto, List<PlotSingleDto>> plotDataByMetricName = databaseService.getPlotDataByMetricNameDto(metricNamesForPlots);

        // Create MetricDescriptionEntities from metricNames and persist them
        Map<MetricNameDto, MetricDescriptionEntity> createdMetricDescriptions = createMetricDescriptions(metricNames);
        persistMetricDescriptions(createdMetricDescriptions.values());

        // This fetch is needed because metric descriptions are linked with plot and summary data by id
        Map<MetricNameDto, MetricDescriptionEntity> persistedMetricDescriptions = fetchMetricDescriptions(metricNames);

        Set<MetricPointEntity> metricPointEntities = createMetricPoints(plotDataByMetricName, persistedMetricDescriptions);
        persistMetricPoints(metricPointEntities);

        Set<MetricSummaryEntity> metricSummaryEntities = createMetricSummaries(summaryByMetricName, persistedMetricDescriptions);
        persistMetricSummaries(metricSummaryEntities);
    }

    private Predicate<MetricNameDto> metricNamesForSummariesPredicate() {
        return name -> name.getOrigin() == STANDARD_METRICS ||
                name.getOrigin() == DURATION ||
                name.getOrigin() == LATENCY_PERCENTILE ||
                name.getOrigin() == METRIC ||
                name.getOrigin() == TEST_GROUP_METRIC ||
                name.getOrigin() == VALIDATOR;
    }

    private Predicate<MetricNameDto> metricNamesForPlotsPredicate() {
        return name -> name.getOrigin() == MONITORING ||
                name.getOrigin() == LATENCY_PERCENTILE ||
                name.getOrigin() == METRIC ||
                name.getOrigin() == LATENCY ||
                name.getOrigin() == THROUGHPUT ||
                name.getOrigin() == SESSION_SCOPE_MONITORING ||
                name.getOrigin() == SESSION_SCOPE_TG ||
                name.getOrigin() == TEST_GROUP_METRIC;
    }

    private Map<MetricNameDto, MetricDescriptionEntity> createMetricDescriptions(Set<MetricNameDto> metricNames) {
        return metricNames.stream().collect(toMap(identity(), this::mapToMetricDescription));
    }

    private Map<MetricNameDto, MetricDescriptionEntity> fetchMetricDescriptions(Set<MetricNameDto> metricNames) {
        return metricNames.stream().collect(toMap(identity(), this::fetchMetricDescription));
    }

    private MetricDescriptionEntity mapToMetricDescription(MetricNameDto metricNameDto) {
        MetricDescriptionEntity descriptionEntity = new MetricDescriptionEntity();
        descriptionEntity.setDisplayName(metricNameDto.getMetricDisplayName());
        descriptionEntity.setMetricId(metricNameDto.getMetricName());
        descriptionEntity.setTaskData(fetchTaskData(metricNameDto.getTest()));
        return descriptionEntity;
    }

    private MetricDescriptionEntity fetchMetricDescription(MetricNameDto metricNameDto) {
        MetricDescriptionEntity descriptionEntity = mapToMetricDescription(metricNameDto);
        List<MetricDescriptionEntity> metricDescriptionEntities = findMetricDescriptionEntities(descriptionEntity);
        if (metricDescriptionEntities.isEmpty()) {
            throw new MetricDescriptionNotFoundException(format("MetricDescriptionEntity [%s] wasn't found!", descriptionEntity.toString()));
        }
        return metricDescriptionEntities.get(0);
    }

    private TaskData fetchTaskData(TaskDataDto taskDataDto) {
        long taskDataDtoId = taskDataDto.getId();
        Map<Long, TaskData> taskData = databaseService.getTaskData(newHashSet(taskDataDtoId));
        if (MapUtils.isEmpty(taskData)) {
            throw new TaskDataNotFoundException(format("Task data with id %s was not found!", taskDataDtoId));
        }
        return taskData.get(taskDataDtoId);
    }

    private Set<MetricPointEntity> createMetricPoints(Map<MetricNameDto, List<PlotSingleDto>> plotDataByMetricNameDto, Map<MetricNameDto, MetricDescriptionEntity> persistedMetricDescriptions) {
        return plotDataByMetricNameDto.entrySet().stream()
                .map(entry -> mapToMetricPoints(entry.getKey(), entry.getValue(), persistedMetricDescriptions))
                .flatMap(Collection::stream)
                .collect(toSet());
    }

    private Set<MetricPointEntity> mapToMetricPoints(MetricNameDto metricNameDto, List<PlotSingleDto> plotSingleDtos, Map<MetricNameDto, MetricDescriptionEntity> metricDescriptions) {
        return plotSingleDtos.stream()
                .flatMap(plotSingleDto -> plotSingleDto.getPlotData().stream())
                .map(pointDto -> new MetricPointEntity(round(pointDto.getX()), pointDto.getY(), metricDescriptions.get(metricNameDto)))
                .collect(toSet());
    }

    private Set<MetricSummaryEntity> createMetricSummaries(Map<MetricNameDto, SummarySingleDto> summaryByMetricName, Map<MetricNameDto, MetricDescriptionEntity> persistedMetricDescriptions) {
        return summaryByMetricName.entrySet().stream()
                .map(entry -> mapToSummaryEntities(entry.getKey(), entry.getValue(), persistedMetricDescriptions))
                .flatMap(Collection::stream)
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

    private List<MetricDescriptionEntity> findMetricDescriptionEntities(MetricDescriptionEntity desc) {
        return (List<MetricDescriptionEntity>) getHibernateTemplate()
                .find("select m from MetricDescriptionEntity m where m.displayName=? and m.metricId=? and m.taskData.id=?",
                        desc.getDisplayName(), desc.getMetricId(), desc.getTaskData().getId());
    }

    private List<MetricPointEntity> findMetricPointEntities(MetricPointEntity point) {
        return (List<MetricPointEntity>) getHibernateTemplate()
                .find("select p from MetricPointEntity p where p.time=? and p.value=? and p.metricDescription.id=?",
                        point.getTime(), point.getValue(), point.getMetricDescription().getId());
    }

    private List<MetricSummaryEntity> findMetricSummaryEntities(MetricSummaryEntity summary) {
        return (List<MetricSummaryEntity>) getHibernateTemplate()
                .find("select s from MetricSummaryEntity s where s.total=? and s.metricDescription.id=?",
                        summary.getTotal(), summary.getMetricDescription().getId());
    }

    private void persistMetricDescriptions(Collection<MetricDescriptionEntity> metricDescriptions) {
        long persisted = metricDescriptions.stream()
                .filter(desc -> isEmpty(findMetricDescriptionEntities(desc)))
                .peek(desc -> getHibernateTemplate().persist(desc))
                .count();
        getHibernateTemplate().flush();
        logger.info(format("%s metric descriptions saved to MetricDescriptionEntity.", persisted));
    }

    private void persistMetricPoints(Collection<MetricPointEntity> metricPointEntities) {
        long persisted = metricPointEntities.stream()
                .filter(point -> isEmpty(findMetricPointEntities(point)))
                .peek(point -> getHibernateTemplate().persist(point))
                .count();
        getHibernateTemplate().flush();
        logger.info(format("%s metric points saved to MetricPointEntity.", persisted));
    }

    private void persistMetricSummaries(Collection<MetricSummaryEntity> metricSummaryEntities) {
        long persisted = metricSummaryEntities.stream()
                .filter(summary -> isEmpty(findMetricSummaryEntities(summary)))
                .peek(summary -> getHibernateTemplate().persist(summary))
                .count();
        getHibernateTemplate().flush();
        logger.info(format("%s metric summaries saved to MetricSummaryEntity.", persisted));
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    private class MetricDescriptionNotFoundException extends RuntimeException {
        MetricDescriptionNotFoundException(String message) {
            super(message);
        }
    }

    private class TaskDataNotFoundException extends RuntimeException {
        TaskDataNotFoundException(String message) {
            super(message);
        }
    }
}
