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

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.CommandExecutor;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.Qualifier;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class FlushingCommandExecutor<C extends Command<R>, R extends Serializable> implements CommandExecutor<C, R> {
    private static final Logger log = LoggerFactory.getLogger(FlushingCommandExecutor.class);
    private final CommandExecutor<C, R> delegate;

    public static <C extends Command<R>, R extends Serializable> FlushingCommandExecutor<C, R> create(CommandExecutor<C, R> delegate) {
        return new FlushingCommandExecutor<C, R>(delegate);
    }

    private FlushingCommandExecutor(CommandExecutor<C, R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Qualifier<C> getQualifier() {
        return delegate.getQualifier();
    }

    @Override
    public R execute(C command, NodeContext nodeContext) {
        LogWriter logWriter = nodeContext.getService(LogWriter.class);
        try {
            log.debug("Execute command {} on {}", command, nodeContext.getId());
            return delegate.execute(command, nodeContext);
        } finally {
            log.debug("Flushing results execution command {} on {}", command, nodeContext.getId());
            logWriter.flush();
        }
    }
}
