package com.griddynamics.jagger.master;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.util.Nothing;

import com.google.common.base.Function;
import com.google.common.util.concurrent.ForwardingService;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * User: kgribov
 * Date: 11/28/13
 * Time: 12:57 PM
 */
public class ListenableService<T extends Task> extends ForwardingService {
    
    private final ExecutorService executor;
    private final T task;
    private final DistributionListener listener;
    private final Map<NodeId, RemoteExecutor> remotes;
    private final Service service;
    
    public ListenableService(Service delegate, ExecutorService executor, T task, DistributionListener listener,
                             Map<NodeId, RemoteExecutor> remotes
    ) {
        this.executor = executor;
        this.task = task;
        this.listener = listener;
        this.remotes = remotes;
        this.service = delegate;
    }
    
    private ListenableFuture<State> doStart() {
        return super.start();
    }
    
    @Override
    protected Service delegate() {
        return service;
    }
    
    @Override
    public ListenableFuture<State> start() {
        
        ListenableFuture<Nothing> runListener = Futures.makeListenable(executor.submit(() -> {
            listener.onDistributionStarted(task.getSessionId(), task.getTaskId(), task, remotes.keySet());
            return Nothing.INSTANCE;
        }));
        
        return Futures.chain(runListener, new Function<Nothing, ListenableFuture<State>>() {
            @Override
            public ListenableFuture<State> apply(Nothing input) {
                return doStart();
            }
        });
    }
    
    @Override
    public ListenableFuture<State> stop() {
        ListenableFuture<State> stop = super.stop();
        
        return Futures.chain(stop, new Function<State, ListenableFuture<State>>() {
            @Override
            public ListenableFuture<State> apply(final State input) {
                
                final SettableFuture<State> result = SettableFuture.create();
                executor.execute(() -> {
                    try {
                        listener.onTaskDistributionCompleted(task.getSessionId(), task.getTaskId(), task);
                    } finally {
                        result.set(input);
                    }
                });
                return result;
            }
        });
    }
}
