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
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Presents workload configuration on kernel side.
 *
 * @author Mairbek Khadikov
 */
public class WorkloadConfiguration implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(WorkloadConfiguration.class);

    private final int threads;
    private final int delay;
    private final int samples;
    private final long period;

    public static WorkloadConfiguration with(int threads, int delay) {
        return with(threads, delay, -1);
    }

    public static WorkloadConfiguration with(int threads, int delay, int samples) {
        return with(threads, delay, samples, -1L);
    }

    public static WorkloadConfiguration with(long period, int maxThreads) {
        return with(maxThreads, 0, -1, period);
    }

    public static WorkloadConfiguration with(int threads, int delay, int samples, long period) {
        return new WorkloadConfiguration(threads, delay, samples, period);
    }

    public static WorkloadConfiguration zero() {
        return with(0, 0);
    }

    private WorkloadConfiguration(int threads, int delay, int samples, long period) {
        try {
            Preconditions.checkArgument(threads >= 0);
            Preconditions.checkArgument(delay   >= 0);
        }
        catch (IllegalArgumentException e) {
            log.error("Unsupported configuration. threads=" + threads + ", delay=" + delay);
            throw Throwables.propagate(e);
        }

        this.threads = threads;
        this.delay   = delay;
        this.samples = samples;
        this.period = period;
    }

    public int getThreads() {
        return threads;
    }

    public int getDelay() {
        return delay;
    }

    public int getSamples() {
        return samples;
    }

    public long getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkloadConfiguration that = (WorkloadConfiguration) o;

        if (delay != that.delay) return false;
        if (samples != that.samples) return false;
        if (threads != that.threads) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = threads;
        result = 31 * result + delay;
        result = 31 * result + samples;
        return result;
    }

    @Override
    public String toString() {
        return "WorkloadConfiguration{" +
                "threads=" + threads +
                ", delay=" + delay +
                ", samples=" + samples +
                ", period=" + period +
                '}';
    }
}
