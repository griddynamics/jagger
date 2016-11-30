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

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.util.JavaSystemClock;
import com.griddynamics.jagger.util.SystemClock;
import com.griddynamics.jagger.util.TimeUtils;

public class TerminateByDuration implements TerminateStrategyConfiguration {
    private int seconds;
    private SystemClock systemClock = new JavaSystemClock();

    public void setSeconds(int seconds) {
        Preconditions.checkArgument(seconds > 0, "Seconds should be > 0");
        this.seconds = seconds;
    }

    @Override
    public TerminationStrategy getTerminateStrategy() {
        long currentTime = systemClock.currentTimeMillis();
        long stopTime = currentTime + TimeUtils.secondsToMillis(seconds);

        return new TerminateByDurationStrategy(stopTime, systemClock);
    }

    public void setSystemClock(SystemClock systemClock) {
        this.systemClock = systemClock;
    }

    private static class TerminateByDurationStrategy implements TerminationStrategy {
        private final long stopTime;
        private final SystemClock systemClock;

        public TerminateByDurationStrategy(long stopTime, SystemClock systemClock) {
            this.stopTime = stopTime;
            this.systemClock = systemClock;
        }

        @Override
        public boolean isTerminationRequired(WorkloadExecutionStatus status) {
            long currentTime = systemClock.currentTimeMillis();

            return currentTime >= stopTime;
        }
    }

    @Override
    public String toString() {
        return "Terminate after " + seconds +" seconds";
    }
}
