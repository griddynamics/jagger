package com.griddynamics.jagger.test.jaas.listener;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.testsuite.TestSuiteInfo;
import com.griddynamics.jagger.engine.e1.collector.testsuite.TestSuiteListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.test.jaas.util.TestContext;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * Gets expected data into temp storage.
 *
 * Created by ELozovan on 2016-09-27.
 */
public class TestSuiteConfigListener extends ServicesAware implements Provider<TestSuiteListener> {

    @Override
    public TestSuiteListener provide() {
        return new TestSuiteListener() {
            @Override
            public void onStart(TestSuiteInfo testSuiteInfo) {
                super.onStart(testSuiteInfo);
                // Ids are hard-coded for now. Re-factor once JFG-908 is ready.
                Set<SessionEntity> sessionsAvailable = getDataService().getSessions(Arrays.asList("5", "15", "42", "32", "17", "28", "45", "50", "12"));
                sessionsAvailable.stream().forEach(this::fixDates);
                TestContext.setSessions(sessionsAvailable);
            }

            /**
             * DataService returns dates as Timestamp, JSON deserialiser returns them as Date, so session.equals() returns false anyway.
             * This crutch reset date field values to avoid that type mismatch.
             */
            private SessionEntity fixDates(SessionEntity session){
                Date adjustedStart = new Date(session.getStartDate().getTime());
                Date adjustedEnd = new Date(session.getEndDate().getTime());

                session.setStartDate(adjustedStart);
                session.setEndDate(adjustedEnd);

                return session;
            }
        };
    }
}