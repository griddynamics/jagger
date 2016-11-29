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

package com.griddynamics.jagger.agent;

import com.griddynamics.jagger.diagnostics.thread.sampling.SamplingProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

import static com.griddynamics.jagger.agent.model.ManageCollectionProfileFromSuT.ManageHotSpotMethodsFromSuT;

/**
 * @author Alexey Kiselyov
 *         Date: 25.08.11
 */
public class Profiler {
    private static final Logger log = LoggerFactory.getLogger(Profiler.class);

    public static final String POLL_INTERVAL = "pollInterval";
    public static final long DEFAULT_POLL_INTERVAL = 2000L;

    private SamplingProfiler samplingProfiler;

    public Profiler() {
    }

    public void startPolling() throws InterruptedException {
        this.samplingProfiler.startPolling();
    }

    public void stopPolling() {
        this.samplingProfiler.stopPolling();
    }

    private void setPollingInterval(long pollingInterval) {
        this.samplingProfiler.setPollingInterval(pollingInterval);
    }

    public void manageRuntimeGraphsCollection(ManageHotSpotMethodsFromSuT action, Map<String, Object> parameters)
            throws Exception {
        switch (action) {
            case START_POLLING:
                setPollingInterval(parameters.containsKey(POLL_INTERVAL) ?
                        Long.parseLong(parameters.get(POLL_INTERVAL).toString()) :
                        DEFAULT_POLL_INTERVAL);
                startPolling();
                break;
            case STOP_POLLING:
                stopPolling();
                break;
        }
    }

    public SamplingProfiler getSamplingProfiler() {
        return this.samplingProfiler;
    }

    @Required
    public void setSamplingProfiler(SamplingProfiler samplingProfiler) {
        this.samplingProfiler = samplingProfiler;
    }
}
