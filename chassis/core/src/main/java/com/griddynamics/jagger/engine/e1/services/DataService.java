package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricPlotPointEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Service provides access to tests results, stored in jagger database. You can get a full information about sessions, tests, metrics
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details Where this service is available you can find in chapter: @ref Main_ListenersAndServices_group @n
 * @n
 * @par Example - get results from Jagger database:
 * @dontinclude  ProviderOfTestSuiteListener.java
 * @skip  begin: following section is used for docu generation - access to Jagger results in database
 * @until end: following section is used for docu generation - access to Jagger results in database
 *
 * @n
 * Full example code you can find in chapter @ref Main_CustomListenersExamples_group @n
 * @n
 * @ingroup Main_Services_group */
 public interface DataService extends JaggerService {

    /** Returns session's entity for specify session's id
     * @author Gribov Kirill
     * @n
     * @param sessionId - session's id
     * @return session's entity*/
    SessionEntity getSession(String sessionId);

    /** Returns session's entities for specify session's ids
     * @author Gribov Kirill
     * @n
     * @param sessionIds - session's ids
     * @return list of session's entities */
    Set<SessionEntity> getSessions(Collection<String> sessionIds);

    /** Returns tests for specify session
     * @author Gribov Kirill
     * @n
     * @param session - session entity
     * @return list of test entities */
    Set<TestEntity> getTests(SessionEntity session);

    /** Returns tests for specify session's id
     * @author Gribov Kirill
     * @n
     * @param sessionId - session's id
     * @return list of test entities */
    Set<TestEntity> getTests(String sessionId);

    /** Returns all tests for specify list of session's ids
     * @author Gribov Kirill
     * @n
     * @param sessionIds - session's ids
     * @return map of <session id, list of test entities> pairs*/
    Map<String, Set<TestEntity>> getTests(Collection<String> sessionIds);

    /** Returns test entity for specify session's id and test name
     * @author Gribov Kirill
     * @n
     * @param sessionId - session's id
     * @param testName - name of test
     * @return test entity */
    TestEntity getTestByName(String sessionId, String testName);

    /** Returns test entity for specify session and test name
     * @author Gribov Kirill
     * @n
     * @param session - session entity
     * @param testName - name of test
     * @return test entity */
    TestEntity getTestByName(SessionEntity session, String testName);

    /** Returns map, where key is session's id and value is test entity with specify name
     * @author Gribov Kirill
     * @n
     * @param sessionIds - session's ids
     * @param testName - name of test
     * @return map of <session id, test entity> pairs*/
    Map<String, TestEntity> getTestsByName(Collection<String> sessionIds, String testName);

    /** Returns all metric entities for specify test id
     * @author Gribov Kirill
     * @n
     * @param testId - test id
     * @return list of metric entities*/
    Set<MetricEntity> getMetrics(Long testId);

    /** Returns all metric entities for specify test
     * @author Gribov Kirill
     * @n
     * @param test - test entity
     * @return list of metric entities*/
    Set<MetricEntity> getMetrics(TestEntity test);

    /** Returns map, where key is test entity and value is a list of all test metrics
     * @author Gribov Kirill
     * @n
     * @param tests - tests
     * @return map of <test entity, list of metric entity> pairs*/
    Map<TestEntity, Set<MetricEntity>> getMetricsByTests(Collection<TestEntity> tests);

    /** Returns map, where key is test id and value is a list of all test metrics
     * @author Gribov Kirill
     * @n
     * @param testIds - test ids
     * @return map of <test id, list of metric entity> pairs*/
    Map<Long, Set<MetricEntity>> getMetricsByTestIds(Collection<Long> testIds);

    /** Return summary value for selected metric
     * @author Dmitry Latnikov
     * @n
     * @details
     * !Note: It is faster to get summary for set of metrics than fetch every metric in for loop @n
     * See docu for overloaded function with set of metrics @n
     * @param metric - metric entity
     * @return summary value for selected metric  */
    Double getMetricSummary(MetricEntity metric);

    /** Return summary values for selected metrics
     * @author Dmitry Latnikov
     * @n
     * @details
     * Preferable way to get data. Data will be fetched from database in batch in single request => @n
     * it is faster to get batch of metrics than fetch every metric in for loop @n
     * @param metrics - metric entities
     * @return map of <metric entity, summary value> */
    Map<MetricEntity,Double> getMetricSummary(Collection<MetricEntity> metrics);

    /** Return list of points (values vs time) for selected metric
     * @author Dmitry Latnikov
     * @n
     * @details
     * !Note: It is faster to get plot data for set of metrics than fetch every metric in for loop @n
     * See docu for overloaded function with set of metrics @n
     * @param metric - metric entity
     * @return list of points (value vs time) for selected metric  */
    List<MetricPlotPointEntity> getMetricPlotData(MetricEntity metric);

    /** Return lists of points (values vs time) for selected metrics
     * @author Dmitry Latnikov
     * @n
     * @details
     * Preferable way to get data. Data will be fetched from database in batch in single request => @n
     * it is faster to get batch of metrics than fetch every metric in for loop @n
     * @param metrics - metric entities
     * @return map of <metic entity, list of points (value vs time)> for selected metric  */
    Map<MetricEntity,List<MetricPlotPointEntity>> getMetricPlotData(Collection<MetricEntity> metrics);

}
