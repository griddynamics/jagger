package com.griddynamics.jagger.test.jaas.listener;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.testsuite.TestSuiteInfo;
import com.griddynamics.jagger.engine.e1.collector.testsuite.TestSuiteListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.test.jaas.util.TestContext;

import java.util.Arrays;

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
                TestContext.setSessions(getDataService().getSessions(Arrays.asList("5", "15", "42", "32", "17"))); //hard-coded for now. Re-factor once JFG-908 is ready.
            }
        };
    }
}