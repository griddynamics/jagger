package com.griddynamics.jagger.engine.e1.collector.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for listeners of
 * {@link com.griddynamics.jagger.engine.e1.collector.test.AbstractTestInfo} subclasses.
 */
public abstract class AbstractTestListener<T extends AbstractTestInfo> {
    
    public static <T extends AbstractTestListener<V>, V extends AbstractTestInfo> CompositeListener<T, V> compose(
            Iterable<T> collectors
    ) {
        return new CompositeListener<>(collectors);
    }
    
    /**
     * Method is executed before test starts
     *
     * @param testInfo - describes start test information
     */
    public void onStart(T testInfo) {}
    
    /**
     * This method is periodically called while test is running. It shows current Jagger execution status(number of Jagger threads, etc)
     *
     * @param testInfo - contains info about current number of threads, samples and etc.
     */
    public void onRun(T testInfo) {}
    
    /**
     * Executes after test stops
     *
     * @param testInfo - describes stop test information
     */
    public void onStop(T testInfo) {}
    
    /**
     * Executed in case of test execution failure
     *
     * @param testInfo - describes failed test information
     */
    public void onFailure(T testInfo) {
        onStop(testInfo);
    }
    
    public static class CompositeListener<T extends AbstractTestListener<V>, V extends AbstractTestInfo>
            extends AbstractTestListener<V> {
        
        private static Logger log = LoggerFactory.getLogger(CompositeListener.class);
        private final Iterable<T> listeners;
        
        public CompositeListener(Iterable<T> listeners) {
            this.listeners = listeners;
        }
        
        @Override
        public void onStart(V testInfo) {
            testInfo.onStart(testInfo);
            for (T listener : listeners) {
                try {
                    listener.onStart(testInfo);
                } catch (RuntimeException ex) {
                    log.error("Failed to call on start in {} test-listener", listener.toString(), ex);
                }
            }
        }
        
        @Override
        public void onRun(V testInfo) {
            testInfo.onRun(testInfo);
            for (T listener : listeners) {
                try {
                    listener.onRun(testInfo);
                } catch (RuntimeException ex) {
                    log.error("Failed to call on run in {} test-listener", listener.toString(), ex);
                }
            }
        }
        
        @Override
        public void onStop(V testInfo) {
            testInfo.onStop(testInfo);
            for (T listener : listeners) {
                try {
                    listener.onStop(testInfo);
                } catch (RuntimeException ex) {
                    log.error("Failed to call on stop in {} test-listener", listener.toString(), ex);
                }
            }
        }
        
        @Override
        public void onFailure(V testInfo) {
            testInfo.onFailure(testInfo);
            for (T listener : listeners) {
                try {
                    listener.onFailure(testInfo);
                } catch (RuntimeException ex) {
                    log.error("Failed to call on stop in {} test-listener", listener.toString(), ex);
                }
            }
        }
    }
}
