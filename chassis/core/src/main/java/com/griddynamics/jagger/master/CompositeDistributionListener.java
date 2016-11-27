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

package com.griddynamics.jagger.master;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.master.configuration.Task;

import java.util.Collection;

public class CompositeDistributionListener implements DistributionListener {
    private final Iterable<DistributionListener> delegates;

    public static CompositeDistributionListener of(Iterable<DistributionListener> delegates) {
        return new CompositeDistributionListener(delegates);
    }

    public static CompositeDistributionListener of(DistributionListener... delegates) {
        return new CompositeDistributionListener(Lists.newArrayList(delegates));
    }

    private CompositeDistributionListener(Iterable<DistributionListener> delegates) {
        this.delegates = delegates;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        for (DistributionListener delegate : delegates) {
            delegate.onDistributionStarted(sessionId, taskId, task, capableNodes);
        }
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        for (DistributionListener delegate : delegates) {
            delegate.onTaskDistributionCompleted(sessionId, taskId, task);
        }
    }
}
