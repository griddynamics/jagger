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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TerminateByTotalSamplingTest {
    private TerminateByTotalSampling configuration;
    private WorkloadExecutionStatus status;

    @BeforeMethod
    public void setUp() throws Exception {
        status = mock(WorkloadExecutionStatus.class);
        configuration = new TerminateByTotalSampling();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNegativeSamples() throws Exception {
        configuration.setSamples(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenZeroSamples() throws Exception {
        configuration.setSamples(0);
    }

    @Test
    public void shouldTerminateWhenSamplesAreEqualToConfiguredOne() throws Exception {
        configuration.setSamples(5);
        when(status.getTotalSamples()).thenReturn(5);
        when(status.getTotalStartedSamples()).thenReturn(5);

        TerminationStrategy terminateStrategy = configuration.getTerminateStrategy();
        assertTrue(terminateStrategy.isTerminationRequired(status));
    }

    @Test
    public void shouldTerminateWhenSamplesMoreThenConfiguredOne() throws Exception {
        configuration.setSamples(5);
        when(status.getTotalSamples()).thenReturn(6);
        when(status.getTotalStartedSamples()).thenReturn(6);

        TerminationStrategy terminateStrategy = configuration.getTerminateStrategy();
        assertTrue(terminateStrategy.isTerminationRequired(status));
    }

    @Test
    public void shouldNotTerminateWhenSamplesLessThenConfiguredOne() throws Exception {
        configuration.setSamples(5);
        when(status.getTotalSamples()).thenReturn(4);
        when(status.getTotalStartedSamples()).thenReturn(4);

        TerminationStrategy terminateStrategy = configuration.getTerminateStrategy();
        assertFalse(terminateStrategy.isTerminationRequired(status));
    }
}
