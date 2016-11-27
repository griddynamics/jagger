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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Coordination {
    private Coordination() {

    }

    public static Worker emptyWorker() {
        return EmptyWorker.INSTANCE;
    }

    public static Worker workerOf(Collection<CommandExecutor<?,?>> executors) {
        return new SimpleWorker(executors);
    }

    public static Worker workerOf(CommandExecutor<?,?>... executors) {
        return new SimpleWorker(Arrays.asList(executors));
    }

    public static NodeContextBuilder contextBuilder(NodeId id) {
        return new NodeContextBuilder(id);
    }

    public static NodeContext emptyContext(final NodeId id) {
        return new NodeContext() {
            @Override
            public NodeId getId() {
                return id;
            }

            @Override
            public <T> T getService(Class<T> clazz) {
                return null;
            }
        };
    }

    public static <C extends Command> NodeCommandExecutionListener<C> compose(Iterable<NodeCommandExecutionListener<C>> listeners) {
        return new CompositeNodeCommandExecutionListener<C>(listeners);
    }

    @SuppressWarnings("unchecked")
    public static <C extends Command> NodeCommandExecutionListener<C> doNothing() {
        return DoNothingExecutionListener.INSTANCE;
    }

    private static enum DoNothingExecutionListener implements NodeCommandExecutionListener {
        INSTANCE;

        @Override
        public void onCommandExecutionStarted(Object command, Object context) {
            // do nothing
        }

        @Override
        public void onCommandExecuted(Object command) {
            // do nothing
        }
    }

    private static enum EmptyWorker implements Worker {
        INSTANCE;

        @Override
        public Collection<CommandExecutor<?, ?>> getExecutors() {
            return Collections.emptySet();
        }
    }

    private static class CompositeNodeCommandExecutionListener<C extends Command> implements NodeCommandExecutionListener<C> {
        private final Iterable<NodeCommandExecutionListener<C>> listeners;

        public CompositeNodeCommandExecutionListener(Iterable<NodeCommandExecutionListener<C>> listeners) {
            this.listeners = listeners;
        }

        @Override
        public void onCommandExecutionStarted(C command, NodeContext context) {
            for (NodeCommandExecutionListener<C> listener : listeners) {
                listener.onCommandExecutionStarted(command, context);
            }
        }

        @Override
        public void onCommandExecuted(C command) {
            for (NodeCommandExecutionListener<C> listener : listeners) {
                listener.onCommandExecuted(command);
            }
        }
    }
}
