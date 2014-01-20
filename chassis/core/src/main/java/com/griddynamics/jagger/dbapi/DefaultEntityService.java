package com.griddynamics.jagger.dbapi;

import com.griddynamics.jagger.dbapi.entity.MetricEntity;
import com.griddynamics.jagger.dbapi.entity.MetricValueEntity;
import com.griddynamics.jagger.dbapi.entity.SessionEntity;
import com.griddynamics.jagger.dbapi.entity.TestEntity;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DiagnosticResultEntity;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.MetricDetails;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/17/13
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultEntityService extends HibernateDaoSupport implements EntityService {

    private static final Map<String, String> STANDARD_METRICS = new HashMap<String, String>();
    static {
        STANDARD_METRICS.put(JaggerMetric.Throughput, "Throughput");
        STANDARD_METRICS.put(JaggerMetric.AvgLatency, "AvgLatency");
        STANDARD_METRICS.put(JaggerMetric.Failures, "Failures");
        STANDARD_METRICS.put(JaggerMetric.Samples, "Samples");
        STANDARD_METRICS.put(JaggerMetric.StdDevLatency, "StdDevLatency");
        STANDARD_METRICS.put(JaggerMetric.SuccessRate, "SuccessRate");
    }

    @Override
    public SessionEntity getSession(String sessionId) {
        List<SessionEntity> sessions = getSessions(Arrays.asList(sessionId));
        if (!sessions.isEmpty()){
            return sessions.iterator().next();
        }

        return null;
    }

    @Override
    public List<SessionEntity> getSessions(List<String> sessionIds) {
        if (sessionIds.isEmpty()){
            return Collections.EMPTY_LIST;
        }

        List<SessionData> sessions = getHibernateTemplate().findByNamedParam("select ses from SessionData as ses " +
                                                                                "where ses.sessionId in (:sessionIds)",
                                                                                "sessionIds", sessionIds);

        List<SessionEntity> entities = new ArrayList<SessionEntity>(sessions.size());

        for (SessionData sessionData : sessions){
            SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.setId(sessionData.getSessionId());
            sessionEntity.setStartTime(sessionData.getStartTime().getTime());
            sessionEntity.setEndTime(sessionData.getEndTime().getTime());
            sessionEntity.setKernels(sessionData.getActiveKernels());
            sessionEntity.setComment(sessionData.getComment());

            entities.add(sessionEntity);
        }

        return entities;
    }

    public List<TestEntity> getTests(SessionEntity session){
        return getTests(session.getId());
    }

    public List<TestEntity> getTests(String sessionId){
        Map<String, List<TestEntity>> map = getTests(Arrays.asList(sessionId));

        List<TestEntity> result = map.get(sessionId);
        if (result != null){
            return result;
        }

        return Collections.EMPTY_LIST;
    }

    public TestEntity getTestByName(SessionEntity session, String testName){
        return getTestByName(session.getId(), testName);
    }

    public TestEntity getTestByName(String sessionId, String testName){
        Map<String, TestEntity> map = getTestsByName(Arrays.asList(sessionId), testName);

        TestEntity result = map.get(sessionId);
        if (result != null){
            return result;
        }

        return null;
    }

    public Map<String, TestEntity> getTestsByName(List<String> sessionIds, String testName){
        Map<String, List<TestEntity>> tests = getTestsWithName(sessionIds, testName);

        Map<String, TestEntity> result = new HashMap<String, TestEntity>(tests.size());

        for (Map.Entry<String, List<TestEntity>> entry : tests.entrySet()){
            List<TestEntity> testEntities = entry.getValue();
            if (!testEntities.isEmpty()){
                result.put(entry.getKey(), testEntities.iterator().next());
            }
        }

        return result;
    }

    public Map<String, List<TestEntity>> getTests(List<String> sessionIds){
        return getTestsWithName(sessionIds, "%");
    }

    private Map<String, List<TestEntity>> getTestsWithName(List<String> sessionIds, String testName){
        if (sessionIds.isEmpty()){
            return Collections.EMPTY_MAP;
        }

        List<Object[]> tasksEntities = getHibernateTemplate().findByNamedParam("select task, taskData.id from WorkloadTaskData task, TaskData taskData " +
                                                                                   "where task.sessionId in (:sessionIds) and " +
                                                                                   "      task.scenario.name like :name and " +
                                                                                   "      taskData.sessionId in (:sessionIds) and "+
                                                                                   "      taskData.taskId=task.taskId",
                                                                                          new String[]{"sessionIds", "name"}, new Object[]{sessionIds, testName});

        Map<String, List<TestEntity>> result = new HashMap<String, List<TestEntity>>();

        for (Object[] taskEntity : tasksEntities){
            WorkloadTaskData task = (WorkloadTaskData)taskEntity[0];

            TestEntity entity = new TestEntity();
            entity.setId((Long)taskEntity[1]);
            entity.setName(task.getScenario().getName());
            entity.setDescription(task.getScenario().getDescription());
            entity.setLoad(task.getClock());
            entity.setTerminationStrategy(task.getTermination());

            if (result.containsKey(task.getSessionId())){
                result.get(task.getSessionId()).add(entity);
            }else{
                List<TestEntity> list = new ArrayList<TestEntity>();
                list.add(entity);

                result.put(task.getSessionId(), list);
            }
        }

        return result;
    }

    public List<MetricEntity> getMetrics(Long testId){
        Map<Long, List<MetricEntity>> map = getMetricsByIds(Arrays.asList(testId));

        List<MetricEntity> result = map.get(testId);
        if (result != null){
            return result;
        }

        return Collections.EMPTY_LIST;
    }

    public List<MetricEntity> getMetrics(TestEntity test){
        Map<TestEntity, List<MetricEntity>> map = getMetrics(Arrays.asList(test));

        List<MetricEntity> result = map.get(test);
        if (result != null){
            return result;
        }

        return Collections.EMPTY_LIST;
    }

    public Map<TestEntity, List<MetricEntity>> getMetrics(List<TestEntity> tests){
        Map<Long, TestEntity> map = new HashMap<Long, TestEntity>(tests.size());
        List<Long> ids = new ArrayList<Long>(tests.size());

        for (TestEntity test : tests){
            map.put(test.getId(), test);
            ids.add(test.getId());
        }

        Map<Long, List<MetricEntity>> metrics = getMetricsByIds(ids);

        Map<TestEntity, List<MetricEntity>> result = new HashMap<TestEntity, List<MetricEntity>>();

        for (Long key : map.keySet()){
            result.put(map.get(key), metrics.get(key));
        }

        return result;
    }

    public Map<Long, List<MetricEntity>> getMetricsByIds(List<Long> testIds){
        if (testIds.isEmpty()){
            return Collections.EMPTY_MAP;
        }

        MultiMap<Long, MetricEntity> result = new MultiMap<Long, MetricEntity>();

        //try to find standard metrics
        List<Object[]> standardMetrics = getHibernateTemplate().findByNamedParam("select task, taskData.id from WorkloadTaskData task, TaskData taskData " +
                                                                                   "where taskData.id in (:ids) and " +
                                                                                   "      task.taskId=taskData.taskId and " +
                                                                                   "      task.sessionId=taskData.sessionId","ids", testIds);
        for (Object[] taskEntity : standardMetrics){
            WorkloadTaskData task = (WorkloadTaskData)taskEntity[0];
            Long taskId = (Long)taskEntity[1];

            MetricEntity throughput = new MetricEntity();
            throughput.setMetricId(JaggerMetric.Throughput);
            throughput.setSummaryValue(task.getThroughput().doubleValue());
            throughput.setDisplayName(STANDARD_METRICS.get(JaggerMetric.Throughput));
            result.put(taskId, throughput);

            MetricEntity avgLatency = new MetricEntity();
            avgLatency.setMetricId(JaggerMetric.AvgLatency);
            avgLatency.setSummaryValue(task.getAvgLatency().doubleValue());
            avgLatency.setDisplayName(STANDARD_METRICS.get(JaggerMetric.AvgLatency));
            result.put(taskId, avgLatency);

            MetricEntity failures = new MetricEntity();
            failures.setMetricId(JaggerMetric.Failures);
            failures.setSummaryValue(task.getFailuresCount().doubleValue());
            failures.setDisplayName(STANDARD_METRICS.get(JaggerMetric.Failures));
            result.put(taskId, failures);

            MetricEntity samples = new MetricEntity();
            samples.setMetricId(JaggerMetric.Samples);
            samples.setSummaryValue(task.getSamples().doubleValue());
            samples.setDisplayName(STANDARD_METRICS.get(JaggerMetric.Samples));
            result.put(taskId, samples);

            MetricEntity stdDevLatency = new MetricEntity();
            stdDevLatency.setMetricId(JaggerMetric.StdDevLatency);
            stdDevLatency.setSummaryValue(task.getStdDevLatency().doubleValue());
            stdDevLatency.setDisplayName(STANDARD_METRICS.get(JaggerMetric.StdDevLatency));
            result.put(taskId, stdDevLatency);

            MetricEntity successRate = new MetricEntity();
            successRate.setMetricId(JaggerMetric.SuccessRate);
            successRate.setSummaryValue(task.getSuccessRate().doubleValue());
            successRate.setDisplayName(STANDARD_METRICS.get(JaggerMetric.SuccessRate));
            result.put(taskId, successRate);
        }

        //try to find custom metrics
        List<Object[]> customSummaryMetrics = getHibernateTemplate().findByNamedParam("select metric, taskData.id from DiagnosticResultEntity metric, TaskData taskData " +
                                                                                        "where taskData.id in (:ids) and " +
                                                                                        "      metric.workloadData.taskId=taskData.taskId and " +
                                                                                        "      metric.workloadData.sessionId=taskData.sessionId", "ids", testIds);
        for (Object[] customMetric : customSummaryMetrics){
            DiagnosticResultEntity metric = (DiagnosticResultEntity) customMetric[0];

            MetricEntity metricEntity = new MetricEntity();
            metricEntity.setMetricId(metric.getName());
            //need to change to real display name!!
            metricEntity.setDisplayName(metric.getName());
            metricEntity.setSummaryValue(metric.getTotal());

            result.put((Long)customMetric[1], metricEntity);
        }

        List<MetricDetails> customPlotMetrics = getHibernateTemplate().findByNamedParam("select metric from MetricDetails metric " +
                                                                                            "where metric.taskData.id in (:ids) " +
                                                                                            "group by metric.metric","ids", testIds);
        //try to find metrics with plotData only
        for (MetricDetails metricDetails : customPlotMetrics){
            MetricEntity metricEntity = new MetricEntity();
            metricEntity.setMetricId(metricDetails.getMetric());
            //need to change to real display name!!
            metricEntity.setDisplayName(metricDetails.getMetric());
            metricEntity.setSummaryValue(null);

            if (result.get(metricDetails.getId()) != null && !result.get(metricDetails.getId()).contains(metricEntity)){
                result.put(metricDetails.getTaskData().getId(), metricEntity);
            }
        }
        return result.getOrigin();
    }

    public List<MetricValueEntity> getMetricValues(Long testId, String metricId){
        Map<String, List<MetricValueEntity>> map = getMetricValuesByIds(testId, Arrays.asList(metricId));

        List<MetricValueEntity> result = map.get(metricId);
        if (result != null){
            return result;
        }

        return Collections.EMPTY_LIST;
    }

    public List<MetricValueEntity> getMetricValues(TestEntity test, MetricEntity metric){
        return getMetricValues(test.getId(), metric);
    }

    public List<MetricValueEntity> getMetricValues(Long testId, MetricEntity metric){
        Map<MetricEntity, List<MetricValueEntity>> map = getMetricValues(testId, Arrays.asList(metric));

        List<MetricValueEntity> result = map.get(metric);
        if (result != null){
            return result;
        }

        return Collections.EMPTY_LIST;
    }

    public Map<String, List<MetricValueEntity>> getMetricValuesByIds(Long testId, List<String> metricIds){
        if (metricIds.isEmpty()){
            return Collections.EMPTY_MAP;
        }

        MultiMap<String, MetricValueEntity> result = new MultiMap<String,MetricValueEntity>();

        List<MetricDetails> metricValues = getHibernateTemplate().findByNamedParam("select metric from MetricDetails metric " +
                                                                                    "where metric.taskData.id=:testId",
                                                                                    "testId", testId);
        if (metricValues.isEmpty()){
            return Collections.EMPTY_MAP;
        }

        for (MetricDetails metricValue : metricValues){
            MetricValueEntity valueEntity = new MetricValueEntity();
            valueEntity.setTimeStamp(metricValue.getTime());
            valueEntity.setValue(metricValue.getValue());

            result.put(metricValue.getMetric(), valueEntity);
        }

        return result.getOrigin();
    }

    public Map<MetricEntity, List<MetricValueEntity>> getMetricValues(TestEntity test, List<MetricEntity> metrics){
        return getMetricValues(test.getId(), metrics);
    }

    public Map<MetricEntity, List<MetricValueEntity>> getMetricValues(Long testId, List<MetricEntity> metrics){
        List<String> ids = new ArrayList<String>(metrics.size());
        Map<String, MetricEntity> map = new HashMap<String, MetricEntity>(metrics.size());
        for (MetricEntity metricEntity : metrics){
            ids.add(metricEntity.getMetricId());
            map.put(metricEntity.getMetricId(), metricEntity);
        }

        Map<String, List<MetricValueEntity>> metricValues = getMetricValuesByIds(testId, ids);

        Map<MetricEntity, List<MetricValueEntity>> result = new HashMap<MetricEntity, List<MetricValueEntity>>();
        for (String key : map.keySet()){
            result.put(map.get(key), metricValues.get(key));
        }

        return result;
    }

    class MultiMap<K,V>{
        private Map<K,List<V>> map;

        public MultiMap(){
            map = new HashMap<K, List<V>>();
        }

        public void put(K key, V value){
            if (map.containsKey(key)){
                map.get(key).add(value);
            }else{
                ArrayList<V> list = new ArrayList<V>();
                list.add(value);

                map.put(key, list);
            }
        }

        public List<V> get(K key){
            return map.get(key);
        }

        public Map<K,List<V>> getOrigin(){
            return map;
        }
    }
}
