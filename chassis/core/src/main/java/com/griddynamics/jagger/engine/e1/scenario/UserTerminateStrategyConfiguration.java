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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: dkotlyarov
 */
public class UserTerminateStrategyConfiguration implements TerminateStrategyConfiguration {
    private final ProcessingConfig.Test testConfig;
    private final ProcessingConfig.Test.Task taskConfig;
    private final AtomicBoolean shutdown;

    public UserTerminateStrategyConfiguration(ProcessingConfig.Test testConfig, ProcessingConfig.Test.Task taskConfig, AtomicBoolean shutdown) {
        this.testConfig = testConfig;
        this.taskConfig = taskConfig;
        this.shutdown = shutdown;
    }

    @Override
    public TerminationStrategy getTerminateStrategy() {
        return new UserTerminationStrategy(testConfig, taskConfig, shutdown);
    }

    @Override
    public String toString() {
        String result = "";
        if (taskConfig.getDuration() != null) {
            result += taskConfig.getDuration();
        }
        int samples = UserTerminationStrategy.calculateStopSamplesCount(taskConfig);
        if (samples != -1) {
            if (!result.isEmpty()) {
                result += "; ";
            }
            result += samples + " samples";
        }
        return result;
    }
}
