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
import com.griddynamics.jagger.invoker.LoadInvocationLogger;
import com.griddynamics.jagger.invoker.Scenario;
import com.griddynamics.jagger.invoker.ScenarioFactory;
import com.griddynamics.jagger.invoker.SimpleScenario;
import com.griddynamics.jagger.util.JavaSystemClock;
import com.griddynamics.jagger.util.Nothing;
import com.griddynamics.jagger.util.SystemClock;

import java.io.Serializable;

@Deprecated
public class HttpVisitorInvokerConfiguration implements ScenarioFactory<Nothing, String, String>, Serializable {

    private static final long serialVersionUID = 5252435842227903389L;

    private SystemClock systemClock = new JavaSystemClock();
    private String url;

    public HttpVisitorInvokerConfiguration() {
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSystemClock(SystemClock systemClock) {
        this.systemClock = systemClock;
    }

    @Override
    public Scenario<Nothing, String, String> get(NodeContext nodeContext) {

        HttpVisitorInvoker invoker = nodeContext.getService(HttpVisitorInvoker.class);
        SimpleScenario<Nothing, String, String> scenario = SimpleScenario.create(
                invoker, Nothing.INSTANCE, url, systemClock);
        LoadInvocationLogger<Nothing, String, String> logger = LoadInvocationLogger.create();
        scenario.setListener(logger);
        return scenario;
    }

    @Override
    public String toString() {
        return "HttpVisitorInvokerConfiguration{" +
                "url='" + url + '\'' +
                '}';
    }

    @Override
    public int getCalibrationSamplesCount() {
        return 1;
    }
}
