package com.griddynamics.jagger.engine.e1.collector.testgroup;

import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.WorstCaseDecisionMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** Listener, executed on decision-making for a test-group
    * @author Novozhilov Mark
    * @n
    * @par Details:
    * @details
    * @n
    * @ingroup Main_Listeners_Base_group */

public interface TestGroupDecisionMakerListener {

    /** Executes after test-group information aggregates in the database.
     * @param decisionMakerInfo - describes test-group information */
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
