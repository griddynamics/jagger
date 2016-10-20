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

import com.griddynamics.jagger.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Alternative to PropertyPlaceholderConfigurer that allows to declare multiple beans which are backed by single properties registry
 */
public class PropertiesResolver extends PropertyPlaceholderConfigurer {
    
    private PropertiesResolverRegistry registry;

    protected String resolvePlaceholder(String placeholder, Properties props) {
        return registry.getProperty(placeholder);
    }

    public void setRegistry(PropertiesResolverRegistry registry) {
        this.registry = registry;
        loadSystemProperties();
    }

    public void setResources(List<Resource> resources) {
        try {
            for (Resource resource : resources) {
                Properties properties = new Properties();
                properties.load(resource.getInputStream());
                registry.addProperties(properties);
            }
            loadSystemProperties();
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }

    private void loadSystemProperties(){
        Properties propertiesSys = System.getProperties();
        for (Enumeration<String> enumeration = (Enumeration<String>) propertiesSys.propertyNames(); enumeration.hasMoreElements(); ) {
            String key = enumeration.nextElement();
            registry.addProperty(key, (String) propertiesSys.get(key));
        }
    }
}