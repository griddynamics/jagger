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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

public class ExtensionManager implements Ordered, ApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(ExtensionManager.class);

    private Map<String, ExtensionRegistry<?>> extensionRegistryMap = new HashMap<String, ExtensionRegistry<?>>();

    private boolean logExtensions = true;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ContextRefreshedEvent) {

            for(Map.Entry<String, ExtensionRegistry> entry : ((ContextRefreshedEvent) event).getApplicationContext().getBeansOfType(ExtensionRegistry.class).entrySet()) {
                extensionRegistryMap.put(entry.getKey(), entry.getValue());
            }

            if(logExtensions && log.isDebugEnabled()) {
                if(extensionRegistryMap.isEmpty()) {
                    log.debug("No Extensions Registered");
                } else {
                    log.debug("Extension registries :");
                    for(Map.Entry<String, ExtensionRegistry<?>> extensionRegistry : extensionRegistryMap.entrySet()) {
                        log.debug(String.format(
                                "   Registry : [ %s ]<%s> (autodiscovery=%s) contains [%d] extensions :",
                                extensionRegistry.getKey(),
                                extensionRegistry.getValue().getExtensionClass().getCanonicalName(),
                                extensionRegistry.getValue().isAutoDiscovery(),
                                extensionRegistry.getValue().getExtensions().size()
                        ));
                        for(Map.Entry<String, ?> extension : extensionRegistry.getValue().getExtensions().entrySet()) {
                            log.debug("      Extension : class [ " + extension.getValue().getClass().getCanonicalName() + " ], bean name [ " + extension.getKey() + " ]");
                        }
                    }
                }
            }
        }
    }

    public boolean isLogExtensions() {
        return logExtensions;
    }

    public void setLogExtensions(boolean logExtensions) {
        this.logExtensions = logExtensions;
    }
}
