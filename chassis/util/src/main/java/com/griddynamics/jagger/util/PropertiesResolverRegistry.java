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

package com.griddynamics.jagger.util;

import com.griddynamics.jagger.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Repository that stores two sets of properties: root properties and regular properties.
 * Root properties can substituted into regular properties.
 */
public class PropertiesResolverRegistry implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(PropertiesResolverRegistry.class);

    private ApplicationContext context;
    private Properties properties = new Properties();
    private Properties priorityProperties = new Properties();

    public void addProperty(String name, String value) {
        priorityProperties.setProperty(name, value);
    }

    public String getProperty(String name) {
        String value = priorityProperties.getProperty(name);
        if(value == null) {
            value = resolveProperty(properties.getProperty(name));
        }

        return value;
    }

    public Properties resolve(String propertiesResourceLocation) {
        Properties rawProperties = new Properties();
        Properties result = new Properties();

        try {
            Resource resource = context.getResource(propertiesResourceLocation);
            rawProperties.load(resource.getInputStream());
        } catch(IOException e) {
            throw new TechnicalException(e);
        }

        for(String rawPropertyName : rawProperties.stringPropertyNames()) {
            String overridingValue = getProperty(rawPropertyName);
            if (StringUtils.isEmpty(overridingValue)) {
                String rawPropertyValue = rawProperties.getProperty(rawPropertyName);
                result.setProperty(rawPropertyName, resolveProperty(rawPropertyValue));
            } else {
                result.setProperty(rawPropertyName, overridingValue);
            }
        }

        return result;
    }

    private String resolveProperty(String property) {
        if(property == null) {
            return null;
        }

        for(String rootPropertyName : properties.stringPropertyNames()) {
            String resolvedProperty= priorityProperties.getProperty(rootPropertyName);
            if(resolvedProperty==null){
                resolvedProperty= properties.getProperty(rootPropertyName);
            }
            property = property.replaceAll("\\$\\{" + rootPropertyName + "\\}", resolvedProperty);
        }

        return property;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public void setResources(List<Resource> resources) {
         setResources(resources, false);
    }

    public void setPriorityResources(List<Resource> resources) {
             setResources(resources, true);
        }

    public void addProperties(Properties properties) {
         mergeProperties(this.properties, properties);
    }

    private void setResources(List<Resource> resources, boolean priority) {
        Properties base = priority ? priorityProperties : properties;
        try {
            for(Resource resource : resources) {
                if(resource != null) {
                    Properties properties = new Properties();
                    properties.load(resource.getInputStream());
                    mergeProperties(base, properties);
                }
            }
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }

    private static void mergeProperties(Properties base, Properties mixin) {
        for(String name : mixin.stringPropertyNames()) {
            base.setProperty(name, mixin.getProperty(name));
        }
    }
}