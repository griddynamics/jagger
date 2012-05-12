/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Service;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.engine.e1.process.PollWorkloadProcessStatus;
import com.griddynamics.jagger.engine.e1.process.StartWorkloadProcess;
import com.griddynamics.jagger.engine.e1.process.StopWorkloadProcess;
import com.griddynamics.jagger.master.AbstractDistributionService;
import com.griddynamics.jagger.master.AbstractDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.griddynamics.jagger.util.TimeUtils.sleepMillis;

public class WorkloadTaskDistributor extends AbstractDistributor<WorkloadTask> {
    private static Logger log = LoggerFactory.getLogger(WorkloadTaskDistributor.class);

    private long logInterval;

    @Override
    public Set<Qualifier<?>> getQualifiers() {
        Set<Qualifier<?>> result = Sets.newHashSet();

        result.add(Qualifier.of(StartWorkloadProcess.class));
        result.add(Qualifier.of(StopWorkloadProcess.class));
        result.add(Qualifier.of(PollWorkloadProcessStatus.class));

        return result;
    }

    @Override
    protected Service performDistribution(final ExecutorService executor, final String sessionId, final String taskId, final WorkloadTask task,
                                          final Map<NodeId, RemoteExecutor> remotes, final Multimap<NodeType, NodeId> availableNodes,
                                          final Coordinator coordinator) {

        return new AbstractDistributionService(executor) {
            @Override
            protected void run() throws Exception {
                log.info("Going to distribute workload task {}", task);

                log.debug("Going to do calibration");
                Calibrator calibrator = task.getCalibrator();
                calibrator.calibrate(sessionId, taskId, task.getScenarioFactory(), remotes);
                log.debug("Calibrator completed");

                DefaultWorkloadController controller = new DefaultWorkloadController(sessionId, taskId, task, remotes);

                WorkloadClock clock = task.getClock();
                TerminationStrategy terminationStrategy = task.getTerminationStrategy();

                log.debug("Going to start workload");
                controller.startWorkload(clock.getPoolSizes(controller.getNodes()));
                log.debug("Workload started");

                int sleepInterval = clock.getTickInterval();
                long multiplicity = logInterval / sleepInterval;
                long countIntervals = 0;

                while (true) {
                    if (!isRunning()) {
                        log.debug("Going to terminate work. Requested from outside");
                        break;
                    }

                    WorkloadExecutionStatus status = controller.getStatus();

                    if (terminationStrategy.isTerminationRequired(status)) {
                        log.debug("Going to terminate work. According to termination strategy");
                        break;
                    }

                    clock.tick(status, controller);
                    if (--countIntervals <= 0) {
                        log.info("Status of execution {}", status);
                        countIntervals = multiplicity;
                    }

                    log.debug("Clock should continue. Going to sleep {} seconds", sleepInterval);
                    sleepMillis(sleepInterval);

                }

                log.debug("Going to stop workload");
                controller.stopWorkload();
                log.debug("Workload stopped");

            }

            @Override
            public String toString() {
                return WorkloadTask.class.getName() + " distributor";
            }
        };
    }

    public void setLogInterval(long logInterval) {
        this.logInterval = logInterval;
    }

}
