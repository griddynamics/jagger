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

package com.griddynamics.jagger.kernel;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.invoker.Invoker;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.fs.logging.LogReader;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author Alexey Kiselyov Date: 17.05.11
 */
// todo rename to runnable
public class SimpleKernel extends Kernel implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SimpleKernel.class);

    private CountDownLatch lock;

    private Set<Worker> workers;
    private Set<CommandExecutor<?, ?>> commandExecutors = Collections.emptySet();

    private final NodeId nodeId;

    private ApplicationContext context;

    public SimpleKernel(Coordinator coordinator) {
        super(coordinator);
        lock = new CountDownLatch(1);
        this.nodeId = NodeId.kernelNode();
    }

    @Override
    public NodeId getKernelId() {
        return nodeId;
    }

    @Override
    public NodeContext getContext() {
        NodeContextBuilder builder = Coordination.contextBuilder(nodeId);
        builder
                .addService(LogWriter.class, getLogWriter())
                .addService(LogReader.class, getLogReader())
                .addService(KeyValueStorage.class, getKeyValueStorage())
                .addAll(lookUpInvokers());
        return builder.build();
    }

    @Override
    public Set<Worker> getWorkers() {
        return allWorkers();
//                Sets.newHashSet(Collections2.transform(allWorkers(),
//                new Function<Worker, Worker>() {
//                    @Override
//                    public Worker apply(Worker input) {
//                        return new FlushingWorker(input);
//                    }
//                }));
    }

    private Set<Worker> allWorkers() {
        Set<Worker> result = Sets.newHashSet();
        result.addAll(workers);
        for (CommandExecutor<?, ?> commandExecutor : commandExecutors) {
            result.add(Coordination.workerOf(commandExecutor));
        }
        return result;
    }


    @Required
    public void setWorkers(Set<Worker> workers) {
        this.workers = workers;
    }

    public void run() {
        super.run();
        try {
            lock.await();
        } catch (InterruptedException e) {
            log.error("Interrupted!");
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void terminate() {
        log.debug("termination signal received!");
        if (context instanceof DisposableBean) {
            try {
                ((DisposableBean) context).destroy();
            } catch (Exception e) {
                log.error("Error during context termination: {}", e.getMessage(), e);
            }
        }
        for (long i = lock.getCount(); i > 0; i--) lock.countDown();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.context = applicationContext;
    }

    public void setCommandExecutors(Set<CommandExecutor<?, ?>> commandExecutors) {
        this.commandExecutors = commandExecutors;
    }

    private Map<Class<Object>, Object> lookUpInvokers() {
        log.debug("Looking up for all invokers");
        Map<Class<Object>, Object> result = Maps.newHashMap();

        Map<String, Invoker> invokers = context.getBeansOfType(Invoker.class);
        log.debug("Loaded invokers from context {}", invokers);

        for (Map.Entry<String, Invoker> entry : invokers.entrySet()) {
            Invoker invoker = entry.getValue();
            Class clazz = invoker.getClass();
            result.put(clazz, invoker);
        }

        log.debug("Invokers found {}", result);
        return result;
    }
}
