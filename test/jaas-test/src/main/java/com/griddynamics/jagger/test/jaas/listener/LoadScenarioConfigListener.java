package com.griddynamics.jagger.test.jaas.listener;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioInfo;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.DefaultHttpInvoker;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import com.griddynamics.jagger.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Loads expected data into temp storage({@link TestContext}).
 * <p>
 * Created by ELozovan on 2016-09-27.
 */
public class LoadScenarioConfigListener extends ServicesAware implements Provider<LoadScenarioListener> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadScenarioConfigListener.class);

    @Override
    public LoadScenarioListener provide() {
        return new LoadScenarioListener() {
            @Override
            public void onStart(LoadScenarioInfo loadScenarioInfo) {
                super.onStart(loadScenarioInfo);
                //collect all test sessions
                Set<SessionEntity> sessionsAvailable = getDataService().getSessions(Collections.emptyList());
                sessionsAvailable.forEach(this::correctDateFieldValue);
                TestContext.setSessions(sessionsAvailable);

                // collect sessions with not empty tests
                Map<SessionEntity, Set<TestEntity>> testToTest = sessionsAvailable.stream()
                        .map(s-> Pair.of(s, getDataService().getTests(s)))
                        .filter(e->!e.getSecond().isEmpty())
                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

                Pair<SessionEntity, Map.Entry<TestEntity, Set<MetricEntity>>> testData = searchTestData(testToTest);
                Set<MetricEntity> metrics = testData.getSecond().getValue();
                SessionEntity session = testData.getFirst();

                metrics.forEach(this::correctDateFieldValue);
                TestContext.addMetrics(session.getId(), testData.getSecond().getKey().getName(), metrics);

                Set<TestEntity> tests = testToTest.get(session);
                tests.forEach(this::correctDateFieldValue);
                TestContext.addTests(session.getId(), tests);
            }

            @Override
            public void onStop(LoadScenarioInfo loadScenarioInfo) {
                super.onStop(loadScenarioInfo);
                DefaultHttpInvoker invoker = new DefaultHttpInvoker();
                JHttpEndpoint jaasEndpoint = new JHttpEndpoint(TestContext.getEndpointUri());

                // Request to delete executions not deleted during test run.
                for (Long executionId : TestContext.getCreatedExecutionIds()) {
                    try {
                        invoker.invoke(new JHttpQuery<String>().delete().path(TestContext.getExecutionsUri() + "/" + executionId), jaasEndpoint);
                    } catch (InvocationException ignored) {
                    }
                }
            }

            /**
             * Search and return random test with not empty plot data and summary for metrics
             */
            private Pair<SessionEntity, Map.Entry<TestEntity, Set<MetricEntity>>> searchTestData(Map<SessionEntity, Set<TestEntity>> sessionsToTests){
                Random rnd = new Random();
                Pair<SessionEntity, Map.Entry<TestEntity, Set<MetricEntity>>> testData  = null;

                for (Map.Entry<SessionEntity, Set<TestEntity>> sessionToTests : sessionsToTests.entrySet()) {
                    Map<TestEntity, Set<MetricEntity>> testsToMetrics = getDataService().getMetricsByTests(sessionToTests.getValue());

                    for (Map.Entry<TestEntity, Set<MetricEntity>> testToMetrics : testsToMetrics.entrySet()) {
                        if(testToMetrics.getValue().stream().anyMatch(m -> m.isPlotAvailable() && m.isSummaryAvailable())){
                            TestContext.setMetricPlotData(getDataService().getMetricPlotData(testToMetrics.getValue()));
                            TestContext.setMetricSummaries(getDataService().getMetricSummary(testToMetrics.getValue()));
                            testData = Pair.of(sessionToTests.getKey(), testToMetrics);
                            if(rnd.nextFloat()>0.7){ //provide some randomization of test data between test runs
                                return testData;
                            }
                        }
                    }
                }

                if(testData==null){
                    throw new RuntimeException("There are no appropriate test data. Expected at least one test session with test has both metric plot data and summary");
                }

                return testData;
            }

            /**
             * DataService returns dates as Timestamp, JSON deserialiser returns them as Date, so #equals() returns false anyway.
             * This crutch resets date fields values to avoid that type mismatch.
             */
            private <T> void correctDateFieldValue(T entity) {
                final String getterPrefix = "get";
                Method[] allMethods = entity.getClass().getDeclaredMethods();
                for (Method m : allMethods) {
                    String mName = m.getName();
                    Type mReturnType = m.getGenericReturnType();

                    // Looking for a Date getXYZ()
                    if (!(mName.startsWith(getterPrefix) && (mReturnType.equals(Date.class)))) {
                        continue;
                    }

                    m.setAccessible(true);
                    try {
                        Date adjustedValue = new Date(((Date) m.invoke(entity)).getTime());
                        Method theSetter = entity.getClass().getDeclaredMethod(mName.replace(getterPrefix, "set"), Date.class);
                        theSetter.setAccessible(true);
                        theSetter.invoke(entity, adjustedValue);
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        LOGGER.warn("Was not able to correct date value({}) in the entity {}.", mName, entity);
                    }
                }
            }
        };
    }
}