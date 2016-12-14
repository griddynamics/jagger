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

package com.griddynamics.jagger.coordinator.http;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.async.AsyncCallback;
import com.griddynamics.jagger.coordinator.async.AsyncRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class DefaultPackExchanger implements PackExchanger, AsyncRunner<Command<Serializable>, Serializable> {
    private static final Logger log = LoggerFactory.getLogger(DefaultPackExchanger.class);

    private final LinkedBlockingQueue<PackEntry<Command<Serializable>>> commandsToSend = new LinkedBlockingQueue<PackEntry<Command<Serializable>>>();
    private final LinkedBlockingQueue<PackEntry<Serializable>> resultsToSend = new LinkedBlockingQueue<PackEntry<Serializable>>();
    private final Map<UUID, AsyncCallback<Serializable>> expectedResults = Maps.newConcurrentMap();

    private final Executor executor;
    private final AsyncRunner<Command<Serializable>, Serializable> incomingCommandRunner;

    public DefaultPackExchanger(Executor executor, AsyncRunner<Command<Serializable>, Serializable> incomingCommandRunner) {
        this.executor = executor;
        this.incomingCommandRunner = incomingCommandRunner;
    }

    public LinkedBlockingQueue<PackEntry<Command<Serializable>>> getCommandsToSend() {
        return commandsToSend;
    }

    public LinkedBlockingQueue<PackEntry<Serializable>> getResultsToSend() {
        return resultsToSend;
    }

    @Override
    public Pack retrieve() {
        log.debug("Pack retrieving requested");
        List<PackEntry<Command<Serializable>>> commandsChunk = Lists.newLinkedList();
        commandsToSend.drainTo(commandsChunk);

        List<PackEntry<Serializable>> resultsChunk = Lists.newLinkedList();
        resultsToSend.drainTo(resultsChunk);

        log.debug("{} commands to send; {} results to sends", resultsToSend.size(), commandsToSend.size());

        return Pack
                .builder()
                .addAllCommands(commandsChunk)
                .addAllResults(resultsChunk)
                .build();
    }

    @Override
    public void process(Pack income) {
        log.debug("Exchange of packs requested");

        processResults(income.getResults());

        scheduleCommands(income.getCommands());
    }

    private void scheduleCommands(List<PackEntry<Command<Serializable>>> commands) {
        log.debug("Scheduling commands requested");

        for (PackEntry<Command<Serializable>> entry : commands) {
            final UUID id = entry.getId();
            final Command<Serializable> command = entry.getValue();

            log.debug("Going to schedule command {} with id {}", command, id);

            incomingCommandRunner.run(command, new AsyncCallback<Serializable>() {
                @Override
                public void onSuccess(Serializable result) {
                    log.debug("Command {} with id {} executed successfully", command, id);
                    resultsToSend.add(PackEntry.create(id, result));
                }

                @Override
                public void onFailure(Throwable throwable) {
                    log.debug("Command {} with id {} execution failed", command, id);
                    resultsToSend.add(PackEntry.fail(id, throwable));
                }
            });

        }

        log.debug("Scheduling commands completed");
    }

    private void processResults(List<PackEntry<Serializable>> results) {
        log.debug("Processing results requested");

        for (PackEntry<Serializable> entry : results) {
            final UUID id = entry.getId();
            final Serializable result = entry.getValue();
            final Throwable error = entry.getException();

            log.debug("Processing result for command with id {}. Result : {}", id, result);

            final AsyncCallback<Serializable> callback = expectedResults.get(id);
            if (callback == null) {
                throw new IllegalStateException("No callback for command with id " + id + " found");
            }

            expectedResults.remove(id);

            if (error == null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        log.debug("Calling onSuccess for command {}", id);
                        callback.onSuccess(result);
                        log.debug("onSuccess for command {} called", id);
                    }
                });
            } else {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        log.debug("Calling onFailure for command {}", id);
                        callback.onFailure(error);
                        log.debug("onFailure for command {} called", id);
                    }
                });
            }
        }

        log.debug("Processing results completed");
    }

    @Override
    public void run(Command<Serializable> command, AsyncCallback<Serializable> callback) {
        log.debug("Going to schedule command {} for next pack", command);
        UUID uuid = UUID.randomUUID();

        expectedResults.put(uuid, callback);
        PackEntry<Command<Serializable>> entry = PackEntry.create(uuid, command);
        commandsToSend.add(entry);
        log.debug("Pack entry {} is scheduled for client", entry);
    }

    public void clean() {
        commandsToSend.clear();
        resultsToSend.clear();
        expectedResults.clear();
        log.debug("Pack exchanger cleaned");
    }
}
