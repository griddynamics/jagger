package com.griddynamics.jagger.engine.e1.process;

import com.griddynamics.jagger.coordinator.NodeProcess;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadConfiguration;

/**
 * Interface to interact with workload process
 */
public interface WorkloadProcess extends NodeProcess<WorkloadStatus> {

    public void changeConfiguration(WorkloadConfiguration configuration);
}
