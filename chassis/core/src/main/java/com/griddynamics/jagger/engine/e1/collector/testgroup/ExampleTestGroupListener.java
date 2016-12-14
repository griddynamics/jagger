package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* begin: following section is used for docu generation - example of the test group listener */

/** Example of the test group listener
 * @author Andrey Badaev
 * @n
 * @par Details:
 * @details
 * Does nothing but logging at test group execution start and stop
 *
 * @ingroup Main_Listeners_group */
public class ExampleTestGroupListener extends ServicesAware implements Provider<TestGroupListener> {
    
    private final static Logger log = LoggerFactory.getLogger(ExampleTestGroupListener.class);
    
    @Override
    protected void init() {
        super.init();
    }
    
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

/* end: following section is used for docu generation - example of the test group listener */
