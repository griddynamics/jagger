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

package com.griddynamics.jagger.invoker.http;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.invoker.*;
import com.griddynamics.jagger.util.JavaSystemClock;
import com.griddynamics.jagger.util.SystemClock;
import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;

/**
 * @author Alexey Kiselyov
 *         Date: 04.08.11
 */
@Deprecated
public class HttpInvokerConfiguration implements ScenarioFactory<HttpQuery, HttpResponse, String>, Serializable {
    private HttpQuery query;
    private String endpoint;
    private SystemClock systemClock = new JavaSystemClock();

    @Override
    public Scenario<HttpQuery, HttpResponse, String> get(NodeContext nodeContext) {
        HttpInvoker httpInvoker = nodeContext.getService(HttpInvoker.class);
        SimpleScenario<HttpQuery, HttpResponse, String> invoker = SimpleScenario.create(httpInvoker, query, endpoint, systemClock);
        LoadInvocationLogger<HttpQuery, HttpResponse, String> logger = LoadInvocationLogger.create();
        invoker.setListener(logger);
        return invoker;
    }

    @Required
    public void setQuery(HttpQuery query) {
        this.query = query;
    }


    @Required
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public int getCalibrationSamplesCount() {
        return 1;
    }
}
