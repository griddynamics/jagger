package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.dto.TestInfoDto;
import com.griddynamics.jagger.dbapi.entity.TaskData;
import com.griddynamics.jagger.util.Decision;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

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
    public Multimap<Long, Long> getTestGroupIdsByTestIds(Set<Long> taskIds){

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
     * @param taskIds TaskData ids of tests to select pairs
     * @return pairs as (test group id, test id)
     */
    private List<Object[]> getGroupToTaskIdsList (Collection<String> sessionIds, Collection<Long> taskIds) {
        return entityManager.createNativeQuery("select grTaskData.id, mysome.taskDataId from" +
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
                "  ) as mysome " +
                "      on grTaskData.sessionId = mysome.sessionId and grTaskData.taskId=mysome.parentId")
                .setParameter("sessionIds", sessionIds)
                .setParameter("taskIds", taskIds)
                .getResultList();
    }

    /**
     * @param taskIds TaskData ids for required sessions
     * @return list of session Ids
     */
    public List<String> getSessionIdsByTaskIds(Set<Long> taskIds) {
        return entityManager.createNativeQuery("select distinct taskData.sessionId from TaskData taskData " +
                "where taskData.id in (:ids)")
                .setParameter("ids", taskIds)
                .getResultList();
    }

    /** Returns test info for specified tests ids
     * @param taskIds - selected test ids
     * @return map <testId, map <sessionId, test info>> of test info
     * */
    public Map<Long, Map<String, TestInfoDto>> getTestInfoByTaskIds(Set<Long> taskIds) throws RuntimeException {

        if (taskIds.isEmpty()) {
            return Collections.emptyMap();
        }

        @SuppressWarnings("all")
        List<Object[]> objectsList = (List<Object[]>)entityManager.createNativeQuery(
                "select wtd.sessionId, wtd.clock, wtd.clockValue, wtd.termination, finalTaskData.id," +
                        "finalTaskData.startTime, finalTaskData.endTime, wtd.number, finalTaskData.status " +
                        "from WorkloadTaskData as wtd join " +
                        "(select wd.startTime, wd.endTime, wd.taskId, wd.sessionId, taskData.id, taskData.status from WorkloadData " +
                        "as wd join " +
                        "( select  td.id, td.sessionId, td.taskId, td.status from TaskData td where td.id in (:taskDataIds) " +
                        ") as taskData " +
                        "on wd.taskId=taskData.taskId and wd.sessionId=taskData.sessionId" +
                        ") as finalTaskData " +
                        "on wtd.sessionId=finalTaskData.sessionId and wtd.taskId=finalTaskData.taskId order by finalTaskData.startTime")
                .setParameter("taskDataIds", taskIds)
                .getResultList();

        Map<Long, Map<String, TestInfoDto>> resultMap = Maps.newLinkedHashMap();

        for (Object[] objects : objectsList) {

            Long taskId = ((BigInteger)objects[4]).longValue();
            String clock = objects[1] + " (" + objects[2] + ')';
            String termination = (String)objects[3];
            String sessionId = (String)objects[0];

            Date startTime = (Date)objects[5];
            Date endTime = (Date)objects[6];


            Integer number = (Integer)objects[7];
            if (number == null) {
                number = 0;
            }
            TaskData.ExecutionStatus executionStatus = TaskData.ExecutionStatus.valueOf((String) objects[8]);
            Decision status = Decision.OK;
            if (TaskData.ExecutionStatus.FAILED.equals(executionStatus)) {
                status = Decision.FATAL;
            }

            if (!resultMap.containsKey(taskId)) {
                resultMap.put(taskId,new HashMap<String, TestInfoDto>());
            }
            TestInfoDto testInfo = new TestInfoDto();
            testInfo.setClock(clock);
            testInfo.setTermination(termination);
            testInfo.setStartTime(startTime);
            testInfo.setEndTime(endTime);
            testInfo.setNumber(number);
            testInfo.setStatus(status);

            resultMap.get(taskId).put(sessionId,testInfo);
        }

        return resultMap;
    }

    /** Returns task data, corresponding to defined pair of taskIs and sessionId
     * @param taskId - TaskData taskId
     * @param sessionId - session id
     * @return TaskData for selected params
     */
    public TaskData getTaskData(String taskId, String sessionId) {
        return (TaskData) entityManager.createQuery("select t from TaskData t where sessionId=(:sessionId) and taskId=(:taskId)")
                        .setParameter("sessionId", sessionId)
                        .setParameter("taskId", taskId).
                        getSingleResult();
    }

    /** Returns task data, corresponding to TaskData ids
     * @param ids - TaskData ids
     * @return map <TaskData id, TaskData>
     */
    public Map<Long, TaskData> getTaskData(Collection<Long> ids) {
        List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery("select t from TaskData t where id in (:ids)")
                                        .setParameter("ids", ids).
                                        getResultList();

        Map<Long, TaskData> result = new HashMap<Long, TaskData>();
        for (TaskData taskData : taskDataList) {
            result.put(taskData.getId(), taskData);
        }

        return result;
    }


}
