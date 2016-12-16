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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Set;


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
                Set<SessionEntity> sessionsAvailable = getDataService().getSessions(Collections.emptyList());
                sessionsAvailable.forEach(this::correctDateFieldValue);
                TestContext.setSessions(sessionsAvailable);

                findAndLoadExpectedTests(sessionsAvailable);

                String tmpSessionId = TestContext.getTests().keySet().toArray(new String[]{})[0];
                findAndLoadExpectedMetrics(tmpSessionId, TestContext.getTestsBySessionId(tmpSessionId));
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

            private void findAndLoadExpectedTests(Set<SessionEntity> sessionsAvailable) {
                SessionEntity sessionToGetTests = null;
                Set<TestEntity> tests = null;
                while (null == tests) {
                    sessionToGetTests = sessionsAvailable.stream().skip(new Random().nextInt(sessionsAvailable.size() - 1)).findFirst().orElse(null);
                    tests = getDataService().getTests(sessionToGetTests);

                    if (tests.isEmpty()) {
                        tests = null; //Let's find another session which shall have some tests stored.
                    }
                }

                tests.forEach(this::correctDateFieldValue);
                TestContext.addTests(sessionToGetTests.getId(), tests);
            }

            private void findAndLoadExpectedMetrics(String sessionId, Set<TestEntity> testsAvailable) {
                TestEntity testToGetMetricsFrom = null;
                Set<MetricEntity> metrics = null;
                while (null == metrics) {
                    testToGetMetricsFrom = testsAvailable.size() < 2 ?
                            testsAvailable.stream().findFirst().orElse(null)
                            : testsAvailable.stream().skip(new Random().nextInt(testsAvailable.size() - 1)).findFirst().orElse(null);
                    metrics = getDataService().getMetrics(testToGetMetricsFrom);

                    if (metrics.isEmpty()) {
                        metrics = null; //Let's find another test which shall have some metrics stored.
                    }
                }

                TestContext.setMetricPlotData(getDataService().getMetricPlotData(metrics));
                TestContext.setMetricSummaries(getDataService().getMetricSummary(metrics));

                metrics.forEach(this::correctDateFieldValue);
                TestContext.addMetrics(sessionId, testToGetMetricsFrom.getName(), metrics);
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