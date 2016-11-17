package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.dto.TestInfoDto;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.util.Decision;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by kgribov on 3/17/14.
 */
@Component
public class FetchUtil {
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * @return multi map &lt;test-group id, tests ids&gt;
     */
    public Multimap<Long, Long> getTestGroupIdsByTestIds(Set<Long> taskIds) {

        Multimap<Long, Long> resultMap = HashMultimap.create();

        List<String> sessionIds = getSessionIdsByTaskIds(taskIds);
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
     * @param taskIds    TaskData ids of tests to select pairs
     * @return pairs as (test group id, test id)
     */
    private List<Object[]> getGroupToTaskIdsList(Collection<String> sessionIds, Collection<Long> taskIds) {
        return entityManager.createNativeQuery("SELECT grTaskData.id, mysome.taskDataId FROM" +
                "  (" +
                "    SELECT * FROM TaskData AS td WHERE td.sessionId IN (:sessionIds) " +
                "  ) AS grTaskData JOIN" +
                "  (" +
                "    SELECT td2.sessionId, td2.id AS taskDataId, wd.parentId FROM" +
                "      ( " +
                "         SELECT wd.parentId, wd.sessionId, wd.taskId FROM WorkloadData AS wd WHERE wd.sessionId IN (:sessionIds)" +
                "      ) AS wd JOIN " +
                "      TaskData AS td2" +
                "          ON td2.id IN (:taskIds)" +
                "          AND wd.sessionId = td2.sessionId" +
                "          AND wd.taskId=td2.taskId" +
                "  ) AS mysome " +
                "      ON grTaskData.sessionId = mysome.sessionId AND grTaskData.taskId=mysome.parentId")
                .setParameter("sessionIds", sessionIds)
                .setParameter("taskIds", taskIds)
                .getResultList();
    }

    /**
     * @param taskIds TaskData ids for required sessions
     * @return list of session Ids
     */
    public List<String> getSessionIdsByTaskIds(Set<Long> taskIds) {
        return entityManager.createNativeQuery("SELECT DISTINCT taskData.sessionId FROM TaskData taskData " +
                "WHERE taskData.id IN (:ids)")
                .setParameter("ids", taskIds)
                .getResultList();
    }

    /**
     * Returns test info for specified tests ids
     *
     * @param taskIds - selected test ids
     * @return map &lt;testId, map &lt;sessionId, test info&gt;&gt; of test info
     */
    public Map<Long, Map<String, TestInfoDto>> getTestInfoByTaskIds(Set<Long> taskIds) throws RuntimeException {

        if (taskIds.isEmpty()) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("all")
        List<Object[]> objectsList = (List<Object[]>) entityManager.createNativeQuery(
                "SELECT wtd.sessionId, wtd.clock, wtd.clockValue, wtd.termination, finalTaskData.id," +
                        "finalTaskData.startTime, finalTaskData.endTime, wtd.number, finalTaskData.status " +
                        "FROM WorkloadTaskData AS wtd JOIN " +
                        "(SELECT wd.startTime, wd.endTime, wd.taskId, wd.sessionId, taskData.id, taskData.status FROM WorkloadData " +
                        "AS wd JOIN " +
                        "( SELECT  td.id, td.sessionId, td.taskId, td.status FROM TaskData td WHERE td.id IN (:taskDataIds) " +
                        ") AS taskData " +
                        "ON wd.taskId=taskData.taskId AND wd.sessionId=taskData.sessionId" +
                        ") AS finalTaskData " +
                        "ON wtd.sessionId=finalTaskData.sessionId AND wtd.taskId=finalTaskData.taskId ORDER BY finalTaskData.startTime")
                .setParameter("taskDataIds", taskIds)
                .getResultList();

        Map<Long, Map<String, TestInfoDto>> resultMap = Maps.newLinkedHashMap();

        for (Object[] objects : objectsList) {

            Long taskId = ((BigInteger) objects[4]).longValue();
            String clock = (String) objects[1];
            Integer clockValue = (Integer) objects[2];
            String termination = (String) objects[3];
            String sessionId = (String) objects[0];

            Date startTime = (Date) objects[5];
            Date endTime = (Date) objects[6];


            Integer number = (Integer) objects[7];
            if (number == null) {
                number = 0;
            }
            TaskData.ExecutionStatus executionStatus = TaskData.ExecutionStatus.valueOf((String) objects[8]);
            Decision status = Decision.OK;
            if (TaskData.ExecutionStatus.FAILED.equals(executionStatus)) {
                status = Decision.FATAL;
            }

            if (!resultMap.containsKey(taskId)) {
                resultMap.put(taskId, new HashMap<>());
            }
            TestInfoDto testInfo = new TestInfoDto();
            testInfo.setClock(clock);
            testInfo.setClockValue(clockValue);
            testInfo.setTermination(termination);
            testInfo.setStartTime(startTime);
            testInfo.setEndTime(endTime);
            testInfo.setNumber(number);
            testInfo.setStatus(status);

            resultMap.get(taskId).put(sessionId, testInfo);
        }

        return resultMap;
    }

    /**
     * Returns task data, corresponding to defined pair of taskIs and sessionId
     *
     * @param taskId    - TaskData taskId
     * @param sessionId - session id
     * @return TaskData for selected params
     */
    public TaskData getTaskData(String taskId, String sessionId) {
        return (TaskData) entityManager.createQuery("select t from TaskData t where sessionId=(:sessionId) and taskId=(:taskId)")
                .setParameter("sessionId", sessionId)
                .setParameter("taskId", taskId)
                .getSingleResult();
    }

    /**
     * Returns task data, corresponding to TaskData ids
     *
     * @param ids - TaskData ids
     * @return map &lt;TaskData id, TaskData&gt;
     */
    public Map<Long, TaskData> getTaskData(Collection<Long> ids) {
        List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery("select t from TaskData t where id in (:ids)")
                .setParameter("ids", ids)
                .getResultList();

        Map<Long, TaskData> result = new HashMap<>();
        for (TaskData taskData : taskDataList) {
            result.put(taskData.getId(), taskData);
        }

        return result;
    }
}
