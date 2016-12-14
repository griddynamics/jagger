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

import com.griddynamics.jagger.coordinator.Coordinator;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.NodeStatus;
import com.griddynamics.jagger.coordinator.StatusChangeListener;
import com.griddynamics.jagger.util.ThreadExecutorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: vshulga
 * Date: 6/10/11
 * Time: 4:08 PM
 */

public class KernelRegistrar implements Runnable {

    private final Logger log = LoggerFactory.getLogger(KernelRegistrar.class);

    private Kernel kernel;
    private Coordinator coordinator;

    public KernelRegistrar(Coordinator coordinator, Kernel kernel) {
        this.coordinator = checkNotNull(coordinator);
        this.kernel = kernel;
    }

    @Override
    public void run() {
        while (!kernel.isConnected()) {
            try {
                this.coordinator.registerNode(kernel.getContext(), kernel.getWorkers(), new StatusChangeListener() {
                    @Override
                    public void onNodeStatusChanged(NodeId nodeId, NodeStatus status) {

                    }

                    @Override
                    public void onCoordinatorDisconnected() {

                    }

                    @Override
                    public void onCoordinatorConnected() {

                    }
                });
                kernel.setConnected(true);
            } catch (Throwable e) {
                log.warn("ThreadId {}. KernelID {} Unable to connect to Coordinator. Waiting for {} ms", new Object[]{Thread.currentThread().getId(), kernel.getKernelId(), kernel.getReconnectPeriod()});
                log.warn("cause: ", e);
                try {
                    Thread.sleep(kernel.getReconnectPeriod());
                } catch (InterruptedException ee) {
                    log.warn("Reconnection thread was interrupted", ee);
                }
            }
        }
    }
}
