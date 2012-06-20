package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.webclient.client.TaskDataService;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class TaskDataServiceImpl /*extends RemoteServiceServlet*/ implements TaskDataService {
    private static final Logger log = LoggerFactory.getLogger(TaskDataServiceImpl.class);

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<TaskDataDto> getTaskDataForSession(String sessionId) {
        long timestamp = System.currentTimeMillis();

        List<TaskDataDto> taskDataDtoList = null;
        try {
            @SuppressWarnings("unchecked")
            List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery(
                    "select td from TaskData as td where td.sessionId=:sessionId and td.taskId in (select wd.taskId from WorkloadData as wd where wd.sessionId=:sessionId) order by td.number asc")
                    .setParameter("sessionId", sessionId).getResultList();

            if (taskDataList == null) {
                return Collections.emptyList();
            }

            taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());
            for (TaskData taskData : taskDataList) {
                taskDataDtoList.add(new TaskDataDto(taskData.getId(), taskData.getTaskName()));
            }

            log.info("For session {} was loaded {} tasks for {} ms", new Object[]{sessionId, taskDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during tasks fetching for session "+sessionId, e);
            throw new RuntimeException(e);
        }
        return taskDataDtoList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds) {
        long timestamp = System.currentTimeMillis();

        List<TaskDataDto> taskDataDtoList = null;

        try {
            List<String> workloadTaskIdList = (List<String>) entityManager.createQuery
                    ("select wd.taskId from WorkloadData as wd where wd.sessionId in (:sessionIds)")
                    .setParameter("sessionIds", sessionIds)
                    .getResultList();

            List<Object[]> commonsTasks = (List<Object[]>) entityManager.createQuery
                    ("select td.taskName, td.taskId from TaskData as td where  td.sessionId in (:sessionIds)" +
                            "and td.taskId in (:workloadTaskIdList) group by td.taskId, td.taskName having count(td.id) >= :count")
                    .setParameter("sessionIds", sessionIds)
                    .setParameter("workloadTaskIdList", workloadTaskIdList)
                    .setParameter("count", (long) sessionIds.size())
                    .getResultList();

            Map<String, String> commonsTaskMap = new HashMap<String, String>();
            for (Object[] obj : commonsTasks) {
                String taskName = (String) obj[0];
                String taskId = (String) obj[1];
                commonsTaskMap.put(taskId, taskName);
            }
            log.debug("For sessions {} commons tasks are: {}", sessionIds, commonsTaskMap);

            List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery(
                    "select td from TaskData as td where td.sessionId in (:sessionIds) and td.taskId in (:workloadTaskIdList) order by td.number asc")
                    .setParameter("sessionIds", sessionIds)
                    .setParameter("workloadTaskIdList", workloadTaskIdList)
                    .getResultList();

            if (taskDataList == null) {
                return Collections.emptyList();
            }

            Map<String, TaskDataDto> added = new LinkedHashMap<String, TaskDataDto>();
            for (TaskData taskData : taskDataList) {
                String taskName = taskData.getTaskName();
                String taskId = taskData.getTaskId();
                Long id = taskData.getId();

                if (!commonsTaskMap.containsKey(taskId) || !commonsTaskMap.get(taskId).equals(taskName)) {
                    continue;
                }

                if (!added.containsKey(taskId)) {
                    added.put(taskId, new TaskDataDto(id, taskName));
                }
                added.get(taskId).getIds().add(id);
            }
            taskDataDtoList = new ArrayList<TaskDataDto>(added.values());

            log.info("For sessions {} were loaded {} tasks for {} ms", new Object[]{sessionIds, taskDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during common tasks fetching for sessions " + sessionIds, e);
            throw new RuntimeException(e);
        }

        return taskDataDtoList;
    }

}
