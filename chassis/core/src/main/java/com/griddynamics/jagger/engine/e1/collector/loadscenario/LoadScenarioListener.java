package com.griddynamics.jagger.engine.e1.collector.loadscenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener, executed before and after test suite
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details Possible applications for test suite listener: @n
 * @li Check some setup before executing of test suite (f.e. via properties)
 * @li Provide smoke tests before test suite
 * @li Add some resume to session comment after test suite is over
 *
 * @n
 * @ingroup Main_Listeners_Base_group
 */
public abstract class LoadScenarioListener {

    /** Method is executed before test suite starts
     * @param loadScenarioInfo - describes start test suite information*/
    public void onStart(LoadScenarioInfo loadScenarioInfo){
    }

    /** Method is executed after test suite stops
     * @param loadScenarioInfo - describes stop test suite information*/
    public void onStop(LoadScenarioInfo loadScenarioInfo){
    }

    /** Class is used by Jagger for sequential execution of several listeners @n
     *  Not required for custom test listeners */
    public static class Composer extends LoadScenarioListener {
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private Iterable<LoadScenarioListener> listeners;

        public Composer(Iterable<LoadScenarioListener> listeners) {
            this.listeners = listeners;
        }

        public static LoadScenarioListener compose(Iterable<LoadScenarioListener> collectors){
            return new Composer(collectors);
        }

        @Override
        public void onStart(LoadScenarioInfo loadScenarioInfo) {
            for (LoadScenarioListener listener : listeners){
                try{
                    listener.onStart(loadScenarioInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on start in {} test-suite-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onStop(LoadScenarioInfo loadScenarioInfo) {
            for (LoadScenarioListener listener : listeners){
                try{
                    listener.onStop(loadScenarioInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on stop in {} test-suite-listener", listener.toString(), ex);
                }
            }
        }
    }
}