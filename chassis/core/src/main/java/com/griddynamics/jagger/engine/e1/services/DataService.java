package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.dbapi.dto.DecisionPerSessionDto;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricPlotPointEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service provides access to tests results, stored in the Jagger database.
 * You can get a full information about sessions, tests, metrics.
 *
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details Where this service is available you can find in chapter: @ref section_listeners_services @n
 * @n
 * @par Example - get results from Jagger database:
 * @dontinclude ProviderOfLoadScenarioListener.java
 * @skip begin: following section is used for docu generation - access to Jagger results in database
 * @until end: following section is used for docu generation - access to Jagger results in database
 * @n Full example code you can find in chapter @ref TODO JFG-993 add correct link @n
 * @n
 * @ingroup Main_Services_group
 */
public interface DataService extends JaggerService {

    /**
     * Returns session's entity for specify session's id
     *
     * @param sessionId - session's id
     * @return session's entity
     * @author Gribov Kirill
     * @n
     */
    SessionEntity getSession(String sessionId);

    /**
     * Returns session entities for specify session ids.
     * If input session ids are an empty collection - returns all sessions.
     *
     * @param sessionIds - session ids
     * @return list of session entities
     * @author Gribov Kirill
     * @n
     */
    Set<SessionEntity> getSessions(Collection<String> sessionIds);

    /**
     * Returns decision for provided session
     * @param sessionId session's id
     * @return {@link DecisionPerSessionDto}
     */
    DecisionPerSessionDto getSessionDecisions(String sessionId);

    /**
     * Returns tests for specify session
     *
     * @param session - session entity
     * @return list of test entities
     * @author Gribov Kirill
     * @n
     */
    Set<TestEntity> getTests(SessionEntity session);

    /**
     * Returns tests for specify session's id
     *
     * @param sessionId - session's id
     * @return list of test entities
     * @author Gribov Kirill
     * @n
     */
    Set<TestEntity> getTests(String sessionId);

    /**
     * Returns all tests for specify list of session's ids
     *
     * @param sessionIds - session's ids
     * @return map of <session id, list of test entities> pairs
     * @author Gribov Kirill
     * @n
     */
    Map<String, Set<TestEntity>> getTests(Collection<String> sessionIds);

    /**
     * Returns test entity for specify session's id and test name
     *
     * @param sessionId - session's id
     * @param testName  - name of test
     * @return test entity
     * @author Gribov Kirill
     * @n
     */
    TestEntity getTestByName(String sessionId, String testName);

    /**
     * Returns test entity for specify session and test name
     *
     * @param session  - session entity
     * @param testName - name of test
     * @return test entity
     * @author Gribov Kirill
     * @n
     */
    TestEntity getTestByName(SessionEntity session, String testName);

    /**
     * Returns map, where key is session's id and value is test entity with specify name
     *
     * @param sessionIds - session's ids
     * @param testName   - name of test
     * @return map of <session id, test entity> pairs
     * @author Gribov Kirill
     * @n
     */
    Map<String, TestEntity> getTestsByName(Collection<String> sessionIds, String testName);

    /**
     * Returns all metric entities for specify test id
     *
     * @param testId - test id
     * @return list of metric entities
     * @author Gribov Kirill
     * @n
     */
    Set<MetricEntity> getMetrics(Long testId);

    /**
     * Returns all metric entities for specify test
     *
     * @param test - test entity
     * @return list of metric entities
     * @author Gribov Kirill
     * @n
     */
    Set<MetricEntity> getMetrics(TestEntity test);

    /**
     * Returns map, where key is test entity and value is a list of all test metrics
     *
     * @param tests - tests
     * @return map of <test entity, list of metric entity> pairs
     * @author Gribov Kirill
     * @n
     */
    Map<TestEntity, Set<MetricEntity>> getMetricsByTests(Collection<TestEntity> tests);

    /**
     * Returns map, where key is test id and value is a list of all test metrics
     *
     * @param testIds - test ids
     * @return map of <test id, list of metric entity> pairs
     * @author Gribov Kirill
     * @n
     */
    Map<Long, Set<MetricEntity>> getMetricsByTestIds(Collection<Long> testIds);

    /**
     * Return summary value for selected metric
     *
     * @param metric - metric entity
     * @return summary for selected metric
     * @author Dmitry Latnikov
     * @n
     * @details !Note: It is faster to get summary for set of metrics than fetch every metric in for loop @n
     * See docu for overloaded function with set of metrics @n
     */
    MetricSummaryValueEntity getMetricSummary(MetricEntity metric);

    /**
     * Return summary values for selected metrics
     *
     * @param metrics - metric entities
     * @return map of <metric entity, summary>
     * @author Dmitry Latnikov
     * @n
     * @details Preferable way to get data. Data will be fetched from database in batch in single request => @n
     * it is faster to get batch of metrics than fetch every metric in for loop @n
     */
    Map<MetricEntity, MetricSummaryValueEntity> getMetricSummary(Collection<MetricEntity> metrics);

    /**
     * Return list of points (values vs time) for selected metric
     *
     * @param metric - metric entity
     * @return list of points (value vs time) for selected metric
     * @author Dmitry Latnikov
     * @n
     * @details !Note: It is faster to get plot data for set of metrics than fetch every metric in for loop @n
     * See docu for overloaded function with set of metrics @n
     */
    List<MetricPlotPointEntity> getMetricPlotData(MetricEntity metric);

    /**
     * Return lists of points (values vs time) for selected metrics
     *
     * @param metrics - metric entities
     * @return map of <metic entity, list of points (value vs time)> for selected metric
     * @author Dmitry Latnikov
     * @n
     * @details Preferable way to get data. Data will be fetched from database in batch in single request => @n
     * it is faster to get batch of metrics than fetch every metric in for loop @n
     */
    Map<MetricEntity, List<MetricPlotPointEntity>> getMetricPlotData(Collection<MetricEntity> metrics);
}
