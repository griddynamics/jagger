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

package com.griddynamics.jagger.util;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * User: dkotlyarov
 */
public class JaggerXmlApplicationContext extends AbstractXmlApplicationContext {
    private final URL directory;
    private final Properties environmentProperties;

    public JaggerXmlApplicationContext(URL directory, Properties environmentProperties, String[] configLocations) {
        if (directory.toString().endsWith("/")) {
            this.directory = directory;
        } else {
            try {
                this.directory = new URL(directory, "/");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        this.environmentProperties = environmentProperties;

        setConfigLocations(configLocations);
        refresh();
    }

    public URL getDirectory() {
        return directory;
    }

    public Properties getEnvironmentProperties() {
        return environmentProperties;
    }

    @Override
    protected Resource getResourceByPath(String path) {
        try {
            return new UrlResource(new URL(directory, path));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
