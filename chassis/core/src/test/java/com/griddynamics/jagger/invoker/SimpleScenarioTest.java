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

package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.util.SystemClock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//rebuild scenario conception
public class SimpleScenarioTest {
    private SimpleScenario<Integer, Integer, Integer> scenario;
    private Invoker<Integer, Integer, Integer> invoker;
    private LoadInvocationListener<Integer, Integer, Integer> listener;
    private SystemClock clock;

//    @BeforeMethod
//    public void setUp() throws Exception {
//        invoker = mock(Invoker.class);
//        clock = mock(SystemClock.class);
//        scenario = SimpleScenario.create(invoker, 1, 2, clock);
//        listener = mock(LoadInvocationListener.class);
//        scenario.setListener(listener);
//    }
//
//    @Test
//    public void shouldInvokeInvokerWhileDoingTransaction() throws Exception {
//        scenario.doTransaction();
//
//        verify(invoker).invoke(1, 2);
//    }
//
//    @Test
//    public void shouldInvokeListenerWhenSuccessfullyInvoked() throws Exception {
//        when(clock.currentTimeMillis()).thenReturn(1L, 5L);
//        when(invoker.invoke(1, 2)).thenReturn(3);
//
//        scenario.doTransaction();
//
//        verify(listener).onStart(1, 2);
//        verify(listener).onSuccess(1, 2, 3, 4);
//    }
//
//    @Test
//    public void shouldInvokeListenerWhenFailed() throws Exception {
//        InvocationException exception = new InvocationException();
//        when(invoker.invoke(1, 2)).thenThrow(exception);
//
//        scenario.doTransaction();
//
//        verify(listener).onStart(1, 2);
//        verify(listener).onFail(1, 2, exception);
//    }
//
//    @Test
//    public void shouldInvokeListenerWhenErrorOccurred() throws Exception {
//        RuntimeException error = new RuntimeException();
//        when(invoker.invoke(1, 2)).thenThrow(error);
//
//        scenario.doTransaction();
//
//        verify(listener).onStart(1, 2);
//        verify(listener).onError(1, 2, error);
//    }
}
