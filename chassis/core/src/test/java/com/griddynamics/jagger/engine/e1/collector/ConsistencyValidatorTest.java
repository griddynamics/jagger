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

package com.griddynamics.jagger.engine.e1.collector;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.scenario.CalibrationInfo;
import com.griddynamics.jagger.storage.fs.logging.LogReader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class ConsistencyValidatorTest {
    private ConsistencyValidator<Integer, Integer, Integer> consistencyValidator;
    private String sessionId;
    private String taskId;
    private NodeId nodeId;
    private LogReader logReader;
    private EquivalenceAdapter<Integer> queryEquivalence;
    private EquivalenceAdapter<Integer> endpointEquivalence;
    private EquivalenceAdapter<Integer> resultEquivalence;

    @BeforeMethod
    public void setUp() throws Exception {
        sessionId = "1";
        taskId = "task";
        nodeId = NodeId.kernelNode("1");
        logReader = mock(LogReader.class);
        queryEquivalence = mock(EquivalenceAdapter.class);
        endpointEquivalence = mock(EquivalenceAdapter.class);
        resultEquivalence = mock(EquivalenceAdapter.class);

        NodeContext kernelContext = mock(NodeContext.class);
        consistencyValidator = new ConsistencyValidator<Integer, Integer, Integer>(taskId, kernelContext, sessionId, queryEquivalence, endpointEquivalence, resultEquivalence);

        when(kernelContext.getService(LogReader.class)).thenReturn(logReader);
        when(kernelContext.getId()).thenReturn(nodeId);
    }

    @Test
    public void shouldValidateCorrectly() throws Exception {
        currentCalibrationInfo(CalibrationInfo.create(1111, 2222, 3333));
        when(queryEquivalence.doEquivalent(1111, 1111)).thenReturn(true);
        when(endpointEquivalence.doEquivalent(2222, 2222)).thenReturn(true);
        when(resultEquivalence.doEquivalent(3333, 3333)).thenReturn(true);

        boolean validate = consistencyValidator.validate(1111, 2222, 3333, 10L);

        assertThat(validate, is(true));
        verify(queryEquivalence).doEquivalent(1111, 1111);
        verify(endpointEquivalence).doEquivalent(2222, 2222);
        verify(resultEquivalence).doEquivalent(3333, 3333);
    }

    @Test
    public void shouldFailBecauseResultDoesNotMatch() throws Exception {
        currentCalibrationInfo(CalibrationInfo.create(1111, 2222, 3333));
        when(queryEquivalence.doEquivalent(1111, 1111)).thenReturn(true);
        when(endpointEquivalence.doEquivalent(2222, 2222)).thenReturn(true);
        when(resultEquivalence.doEquivalent(3333, 3333)).thenReturn(false);

        boolean validate = consistencyValidator.validate(1111, 2222, 3333, 10L);

        assertThat(validate, is(false));
        verify(queryEquivalence).doEquivalent(1111, 1111);
        verify(endpointEquivalence).doEquivalent(2222, 2222);
        verify(resultEquivalence).doEquivalent(3333, 3333);
    }

    @Test
    public void shouldFailBecauseQueryDoesNotMatch() throws Exception {
        currentCalibrationInfo(CalibrationInfo.create(1111, 2222, 3333));
        when(queryEquivalence.doEquivalent(1111, 1111)).thenReturn(false);
        when(endpointEquivalence.doEquivalent(2222, 2222)).thenReturn(true);

        boolean validate = consistencyValidator.validate(1111, 2222, 3333, 10L);

        assertThat(validate, is(false));
        verify(queryEquivalence).doEquivalent(1111, 1111);
        verify(resultEquivalence, never()).doEquivalent(anyInt(), anyInt());
    }

    @Test
    public void shouldFailBecauseEndpointDoesNotMatch() throws Exception {
        currentCalibrationInfo(CalibrationInfo.create(1111, 2222, 3333));
        when(queryEquivalence.doEquivalent(1111, 1111)).thenReturn(true);
        when(endpointEquivalence.doEquivalent(2222, 2222)).thenReturn(false);

        boolean validate = consistencyValidator.validate(1111, 2222, 3333, 10L);

        assertThat(validate, is(false));
        verify(endpointEquivalence).doEquivalent(2222, 2222);
        verify(resultEquivalence, never()).doEquivalent(anyInt(), anyInt());
    }

    private void currentCalibrationInfo(CalibrationInfo... elements) {
        ImmutableList<CalibrationInfo> list = ImmutableList.copyOf(elements);

        LogReader.FileReader<CalibrationInfo> result = mock(LogReader.FileReader.class);
        when(result.iterator()).thenReturn(list.iterator());
        when(logReader.read(sessionId, taskId + "/Calibration", "kernel", CalibrationInfo.class)).thenReturn(result);
    }
}
