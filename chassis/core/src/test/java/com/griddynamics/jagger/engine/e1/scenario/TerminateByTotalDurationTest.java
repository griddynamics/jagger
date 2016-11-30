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

import com.griddynamics.jagger.util.SystemClock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TerminateByTotalDurationTest {
    private TerminateByDuration configuration;
    private WorkloadExecutionStatus status;
    private SystemClock systemClock;

    @BeforeMethod
    public void setUp() throws Exception {
        status = mock(WorkloadExecutionStatus.class);
        systemClock = mock(SystemClock.class);
        configuration = new TerminateByDuration();
        configuration.setSystemClock(systemClock);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNegativeDuration() throws Exception {
        configuration.setSeconds(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenZeroDuration() throws Exception {
        configuration.setSeconds(0);
    }

    @Test
    public void shouldTerminateWhenSamplesAreEqualToConfiguredOne() throws Exception {
        configuration.setSeconds(5);
        when(systemClock.currentTimeMillis()).thenReturn(0L, 5000L);

        TerminationStrategy terminateStrategy = configuration.getTerminateStrategy();
        assertTrue(terminateStrategy.isTerminationRequired(status));
    }

    @Test
    public void shouldTerminateWhenSamplesMoreThenConfiguredOne() throws Exception {
        configuration.setSeconds(5);
        when(systemClock.currentTimeMillis()).thenReturn(0L, 6000L);

        TerminationStrategy terminateStrategy = configuration.getTerminateStrategy();
        assertTrue(terminateStrategy.isTerminationRequired(status));
    }

    @Test
    public void shouldTerminateWhenSamplesLessThenConfiguredOne() throws Exception {
        configuration.setSeconds(5);
        when(systemClock.currentTimeMillis()).thenReturn(0L, 4000L);


        TerminationStrategy terminateStrategy = configuration.getTerminateStrategy();
        assertFalse(terminateStrategy.isTerminationRequired(status));
    }
}
