package com.griddynamics.jagger.engine.e1.collector.loadscenario;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Does nothing but logging at test suite execution start and stop
 * Created by Andrey Badaev
 * Date: 05/12/16
 */
public class ExampleLoadScenarioListener extends ServicesAware implements Provider<LoadScenarioListener> {
    
    private final static Logger log = LoggerFactory.getLogger(ExampleLoadScenarioListener.class);
    
    @Override
    protected void init() {
        super.init();
    }
    
    @Override
    public LoadScenarioListener provide() {
        return new LoadScenarioListener() {
            @Override
            public void onStart(LoadScenarioInfo loadScenarioInfo) {
                log.info("Started {} session execution", loadScenarioInfo.getSessionId());
            }
    
            @Override
            public void onStop(LoadScenarioInfo loadScenarioInfo) {
                log.info("{} session execution took {}ms", loadScenarioInfo.getSessionId(), loadScenarioInfo.getDuration());
            }
        };
    }
}
