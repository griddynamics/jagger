package com.griddynamics.jagger.engine.e1.collector.testgroup;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 11/8/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TestGroupListener {
    void onStart(TestGroupInfoStart infoStart);
    void onStop(TestGroupInfoStop infoStop);

    public static class Composer implements TestGroupListener{
        private List<TestGroupListener> listenerList;

        private Composer(List<TestGroupListener> listenerList){
            this.listenerList = listenerList;
        }

        @Override
        public void onStart(TestGroupInfoStart infoStart) {
            for (TestGroupListener listener : listenerList){
                listener.onStart(infoStart);
            }
        }

        @Override
        public void onStop(TestGroupInfoStop infoStop) {
            for (TestGroupListener listener : listenerList){
                listener.onStop(infoStop);
            }
        }

        public static TestGroupListener compose(List<TestGroupListener> listeners){
            return new Composer(listeners);
        }
    }
}
