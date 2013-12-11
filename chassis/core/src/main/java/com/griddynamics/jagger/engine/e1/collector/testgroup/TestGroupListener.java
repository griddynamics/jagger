package com.griddynamics.jagger.engine.e1.collector.testgroup;

import java.util.List;

/** Listener, that executes before and after test-group execution.
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 *
 * @ingroup */
public interface TestGroupListener {

    /** Executes before test-group starts
     * @author Gribov Kirill
     * @n
     *
     * @param infoStart - describes test-group start information */
    void onStart(TestGroupInfo infoStart);

    /** Executes after test-group stops
     * @author Gribov Kirill
     * @n
     *
     * @param infoStop - describes test-group stop information */
    void onStop(TestGroupInfo infoStop);

    public static class Composer implements TestGroupListener{
        private List<TestGroupListener> listenerList;

        private Composer(List<TestGroupListener> listenerList){
            this.listenerList = listenerList;
        }

        @Override
        public void onStart(TestGroupInfo infoStart) {
            for (TestGroupListener listener : listenerList){
                listener.onStart(infoStart);
            }
        }

        @Override
        public void onStop(TestGroupInfo infoStop) {
            for (TestGroupListener listener : listenerList){
                listener.onStop(infoStop);
            }
        }

        public static TestGroupListener compose(List<TestGroupListener> listeners){
            return new Composer(listeners);
        }
    }
}
