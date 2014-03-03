package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.TaskDataService;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 * @deprecated another conception of control since jagger 1.2.2-m3
 */
@Deprecated
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
            List<Object[]> taskDataList = entityManager.createNativeQuery(
                            "select taskData.id, workloadTaskData.name, workloadTaskData.description, taskData.taskId from " +
                                            "( "+
                                                "select " +
                                                    "l.*, s.name, s.description, s.version " +
                                                "from "+
                                                    "(select * from WorkloadTaskData where sessionId=:sessionId) as l "+
                                                "left outer join "+
                                                    "WorkloadDetails as s "+
                                                "on l.scenario_id=s.id "+
                                                "where " +
                                                    "l.sessionId =:sessionId"+
                                            ") as workloadTaskData " +
                            "inner join " +
                                "(select * from TaskData where sessionId=:sessionId) as taskData "+
                            "on " +
                                    "taskData.taskId=workloadTaskData.taskId and " +
                                    "taskData.sessionId=workloadTaskData.sessionId ")
                            .setParameter("sessionId", sessionId).getResultList();
            if (taskDataList == null) {
                return Collections.emptyList();
            }

            Collections.sort(taskDataList, new Comparator<Object[]>() {
                @Override
                public int compare(Object[] o1, Object[] o2) {
                    String o1TaskId = (String)o1[3];
                    String o2TaskId = (String)o2[3];

                    Integer o1Id = Integer.parseInt(o1TaskId.substring(5));
                    Integer o2Id = Integer.parseInt(o2TaskId.substring(5));
                    return o1Id.compareTo(o2Id);
                }
            });

            taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());
            for (Object[] taskData : taskDataList) {
                TaskDataDto dto = new TaskDataDto(((BigInteger)taskData[0]).longValue(), (String)taskData[1], (String)taskData[2]);
                taskDataDtoList.add(dto);
            }
            log.info("For session {} was loaded {} tasks for {} ms", new Object[]{sessionId, taskDataList.size(), System.currentTimeMillis() - timestamp});
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
        List<Object[]> list = entityManager.createNativeQuery
                (
                "select taskData.id, commonTests.name, commonTests.description, taskData.taskId , commonTests.clock, commonTests.clockValue, commonTests.termination" +
                        " from "+
                           "( "+
                           "select test.name, test.description, test.version, test.sessionId, test.taskId, test.clock, test.clockValue, test.termination from " +
                                                                    "( "+
                                                                          "select " +
                                                                                "l.*, s.name, s.description, s.version " +
                                                                          "from "+
                                                                                 "(select * from WorkloadTaskData where sessionId in (:sessions)) as l "+
                                                                          "left outer join "+
                                                                                 "(select * from WorkloadDetails) as s "+
                                                                          "on l.scenario_id=s.id "+
                                                                     ") as test " +
                           "inner join " +
                                   "( " +
                                        "select t.* from "+
                                                    "( "+
                                                       "select " +
                                                            "l.*, s.name, s.description, s.version " +
                                                       "from "+
                                                            "(select * from WorkloadTaskData where sessionId in (:sessions)) as l "+
                                                       "left outer join "+
                                                            "(select * from WorkloadDetails) as s "+
                                                       "on l.scenario_id=s.id " +
                                                    ") as t "+
                                        "group by "+
                                            "t.termination, t.clock, t.clockValue, t.name, t.version "+
                                        "having count(t.id)>=:sessionCount" +

                                   ") as testArch " +
                           "on "+
                                   "test.clock=testArch.clock and "+
                                   "test.clockValue=testArch.clockValue and "+
                                   "test.termination=testArch.termination and "+
                                   "test.name=testArch.name and "+
                                   "test.version=testArch.version "+
                           ") as commonTests "+
                "left outer join "+
                        "(select * from TaskData where sessionId in (:sessions)) as taskData "+
                "on "+
                        "commonTests.sessionId=taskData.sessionId and "+
                        "commonTests.taskId=taskData.taskId "
               ).setParameter("sessions", sessionIds)
                .setParameter("sessionCount", (long) sessionIds.size()).getResultList();

        //group tests by description
        HashMap<String, TaskDataDto> map = new HashMap<String, TaskDataDto>(list.size());
        HashMap<String, Integer> mapIds = new HashMap<String, Integer>(list.size());
        for (Object[] testData : list){
            BigInteger id = (BigInteger)testData[0];
            String name = (String) testData[1];
            String description = (String) testData[2];
            String taskId = (String)testData[3];
            String clock = testData[4] + " (" + testData[5] + ")";
            String termination = (String) testData[6];


            int taskIdInt = Integer.parseInt(taskId.substring(5));
            String key = description+name;
            if (map.containsKey(key)){
                map.get(key).getIds().add(id.longValue());

                Integer oldValue = mapIds.get(key);
                mapIds.put(key, (oldValue==null ? 0 : oldValue)+taskIdInt);
            }else{
                TaskDataDto taskDataDto = new TaskDataDto(id.longValue(), name, description);
                //merge
                if (map.containsKey(name)){
                    taskDataDto.getIds().addAll(map.get(name).getIds());

                    taskIdInt = taskIdInt + mapIds.get(name);
                }
                map.put(key, taskDataDto);
                mapIds.put(key, taskIdInt);
            }
        }

        if (map.isEmpty()){
            return Collections.EMPTY_LIST;
        }

        PriorityQueue<Object[]> priorityQueue= new PriorityQueue<Object[]>(mapIds.size(), new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                return ((Comparable)o1[0]).compareTo(o2[0]);
            }
        });

        for (String key : map.keySet()){
            TaskDataDto taskDataDto = map.get(key);
            if (taskDataDto.getIds().size() == sessionIds.size()){
                priorityQueue.add(new Object[]{mapIds.get(key), taskDataDto});
            }
        }

        ArrayList<TaskDataDto> result = new ArrayList<TaskDataDto>(priorityQueue.size());
        while (!priorityQueue.isEmpty()){
            result.add((TaskDataDto)priorityQueue.poll()[1]);
        }

        log.info("For sessions {} was loaded {} tasks for {} ms", new Object[]{sessionIds, result.size(), System.currentTimeMillis() - timestamp});
        return result;
    }
}
