package com.griddynamics.jagger.engine.e1.collector.testgroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Listener, executed before and after test-group execution.
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 * @ingroup Main_Listeners_Base_group
 */
public abstract class TestGroupListener {

    /** Executes before test-group starts
     * @param infoStart - describes test-group start information */
    public void onStart(TestGroupInfo infoStart){
    }

    /** Executes after test-group stops
     * @param infoStop - describes test-group stop information */
    public void onStop(TestGroupInfo infoStop){
    }

    /** Class is used by Jagger for sequential execution of several listeners @n
     *  Not required for custom test listeners */
    public static class Composer extends TestGroupListener{
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private List<TestGroupListener> listenerList;

        private Composer(List<TestGroupListener> listenerList){
            this.listenerList = listenerList;
        }

        @Override
        public void onStart(TestGroupInfo testGroupInfo) {
            for (TestGroupListener listener : listenerList){
                try{
                    listener.onStart(testGroupInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on start in {} test-group-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onStop(TestGroupInfo testGroupInfo) {
            for (TestGroupListener listener : listenerList){
                try{
                    listener.onStop(testGroupInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on stop in {} test-group-listener", listener.toString(), ex);
                }
            }
        }

        public static TestGroupListener compose(List<TestGroupListener> listeners){
            return new Composer(listeners);
        }
    }
}
