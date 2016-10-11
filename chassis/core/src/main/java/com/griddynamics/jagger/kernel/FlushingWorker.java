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

package com.griddynamics.jagger.kernel;

import com.google.common.base.Function;
import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.CommandExecutor;
import com.griddynamics.jagger.coordinator.Worker;

import java.io.Serializable;
import java.util.Collection;

import static com.google.common.collect.Collections2.transform;

public class FlushingWorker<C extends Command<R>, R extends Serializable> implements Worker {
    private final Worker worker;

    public FlushingWorker(Worker worker) {
        this.worker = worker;
    }

    @Override
    public Collection<CommandExecutor<?, ?>> getExecutors() {
        return transform(worker.getExecutors(),
                new Function<CommandExecutor<?, ?>, CommandExecutor<?, ?>>() {
                    @Override
                    public CommandExecutor<?, ?> apply(CommandExecutor<?, ?> input) {
                        Object flushingCommandExecutor = FlushingCommandExecutor.create(input);
                        return (CommandExecutor<?, ?>) flushingCommandExecutor;
                    }
                });
    }
}
