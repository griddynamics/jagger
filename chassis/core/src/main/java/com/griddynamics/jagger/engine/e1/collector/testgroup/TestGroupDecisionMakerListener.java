package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.WorstCaseDecisionMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** Listener, executed after test-group execution.
  * @author Novozhilov Mark
  * @n
  * @par Details:
  * @details This type of listener is intended to make decision about test group execution status @n
  * It is executed when decision making based on limits is used. Details: @ref DecisionMakerBasedOnLimits @n
  * @n
  * @ingroup Main_Listeners_group */
public interface TestGroupDecisionMakerListener {
    /** Executes after test-group measured parameters are aggregated in the database, @n
     * intended metrics are compared to limits, decisions for all tests in test group are taken. @n
     * @param decisionMakerInfo - describes test-group, decisions for all tests in test groups, @n
     *                          limits and decisions for all metrics in tests
     * @return decision @ref com.griddynamics.jagger.util.Decision "Decision" for this test group, based on input info
     */
    Decision onDecisionMaking(TestGroupDecisionMakerInfo decisionMakerInfo);

    /** Class is used by Jagger for sequential execution of several listeners @n
     *  Not required for custom test-group decision maker listeners */
    public static class Composer implements TestGroupDecisionMakerListener{
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private List<TestGroupDecisionMakerListener> listenerList;

        private Composer(List<TestGroupDecisionMakerListener> listenerList){
            this.listenerList = listenerList;
        }

        @Override
        public Decision onDecisionMaking(TestGroupDecisionMakerInfo decisionMakerInfo) {
            List<Decision> decisions = new ArrayList<Decision>();

            WorstCaseDecisionMaker worstCaseDecisionMaker = new WorstCaseDecisionMaker();

            for (TestGroupDecisionMakerListener listener : listenerList){
                try{
                    decisions.add(listener.onDecisionMaking(decisionMakerInfo));
                }catch (RuntimeException ex){
                    log.error("Failed to call on decision making in {} test-group-decision-maker-listener", listener.toString(), ex);
                }
            }

            return worstCaseDecisionMaker.getDecision(decisions);
        }
        public static TestGroupDecisionMakerListener compose(List<TestGroupDecisionMakerListener> listeners){
            return new Composer(listeners);
        }
    }
}
