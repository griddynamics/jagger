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

package com.griddynamics.jagger.invoker.soap;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.*;
import com.griddynamics.jagger.util.JavaSystemClock;
import com.griddynamics.jagger.util.SystemClock;

import java.io.Serializable;

/**
 * @author Alexey Kiselyov
 *         Date: 05.08.11
 */
@Deprecated
public class SOAPInvokerConfiguration implements ScenarioFactory<SOAPQuery, String, String>, Serializable {
    private SOAPQuery query;
    private SystemClock systemClock = new JavaSystemClock();

    @Override
    public Scenario<SOAPQuery, String, String> get(NodeContext nodeContext) {
        SOAPInvoker invoker = nodeContext.getService(SOAPInvoker.class);
        SimpleScenario<SOAPQuery, String, String> scenario = SimpleScenario.create(invoker, query, query.getDefaultEndpoint(), systemClock);
        LoadInvocationLogger<SOAPQuery, String, String> logger = LoadInvocationLogger.create();
        scenario.setListener(logger);
        return scenario;
    }

    public void setQuery(SOAPQuery query) {
        this.query = query;
    }

    public SOAPQuery getQuery() {
        return this.query;
    }

    public void setSystemClock(SystemClock systemClock) {
        this.systemClock = systemClock;
    }

    @Override
    public int getCalibrationSamplesCount() {
        return 1;
    }

    @Override
    public String toString() {
        return "SOAPInvokerConfiguration{" +
                "query=" + query +
                ", systemClock=" + systemClock +
                '}';
    }
}
