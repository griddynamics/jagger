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

package com.griddynamics.jagger.extension;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.Map;

public class ExtensionRegistry<T> implements Ordered, ApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(ExtensionRegistry.class);

    private Map<String, T> extensions = Maps.newHashMap();
    private Class<T> extensionClass;
    private boolean autoDiscovery = true;

    public ExtensionRegistry(Class<T> extensionClass) {
        this.extensionClass = extensionClass;
    }

    public void setExtensions(Map<String, T> extensions) {
        this.extensions.putAll(extensions);
    }

    public Map<String, T> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    public T getExtension(String name) {
        return extensions.get(name);
    }

    public void setAutoDiscovery(boolean autoDiscovery) {
        this.autoDiscovery = autoDiscovery;
    }

    public boolean isAutoDiscovery() {
        return autoDiscovery;
    }

    public Class<T> getExtensionClass() {
        return extensionClass;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (autoDiscovery) {
            for (Map.Entry<String, ExtensionExporter> entry : ((ContextRefreshedEvent) event).getApplicationContext().
                    getBeansOfType(ExtensionExporter.class).entrySet()) {
                Object extension = entry.getValue().getExtension();
                if (extensionClass.isAssignableFrom(extension.getClass())) {
                    extensions.put(entry.getKey(), extensionClass.cast(entry.getValue().getExtension()));
                }
            }
        }
    }
}
