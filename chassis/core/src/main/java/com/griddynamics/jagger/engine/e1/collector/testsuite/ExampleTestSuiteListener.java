package com.griddynamics.jagger.engine.e1.collector.testsuite;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Does nothing but logging at test suite execution start and stop
 * Created by Andrey Badaev
 * Date: 05/12/16
 */
public class ExampleTestSuiteListener extends ServicesAware implements Provider<TestSuiteListener> {
    
    private final static Logger log = LoggerFactory.getLogger(ExampleTestSuiteListener.class);
    
    @Override
    protected void init() {
        super.init();
    }
    
    @Override
    public TestSuiteListener provide() {
        return new TestSuiteListener() {
            @Override
            public void onStart(TestSuiteInfo testSuiteInfo) {
                log.info("Started {} session execution", testSuiteInfo.getSessionId());
            }
    
            @Override
            public void onStop(TestSuiteInfo testSuiteInfo) {
                log.info("{} session execution took {}ms", testSuiteInfo.getSessionId(), testSuiteInfo.getDuration());
            }
        };
    }
}
