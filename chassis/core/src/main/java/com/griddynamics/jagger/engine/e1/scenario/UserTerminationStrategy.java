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

import com.griddynamics.jagger.user.ProcessingConfig;
import com.griddynamics.jagger.util.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/** Terminate workload by time or when exact number of samples is reached
 * @author dkotlyarov
 * @n
 * @par Details:
 * @details Can work in two ways - terminate workload by time or terminate workload by number of samples. If duration doesn't equal -1, workload will terminate by time.
 *
 * @ingroup Main_Terminators_group */
public class UserTerminationStrategy implements TerminationStrategy {
    private final long stopTime;
    private final int stopSampleCount;
    private final AtomicBoolean shutdown;

    private static Logger log = LoggerFactory.getLogger(UserTerminationStrategy.class);

    public UserTerminationStrategy(ProcessingConfig.Test testConfig, ProcessingConfig.Test.Task taskConfig, AtomicBoolean shutdown) {
        this.stopTime = (taskConfig.getDuration() == null) ?
                            (testConfig.getDuration() == null ? -1L : System.currentTimeMillis() + Parser.parseTimeMillis(testConfig.getDuration())):
                            System.currentTimeMillis() + Parser.parseTimeMillis(taskConfig.getDuration());
        this.stopSampleCount = calculateStopSamplesCount(taskConfig);
        this.shutdown = shutdown;
    }

    public UserTerminationStrategy(String duration, Integer samples, AtomicBoolean shutdown) {
        this.stopTime = duration == null ? -1
                : (System.currentTimeMillis() + Parser.parseTimeMillis(duration));
        this.stopSampleCount = samples;
        this.shutdown = shutdown;
    }

    public static int calculateStopSamplesCount(ProcessingConfig.Test.Task taskConfig) {
        if (taskConfig.getInvocation() != null) {
            return taskConfig.getInvocation().getExactcount();
        } else {
            return taskConfig.getSample();
        }
    }
    /** Terminate workload by time or samples
     * @author dkotlyarov
     * @n
     * @par Details:
     * @details If duration doesn't equal -1, workload will terminate by time.
     *
     * @param status - current jagger execution status. Contains such info - number of threads, finished samples, current samples.
     *
     * @return true if termination is required */
    @Override
    public boolean isTerminationRequired(WorkloadExecutionStatus status) {
        if (stopTime != -1) {
            if (System.currentTimeMillis() >= stopTime) {
                log.info("Request to terminate work. Max duration reached");
                return true;
            }
        }
        if (stopSampleCount != -1) {
            if (status.getTotalSamples() >= stopSampleCount) {
                log.info("Request to terminate work. Number of samples reached");
                return true;
            }
        }
        return shutdown.get();
    }
}
