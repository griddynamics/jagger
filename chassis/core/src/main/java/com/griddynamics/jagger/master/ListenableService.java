package com.griddynamics.jagger.master;

import com.google.common.base.Function;
import com.google.common.util.concurrent.*;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.util.Nothing;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 11/28/13
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListenableService<T extends Task> extends ForwardingService{

    private final ExecutorService executor;
    private final String sessionId;
    private final String taskId;
    private final T task;
    private final DistributionListener listener;
    private final Map<NodeId, RemoteExecutor> remotes;
    private final Service service;

    public ListenableService(Service delegate, ExecutorService executor, String sessionId, String taskId, T task, DistributionListener listener, Map<NodeId, RemoteExecutor> remotes) {
        this.executor = executor;
        this.sessionId = sessionId;
        this.taskId = taskId;
        this.task = task;
        this.listener = listener;
        this.remotes = remotes;
        this.service = delegate;
    }

    @Override
    public ListenableFuture<State> start() {

        ListenableFuture<Nothing> runListener = Futures.makeListenable(executor.submit(new Callable<Nothing>() {
            @Override
            public Nothing call() {
                listener.onDistributionStarted(sessionId, taskId, task, remotes.keySet());
                return Nothing.INSTANCE;
            }
        }));


        return Futures.chain(runListener, new Function<Nothing, ListenableFuture<State>>() {
            @Override
            public ListenableFuture<State> apply(Nothing input) {
                return doStart();
            }
        });
    }


    private ListenableFuture<State> doStart() {
        return super.start();
    }

    @Override
    protected Service delegate() {
        return service;
    }

    @Override
    public ListenableFuture<State> stop() {
        ListenableFuture<State> stop = super.stop();

        return Futures.chain(stop, new Function<State, ListenableFuture<State>>() {
            @Override
            public ListenableFuture<State> apply(final State input) {

                final SettableFuture<State> result = SettableFuture.create();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listener.onTaskDistributionCompleted(sessionId, taskId, task);
                        } finally {
                            result.set(input);
                        }
                    }
                });
                return result;
            }
        });
    }
}
