package com.griddynamics.jagger.engine.e1.collector.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener, executed before and after a test and periodically during a test
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details Possible applications for test listener: @n
 * @li Collect some parameters during test run and save as metrics
 * @li Get some internal metrics from SUT after test is over and store this data as metrics to Jagger DB
 *
 * @n
 * @ingroup Main_Listeners_group */
public abstract class TestListener {

    /** Method is executed before test starts
     * @param testInfo - describes start test information*/
    public void onStart(TestInfo testInfo){
    }

    /** Executes after test stops
     * @param testInfo - describes stop test information */
    public void onStop(TestInfo testInfo){
    }

    /** This method is periodically called while test is running. It shows current Jagger execution status(number of Jagger threads, etc)
     * @param status - contains info about current number of threads, samples and etc.*/
    public void onRun(TestInfo status){
    }

    /** Class is used by Jagger for sequential execution of several listeners @n
     *  Not required for custom test listeners */
    public static class Composer extends TestListener {
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private Iterable<TestListener> listeners;

        public Composer(Iterable<TestListener> listeners) {
            this.listeners = listeners;
        }

        public static TestListener compose(Iterable<TestListener> collectors){
            return new Composer(collectors);
        }

        @Override
        public void onStart(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onStart(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on start in {} test-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onStop(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onStop(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on stop in {} test-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onRun(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onRun(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on run in {} test-listener", listener.toString(), ex);
                }
            }
        }
    }
}
