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

package com.griddynamics.jagger.coordinator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Adds ability to create worker in declarative style.
 *
 * @author Mairbek Khadikov
 */
public abstract class ConfigurableWorker implements Worker {

    private Map<Qualifier<?>, CommandExecutor<?, ?>> executors;

    @Override
    public Collection<CommandExecutor<?, ?>> getExecutors() {
        executors = Maps.newHashMap();
        configure();
        return executors.values();
    }

    public abstract void configure();

    protected final <C extends Command<R>, R extends Serializable> ConfigurationBuilder<C, R> onCommandReceived(Qualifier<C> qualifier) {
        Preconditions.checkArgument(!executors.containsKey(qualifier), "Executor already bounded for qualifier " + qualifier);

        return new ConfigurationBuilder<C, R>(qualifier);
    }

    protected final <C extends Command<R>, R extends Serializable> ConfigurationBuilder<C, R> onCommandReceived(Class<C> clazz) {
        return onCommandReceived(Qualifier.of(clazz));
    }


    public class ConfigurationBuilder<C extends Command<R>, R extends Serializable> {
        private final Qualifier<C> qualifier;

        public ConfigurationBuilder(Qualifier<C> qualifier) {
            this.qualifier = qualifier;
        }

        public void execute(CommandExecutor<C, R> executor) {
            executors.put(qualifier, executor);
        }
    }
}
