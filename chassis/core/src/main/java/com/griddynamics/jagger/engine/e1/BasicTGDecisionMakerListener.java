package com.griddynamics.jagger.engine.e1;

import com.griddynamics.jagger.engine.e1.collector.limits.DecisionPerTest;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerInfo;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupDecisionMakerListener;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.WorstCaseDecisionMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** Listener, after test group to make decision about test group execution status
 * @author Dmitry Latnikov
 * @n
 * @par Details:
 * @details It is executed when decision making based on limits is used. Details: @ref DecisionMakerBasedOnLimits @n
 * It is used by default as soon as limit set is attached to test
 *
 */
public class BasicTGDecisionMakerListener extends ServicesAware implements Provider<TestGroupDecisionMakerListener> {
    private static final Logger log = LoggerFactory.getLogger(BasicTGDecisionMakerListener.class);

    /** Method is executed single time when listener is created */
    @Override
    protected void init() {

    }

    /** Method is providing listener to Jagger that will make decision for test group */
    @Override
    public TestGroupDecisionMakerListener provide() {
        return new TestGroupDecisionMakerListener() {

            /** Returns worst decision from list of decisions per test from input parameter */
            @Override
            public Decision onDecisionMaking(TestGroupDecisionMakerInfo decisionMakerInfo) {
                Decision decisionPerTestGroup;
                WorstCaseDecisionMaker worstCaseDecisionMaker = new WorstCaseDecisionMaker();
                List<Decision> decisions = new ArrayList<Decision>();

                for (DecisionPerTest decisionPerTest : decisionMakerInfo.getDecisionsPerTest()) {
                    decisions.add(decisionPerTest.getDecisionPerTest());
                }

                decisionPerTestGroup = worstCaseDecisionMaker.getDecision(decisions);

                log.debug("\nDecision for test group {} - {}",decisionMakerInfo.getTestGroup().getTaskName(),decisionPerTestGroup);

                return decisionPerTestGroup;
            }
        };
    }

}

/* **************** Decision maker page ************************* */
/// @defgroup Main_Decision_Maker_General_group Decision Maker main page
///
///
/// @li Approach to make decision: @ref DecisionMakerBasedOnLimits
/// @n
/// @n
/// @details
/// @par General info
/// Decision maker is required to provide result of performance test execution. It decides if measured parameters @n
/// meet acceptance criteria or not. Result provided by decision maker can be used by CI tool to rise alert when @n
/// measured results are below expectations. @n
