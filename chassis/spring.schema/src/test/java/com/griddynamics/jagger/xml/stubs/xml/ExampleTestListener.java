package com.griddynamics.jagger.xml.stubs.xml;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfo;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;

/**
 * @author kgribov
 */
public class ExampleTestListener implements Provider<TestListener> {

    @Override
    public TestListener provide() {
        return new TestListener() {
            @Override
            public void onStart(TestInfo infoStart) {

            }

            @Override
            public void onStop(TestInfo infoStop) {

            }
        };
    }
}
