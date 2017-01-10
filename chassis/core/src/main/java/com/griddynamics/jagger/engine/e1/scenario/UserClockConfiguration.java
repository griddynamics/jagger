/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
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
        if (taskConfig.getTps() != null){
            TpsClockConfiguration tpsClockConfiguration = new TpsClockConfiguration();
            tpsClockConfiguration.setTickInterval(tickInterval);
            tpsClockConfiguration.setTps(taskConfig.getTps().getValue());
            return tpsClockConfiguration.getClock();
        }else
        if (taskConfig.getVirtualUser() != null){
            VirtualUsersClockConfiguration conf = new VirtualUsersClockConfiguration();
            conf.setTickInterval(taskConfig.getVirtualUser().getTickInterval());
            conf.setUsers(taskConfig.getVirtualUser().getCount());
            return conf.getClock();
        }else
        if (taskConfig.getInvocation() != null){
            ExactInvocationsClockConfiguration conf = new ExactInvocationsClockConfiguration();
            conf.setExactcount(taskConfig.getInvocation().getExactcount());
            conf.setThreads(taskConfig.getInvocation().getThreads());
            conf.setDelay(taskConfig.getDelay());
            return conf.getClock();
        }else
        if (taskConfig.getUsers() != null && taskConfig.getUsers().size()>0) {
                return new UserClock(taskConfig.getUsers(), taskConfig.getDelay(), tickInterval, shutdown);
        }else return null;
    }

    @Override
    public String toString() {
        return "virtual userGroups with " + delay + " delay";
    }
}
