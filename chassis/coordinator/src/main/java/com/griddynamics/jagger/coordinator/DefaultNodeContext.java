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

package com.griddynamics.jagger.coordinator;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.ObjectName;
import java.util.Map;

public class DefaultNodeContext implements NodeContext {
    Logger log = LoggerFactory.getLogger(DefaultNodeContext.class);

    private final NodeId id;
    private final Map<Class<?>, Object> services;

    private final Object semaphore = new Object();

    /*package*/ DefaultNodeContext(NodeId id, Map<Class<?>, Object> services) {
        this.id = id;
        this.services = services;
    }

    @Override
    public NodeId getId() {
        return id;
    }

    @Override
    public <T> T getService(Class<T> clazz) {
        T service = (T) services.get(clazz);
        if(service == null) {
            log.warn("Not found service for class '{}'. Try to create via default constructor", clazz.getCanonicalName());
            try {
                synchronized (semaphore) {
                    service = (T) services.get(clazz);
                    if(service == null) {
                        service = clazz.newInstance();
                        services.put(clazz, service);
                    }
                }
            } catch (InstantiationException e) {
                throw Throwables.propagate(e);
            } catch (IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        }
        return service;
    }

    @Override
    public String toString() {
        return "DefaultNodeContext{" +
                "id=" + id +
                ", services=" + services +
                '}';
    }
}
