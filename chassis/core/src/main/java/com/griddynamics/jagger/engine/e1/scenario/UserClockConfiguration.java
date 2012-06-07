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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * UserGroup: dkotlyarov
 */
public class UserClockConfiguration implements WorkloadClockConfiguration {
    private static final Logger log = LoggerFactory.getLogger(UserClockConfiguration.class);

    private final int tickInterval;
    private final InvocationDelayConfiguration delay = FixedDelay.noDelay();
    private final ProcessingConfig.Test.Task taskConfig;
    private final AtomicBoolean shutdown;

    public UserClockConfiguration(int tickInterval, ProcessingConfig.Test.Task taskConfig, AtomicBoolean shutdown) {
        this.tickInterval = tickInterval;
        this.taskConfig = taskConfig;
        this.shutdown = shutdown;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public InvocationDelayConfiguration getDelay() {
        return delay;
    }

    public ProcessingConfig.Test.Task getTaskConfig() {
        return taskConfig;
    }

    @Override
    public WorkloadClock getClock() {
        if (taskConfig.invocation == null) {
            return new UserClock(taskConfig, tickInterval, shutdown);
        } else {
            return new ExactInvocationsClock(taskConfig.invocation.count, taskConfig.invocation.threads, taskConfig.delay);
        }
    }

    @Override
    public String toString() {
        return "virtual userGroups with " + delay + " delay";
    }
}
