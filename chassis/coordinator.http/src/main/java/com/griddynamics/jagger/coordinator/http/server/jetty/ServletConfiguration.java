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

import org.springframework.beans.factory.annotation.Required;

import javax.servlet.Servlet;

/**
 * Allows to bind servlet to it's path via spring configuration.
 *
 * @author Mairbek Khadikov
 */
public class ServletConfiguration {
    private Servlet servlet;
    private String path;

    public static ServletConfiguration create(Servlet servlet, String path) {
        ServletConfiguration configuration = new ServletConfiguration();
        configuration.setServlet(servlet);
        configuration.setPath(path);
        return configuration;
    }

    public Servlet getServlet() {
        return servlet;
    }

    @Required
    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public String getPath() {
        return path;
    }

    @Required
    public void setPath(String path) {
        this.path = path;
    }
}
