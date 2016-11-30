package com.griddynamics.jagger.master;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.JdkFutureAdapters;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.SettableFuture;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.RemoteExecutor;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.util.Nothing;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 11/28/13
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListenableService<T extends Task> implements Service {

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

    public ListenableFuture<State> start() {

        ListenableFuture<Nothing> runListener = JdkFutureAdapters.listenInPoolThread(executor.submit(new Callable<Nothing>() {
            @Override
            public Nothing call() {
                listener.onDistributionStarted(sessionId, taskId, task, remotes.keySet());
                return Nothing.INSTANCE;
            }
        }));


        return Futures.transform(runListener, new AsyncFunction<Nothing, State>() {
            @Override
            public ListenableFuture<State> apply(Nothing input) {
                return doStart();
            }
        });
    }

    @Override
    public State startAndWait() {
        return null;
    }

    @Override
    public Service startAsync() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public State state() {
        return null;
    }


    private ListenableFuture<State> doStart() {
        return service.start();
    }

    public ListenableFuture<State> stop() {
        ListenableFuture<State> stop = service.stop();

        return Futures.transform(stop, new AsyncFunction<State, State>() {
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

    @Override
    public State stopAndWait() {
        return null;
    }

    @Override
    public Service stopAsync() {
        return null;
    }

    @Override
    public void awaitRunning() {

    }

    @Override
    public void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {

    }

    @Override
    public void awaitTerminated() {

    }

    @Override
    public void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {

    }

    @Override
    public Throwable failureCause() {
        return null;
    }

    @Override
    public void addListener(Listener listener, Executor executor) {

    }
}
