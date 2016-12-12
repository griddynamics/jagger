package com.griddynamics.jagger.engine.e1.collector.testsuite;

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
public abstract class TestSuiteListener {

    /** Method is executed before test suite starts
     * @param testSuiteInfo - describes start test suite information*/
    public void onStart(TestSuiteInfo testSuiteInfo){
    }

    /** Method is executed after test suite stops
     * @param testSuiteInfo - describes stop test suite information*/
    public void onStop(TestSuiteInfo testSuiteInfo){
    }

    /** Class is used by Jagger for sequential execution of several listeners @n
     *  Not required for custom test listeners */
    public static class Composer extends TestSuiteListener {
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private Iterable<TestSuiteListener> listeners;

        public Composer(Iterable<TestSuiteListener> listeners) {
            this.listeners = listeners;
        }

        public static TestSuiteListener compose(Iterable<TestSuiteListener> collectors){
            return new Composer(collectors);
        }

        @Override
        public void onStart(TestSuiteInfo testSuiteInfo) {
            for (TestSuiteListener listener : listeners){
                try{
                    listener.onStart(testSuiteInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on start in {} test-suite-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onStop(TestSuiteInfo testSuiteInfo) {
            for (TestSuiteListener listener : listeners){
                try{
                    listener.onStop(testSuiteInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on stop in {} test-suite-listener", listener.toString(), ex);
                }
            }
        }
    }
}