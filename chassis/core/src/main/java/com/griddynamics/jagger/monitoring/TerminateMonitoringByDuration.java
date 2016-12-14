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
package com.griddynamics.jagger.monitoring;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.util.JavaSystemClock;
import com.griddynamics.jagger.util.SystemClock;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

import static com.griddynamics.jagger.util.TimeUtils.secondsToMillis;

/**
 * Performs monitoring fixed amount of time.
 *
 * @author Mairbek Khadikov
 */
public class TerminateMonitoringByDuration implements MonitoringTerminationStrategyConfiguration {
    private SystemClock clock = new JavaSystemClock();
    private int seconds;

    @Override
    public MonitoringTerminationStrategy get() {
        long terminationTime = clock.currentTimeMillis() + secondsToMillis(seconds);
        return new Impl(clock, seconds, terminationTime);
    }

    @Required
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setClock(SystemClock clock) {
        this.clock = clock;
    }

    private static class Impl implements MonitoringTerminationStrategy {
        private final SystemClock clock;
        private final int seconds;
        private final long terminationTime;

        private Impl(SystemClock clock, int seconds, long terminationTime) {
            this.clock = clock;
            this.seconds = seconds;
            this.terminationTime = terminationTime;
        }

        @Override
        public boolean isTerminationRequired(Map<NodeId, MonitoringStatus> statuses) {
            long currentTime = clock.currentTimeMillis();

            return currentTime > terminationTime;
        }

        @Override
        public String toString() {
            return "Terminate after " + seconds +" seconds";
        }
    }

    @Override
    public String toString() {
        return "Terminate after " + seconds +" seconds";
    }

}
