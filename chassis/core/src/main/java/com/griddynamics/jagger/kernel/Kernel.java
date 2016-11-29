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

import com.griddynamics.jagger.Terminable;
import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.Worker;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.fs.logging.LogReader;
import com.griddynamics.jagger.storage.fs.logging.LogWriter;
import com.griddynamics.jagger.util.ThreadExecutorUtil;
import org.springframework.beans.factory.annotation.Required;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic API for kernel node.
 *
 * @author Mairbek Khadikov
 * @author Alexey Kiselyov
 */
public abstract class Kernel implements Runnable, Terminable {

    private final Coordinator coordinator;

    private KeyValueStorage keyValueStorage;

    private long reconnectPeriod;

    private volatile boolean connected;

    private LogWriter logWriter;

    private LogReader logReader;

    public Kernel(Coordinator coordinator) {
        this.coordinator = checkNotNull(coordinator);
    }

    public LogWriter getLogWriter() {
        return logWriter;
    }

    @Required
    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    public LogReader getLogReader() {
        return logReader;
    }

    @Required
    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public long getReconnectPeriod() {
        return reconnectPeriod;
    }

    public void setReconnectPeriod(long reconnectPeriod) {
        this.reconnectPeriod = reconnectPeriod;
    }

    @Override
    public void run() {
        KernelRegistrar task = new KernelRegistrar(coordinator, this);
        ThreadExecutorUtil.getInstance().addTask(task, this.getKernelId());
    }

    public KeyValueStorage getKeyValueStorage() {
        return keyValueStorage;
    }

    @Required
    public void setKeyValueStorage(KeyValueStorage keyValueStorage) {
        this.keyValueStorage = keyValueStorage;
    }

    public abstract NodeId getKernelId();

    public abstract NodeContext getContext();

    public abstract Set<Worker> getWorkers();

}
