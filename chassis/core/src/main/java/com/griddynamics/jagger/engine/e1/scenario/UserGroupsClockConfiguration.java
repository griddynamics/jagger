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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * UserGroup: dkotlyarov
 */
public class UserGroupsClockConfiguration implements WorkloadClockConfiguration {
    private static final Logger log = LoggerFactory.getLogger(UserGroupsClockConfiguration.class);

    private int tickInterval;
    private InvocationDelayConfiguration delay = FixedDelay.noDelay();
    private AtomicBoolean shutdown;
    private List<ProcessingConfig.Test.Task.User> users;

    public UserGroupsClockConfiguration() {
    }

    public void setShutdown(AtomicBoolean shutdown) {
        this.shutdown = shutdown;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    public void setUsers(List<ProcessingConfig.Test.Task.User> users) {
        this.users = users;
    }

    public void setDelay(InvocationDelayConfiguration delay) {
        this.delay = delay;
    }

    public List<ProcessingConfig.Test.Task.User> getUsers(){
        return users;
    }

    public UserGroupsClockConfiguration(int tickInterval, List<ProcessingConfig.Test.Task.User> users, AtomicBoolean shutdown) {
        this.tickInterval = tickInterval;
        this.users = users;
        this.shutdown = shutdown;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public InvocationDelayConfiguration getDelay() {
        return delay;
    }

    @Override
    public WorkloadClock getClock() {
        return new UserClock(users, delay.getInvocationDelay().getValue(), tickInterval, shutdown);
    }

    @Override
    public String toString() {
        return "virtual userGroups with " + delay + " delay";
    }
}
