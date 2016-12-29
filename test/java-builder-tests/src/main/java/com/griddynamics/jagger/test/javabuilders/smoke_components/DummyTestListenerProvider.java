package com.griddynamics.jagger.test.javabuilders.smoke_components;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfo;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;

/**
 * Listener to check creation of JLoadTest with custom listener.
 */
public class DummyTestListenerProvider implements Provider<TestListener> {

    @Override
    public TestListener provide() {
        return new TestListener() {
            @Override
            public void onRun(TestInfo status) {
                super.onRun(status);
            }
        };
    }
}
