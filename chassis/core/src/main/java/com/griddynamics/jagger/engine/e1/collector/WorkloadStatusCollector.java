package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.Flushable;
import com.griddynamics.jagger.engine.e1.scenario.NodeSideInitializable;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadExecutionStatus;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/10/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface WorkloadStatusCollector extends NodeSideInitializable, Flushable{

    void collect(WorkloadExecutionStatus status);

    public static class Composer implements WorkloadStatusCollector{

        private Iterable<WorkloadStatusCollector> collectors;

        public Composer(Iterable<WorkloadStatusCollector> collectors) {
            this.collectors = collectors;
        }

        public static WorkloadStatusCollector compose(Iterable<WorkloadStatusCollector> collectors){
            return new Composer(collectors);
        }

        @Override
        public void collect(WorkloadExecutionStatus status) {
            for (WorkloadStatusCollector collector : collectors){
                collector.collect(status);
            }
        }

        @Override
        public void flush() {
            for (WorkloadStatusCollector collector : collectors){
                collector.flush();
            }
        }

        @Override
        public void init(String sessionId, String taskId, NodeContext nodeContext) {
            for (WorkloadStatusCollector collector : collectors){
                collector.init(sessionId, taskId, nodeContext);
            }
        }
    }
}