package com.griddynamics.jagger.engine.e1.collector.test;

/** Listener, that executes before test, after test and during all test
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 * */
public interface TestListener{

    /** Executes before test starts
     * @author Gribov Kirill
     * @n
     *
     * @param testInfo - describes start test information*/
    void onStart(TestInfo testInfo);

    /** Executes after test stops
     * @author Gribov Kirill
     * @n
     *
     * @param testInfo - describes stop test information */
    void onStop(TestInfo testInfo);

    /** This method is periodically called while test is running. It shows current Jagger execution status(number of Jagger threads, etc)
     * @author Gribov Kirill
     * @n
     * @param status - contains info about current number of threads, samples and etc.*/
    void onRun(TestInfo status);

    public static class Composer implements TestListener {

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
                listener.onStart(testInfo);
            }
        }

        @Override
        public void onStop(TestInfo testInfo) {
            for (TestListener listener : listeners){
                listener.onStop(testInfo);
            }
        }

        @Override
        public void onRun(TestInfo testInfo) {
            for (TestListener listener : listeners){
                listener.onRun(testInfo);
            }
        }
    }
}