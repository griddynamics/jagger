package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.engine.e1.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Andrey Badaev
 * Date: 01/12/16
 */
public class ExampleTestGroupListener implements Provider<TestGroupListener> {
    
    private final static Logger log = LoggerFactory.getLogger(ExampleTestGroupListener.class);
    
    @Override
    public TestGroupListener provide() {
        return new TestGroupListener() {
            @Override
            public void onStart(TestGroupInfo infoStart) {
                log.info("Started {} test group execution", infoStart.getTestGroup().getTaskName());
            }
            
            @Override
            public void onStop(TestGroupInfo infoStop) {
                log.info("{} test group execution took {}ms", infoStop.getTestGroup().getTaskName(), infoStop.getDuration());
            }
        };
    }
}
