package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by kgribov on 3/17/14.
 */
public class FetchUtil {
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * @return multi map <test-group id, tests ids>
     */
    public Multimap<Long, Long> getTestsInTestGroup(Set<Long> taskIds){

        Multimap<Long, Long> resultMap = HashMultimap.create();

        List<String> sessionIds = getSessionIds(taskIds);
        if (sessionIds.isEmpty()) {
            // return empty Map, as we did not find any data we wanted
            return resultMap;
        }

        List<Object[]> groupToTaskList = getGroupToTaskIdsList(sessionIds, taskIds);

        for (Object[] objects : groupToTaskList) {
            Long groupId = ((Number) objects[0]).longValue();
            Long taskId = ((Number) objects[1]).longValue();
            resultMap.put(groupId, taskId);
        }
        return resultMap;
    }

    /**
     * @param sessionIds session ids of sessions to select pairs
     * @param taskIds TaskData ids of tests to select pairs
     * @return pairs as (test group id, test id)
     */
    private List<Object[]> getGroupToTaskIdsList (Collection<String> sessionIds, Collection<Long> taskIds) {
        return entityManager.createNativeQuery("select grTaskData.id, some.taskDataId from" +
                "  (" +
                "    select * from TaskData as td where td.sessionId in (:sessionIds) " +
                "  ) as grTaskData join" +
                "  (" +
                "    select td2.sessionId, td2.id as taskDataId, wd.parentId from" +
                "      ( " +
                "         select wd.parentId, wd.sessionId, wd.taskId from WorkloadData as wd where wd.sessionId in (:sessionIds)" +
                "      ) as wd join " +
                "      TaskData as td2" +
                "          on td2.id in (:taskIds)" +
                "          and wd.sessionId = td2.sessionId" +
                "          and wd.taskId=td2.taskId" +
                "  ) as some " +
                "      on grTaskData.sessionId = some.sessionId and grTaskData.taskId=some.parentId")
                .setParameter("sessionIds", sessionIds)
                .setParameter("taskIds", taskIds)
                .getResultList();
    }

    private List<String> getSessionIds(Set<Long> taskIds) {
        return entityManager.createNativeQuery("select distinct taskData.sessionId from TaskData taskData " +
                "where taskData.id in (:ids)")
                .setParameter("ids", taskIds)
                .getResultList();
    }

    public List<Object[]> getMetricNames(Set<Long> taskIds){
        if (taskIds.isEmpty()){
            return Collections.EMPTY_LIST;
        }
        return entityManager.createQuery(
                "select mse.metricDescription.metricId, mse.metricDescription.displayName, mse.metricDescription.taskData.id " +
                        "from MetricSummaryEntity as mse where mse.metricDescription.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .getResultList();
    }
}
