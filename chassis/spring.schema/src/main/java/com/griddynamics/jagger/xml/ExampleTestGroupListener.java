package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupInfoStart;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupInfoStop;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupListener;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 11/8/13
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExampleTestGroupListener implements Provider<TestGroupListener> {
    @Override
    public TestGroupListener provide() {
        return new TestGroupListener() {
            @Override
            public void onStart(TestGroupInfoStart infoStart) {

            }

            @Override
            public void onStop(TestGroupInfoStop infoStop) {

            }
        };
    }
}
