package com.griddynamics.jagger.engine.e1;

import com.griddynamics.jagger.engine.e1.collector.limits.DecisionPerTest;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerInfo;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.WorstCaseDecisionMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

//todo ??? JFG-744 docu for decision making with use of limits
public class BasicTGDecisionMakerListener extends ServicesAware implements Provider<TestGroupDecisionMakerListener> {
    private static final Logger log = LoggerFactory.getLogger(BasicTGDecisionMakerListener.class);

    @Override
    protected void init() {

    }

    @Override
    public TestGroupDecisionMakerListener provide() {
        return new TestGroupDecisionMakerListener() {
            @Override
            public Decision onDecisionMaking(TestGroupDecisionMakerInfo decisionMakerInfo) {
                Decision decisionPerTestGroup;
                WorstCaseDecisionMaker worstCaseDecisionMaker = new WorstCaseDecisionMaker();
                List<Decision> decisions = new ArrayList<Decision>();

                for (DecisionPerTest decisionPerTest : decisionMakerInfo.getDecisionsPerTest()) {
                    decisions.add(decisionPerTest.getDecisionPerTest());
                }

                decisionPerTestGroup = worstCaseDecisionMaker.getDecision(decisions);

                log.info("Decision for test group {} - {}",decisionMakerInfo.getTestGroup().getTaskName(),decisionPerTestGroup);

                return decisionPerTestGroup;
            }
        };
    }

}
