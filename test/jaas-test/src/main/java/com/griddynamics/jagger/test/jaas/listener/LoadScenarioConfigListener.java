package com.griddynamics.jagger.test.jaas.listener;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioInfo;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.test.jaas.util.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Set;

/**
 * Gets expected data into temp storage({@link TestContext}).
 *
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
                // TODO: Ids are hard-coded for now. Re-factor once JFG-908 is ready.
                Set<SessionEntity> sessionsAvailable = getDataService().getSessions(Arrays.asList("5", "15", "42", "32", "17", "28", "45", "50", "12"));
                sessionsAvailable.stream().forEach(this::correctDateFieldValue);
                TestContext.setSessions(sessionsAvailable);

                findAndStoreExpectedTests(sessionsAvailable);
            }

            private void findAndStoreExpectedTests(Set<SessionEntity> sessionsAvailable) {
                SessionEntity sessionToGetTests = null;
                Set<TestEntity> tests = null;
                while (null == tests) {
                    sessionToGetTests = sessionsAvailable.stream().skip(new Random().nextInt(sessionsAvailable.size() - 1)).findFirst().orElse(null);
                    tests = getDataService().getTests(sessionToGetTests);

                    if (tests.isEmpty()){
                        tests = null; //Let's find another session which shall have some tests stored.
                    }
                }

                tests.stream().forEach(this::correctDateFieldValue);
                TestContext.addTests(sessionToGetTests.getId(), tests);
            }

            /**
             * DataService returns dates as Timestamp, JSON deserialiser returns them as Date, so #equals() returns false anyway.
             * This crutch resets date fields values to avoid that type mismatch.
             */
            private <T> void correctDateFieldValue(T entity){
                final String getterPrefix = "get";
                Method[] allMethods = entity.getClass().getDeclaredMethods();
                for (Method m : allMethods) {
                    String mName = m.getName();
                    Type mReturnType = m.getGenericReturnType();

                    // Looking for a Date getXYZ()
                    if (!(mName.startsWith(getterPrefix) && (mReturnType.equals(Date.class)))) { continue; }

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