package com.griddynamics.jagger.xml.stubs.xml;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupInfo;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupListener;

/**
 * User: kgribov
 * Date: 11/8/13
 * Time: 6:37 PM
 */
public class ExampleTestGroupListener implements Provider<TestGroupListener> {
    int testValue = 11;
    @Override
    public TestGroupListener provide() {
        return new TestGroupListener() {
            @Override
            public void onStart(TestGroupInfo infoStart) {

            }

            @Override
            public void onStop(TestGroupInfo infoStop) {

            }
        };
    }
    public int getTestValue() {
        return testValue;
    }
}
