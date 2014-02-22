package com.griddynamics.jagger.xml.stubs.xml;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.sessioncomparation.DecisionMakerInfo;
import com.griddynamics.jagger.engine.e1.sessioncomparation.TestGroupDecisionMakerListener;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 2/13/14
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */

public class ExampleDecisionMakerListener extends ServicesAware implements Provider<TestGroupDecisionMakerListener> {
    int testValue = 15;

    @Override
    public TestGroupDecisionMakerListener provide() {
        return new TestGroupDecisionMakerListener() {

            @Override
            public void onDecisionMaking(DecisionMakerInfo decisionMakerInfo) {

            }

        };
    }
    public int getTestValue() {
        return testValue;
    }

}
