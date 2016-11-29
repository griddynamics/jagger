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

package com.griddynamics.jagger.coordinator.zookeeper;

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.NodeCommandExecutionListener;

import java.io.Serializable;

public class QueueEntry<C extends Command<R>, R extends Serializable> implements Serializable {
    private C command;
    private NodeCommandExecutionListener<C> listener;
    private String resultPath;

    public QueueEntry() {
    }

    public QueueEntry(C command, NodeCommandExecutionListener<C> listener, String resultPath) {
        super();
        this.command = command;
        this.listener = listener;
        this.resultPath = resultPath;
    }

    public C getCommand() {
        return command;
    }

    public void setCommand(C command) {
        this.command = command;
    }

    public String getResultPath() {
        return resultPath;
    }

    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }

    public NodeCommandExecutionListener<C> getListener() {
        return listener;
    }

    public void setListener(NodeCommandExecutionListener<C> listener) {
        this.listener = listener;
    }

    private static final long serialVersionUID = -5005775538088420162L;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QueueEntry that = (QueueEntry) o;

        if (command != null ? !command.equals(that.command) : that.command != null) return false;
        if (listener != null ? !listener.equals(that.listener) : that.listener != null) return false;
        if (resultPath != null ? !resultPath.equals(that.resultPath) : that.resultPath != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = command != null ? command.hashCode() : 0;
        result = 31 * result + (listener != null ? listener.hashCode() : 0);
        result = 31 * result + (resultPath != null ? resultPath.hashCode() : 0);
        return result;
    }
}
