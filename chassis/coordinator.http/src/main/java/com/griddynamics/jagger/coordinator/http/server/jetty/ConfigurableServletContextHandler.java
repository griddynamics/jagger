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

package com.griddynamics.jagger.coordinator.http.server.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * Allows to configure servlets using spring configuration files instead of web.xml.
 *
 * @author Mairbek Khadikov
 */
public class ConfigurableServletContextHandler implements Handler {
    private final ServletContextHandler delegate;


    public ConfigurableServletContextHandler(String contextPath) {
        delegate = new ServletContextHandler(ServletContextHandler.SESSIONS);
        delegate.setContextPath(contextPath);
    }

    @Required
    public void setServletConfigurations(Collection<ServletConfiguration> servletConfigurations) {
        for (ServletConfiguration configuration : servletConfigurations) {
            delegate.addServlet(new ServletHolder(configuration.getServlet()), configuration.getPath());
        }
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        delegate.handle(s, request, httpServletRequest, httpServletResponse);
    }

    @Override
    public void setServer(Server server) {
        delegate.setServer(server);
    }

    @Override
    public Server getServer() {
        return delegate.getServer();
    }

    @Override
    public void destroy() {
        delegate.destroy();
    }

    @Override
    public void start() throws Exception {
        delegate.start();
    }

    @Override
    public void stop() throws Exception {
        delegate.stop();
    }

    @Override
    public boolean isRunning() {
        return delegate.isRunning();
    }

    @Override
    public boolean isStarted() {
        return delegate.isStarted();
    }

    @Override
    public boolean isStarting() {
        return delegate.isStarting();
    }

    @Override
    public boolean isStopping() {
        return delegate.isStopping();
    }

    @Override
    public boolean isStopped() {
        return delegate.isStopped();
    }

    @Override
    public boolean isFailed() {
        return delegate.isFailed();
    }

    @Override
    public void addLifeCycleListener(Listener listener) {
        delegate.addLifeCycleListener(listener);
    }

    @Override
    public void removeLifeCycleListener(Listener listener) {
        delegate.removeLifeCycleListener(listener);
    }
}
