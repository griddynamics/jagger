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

package com.griddynamics.jagger.agent;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.*;
import com.griddynamics.jagger.coordinator.async.AsyncCallback;
import com.griddynamics.jagger.coordinator.async.AsyncRunner;
import com.griddynamics.jagger.coordinator.http.NodeNotFound;
import com.griddynamics.jagger.coordinator.http.PackResponse;
import com.griddynamics.jagger.coordinator.http.client.ExchangeClient;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.util.ConfigurableExecutor;
import com.griddynamics.jagger.util.TimeUtils;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Starter for agent
 *
 * @author Alexey Kiselyov
 *         Date: 16.05.11
 */
public class Agent {
    private static final Logger log = LoggerFactory.getLogger(Agent.class);

    private Worker worker;
    private NodeContext nodeContext;
    private AtomicBoolean underManagement = new AtomicBoolean(false);
    private ExchangeClient exchangeClient;
    private ConfigurableExecutor executor;
    private ExchangerThread exchangerThread = new ExchangerThread();
    private long pollRate;
    private String urlBase;
    private String urlExchangePack;
    private String urlRegistration;
    private HttpClient httpClient;

    @Required
    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    @Required
    public void setUrlExchangePack(String urlExchangePack) {
        this.urlExchangePack = urlExchangePack;
    }

    @Required
    public void setUrlRegistration(String urlRegistration) {
        this.urlRegistration = urlRegistration;
    }

    @Required
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Worker getWorker() {
        return this.worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public StatusChangeListener changeListener = new StatusChangeListener() {
        @Override
        public void onNodeStatusChanged(NodeId nodeId, NodeStatus status) {
            log.debug("{} {}", nodeId, status);
        }

        @Override
        public void onCoordinatorDisconnected() {
            log.debug("Coordinator disconnected");
        }

        @Override
        public void onCoordinatorConnected() {
            log.debug("Coordinator connected");
        }
    };

    public StatusChangeListener getStatusChangeListener() {
        return this.changeListener;
    }

    public void setNodeContext(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    public NodeContext getNodeContext() {
        return this.nodeContext;
    }

    public boolean isUnderManagement() {
        return this.underManagement.get();
    }

    public void markAsUnderManagement() {
        this.underManagement.set(true);
    }

    public void unmarkAsUnderManagement() {
        this.underManagement.set(false);
    }

    public ExchangeClient getExchangeClient() {
        return exchangeClient;
    }

    public void setExecutor(ConfigurableExecutor executor) {
        this.executor = executor;
    }

    public ConfigurableExecutor getExecutor() {
        return executor;
    }

    @Required
    public void setPollRate(long pollRate) {
        this.pollRate = pollRate;
    }

    public void init() {
        final Map<Qualifier, CommandExecutor> commandsRunner = Maps.newHashMap();
        for (CommandExecutor commandExecutor : worker.getExecutors()) {
            commandsRunner.put(commandExecutor.getQualifier(), commandExecutor);
        }
        AsyncRunner<Command<Serializable>, Serializable> incomingCommandRunner =
                new AsyncRunner<Command<Serializable>, Serializable>() {
                    @Override
                    public void run(final Command<Serializable> command, final AsyncCallback<Serializable> callback) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Serializable result = commandsRunner.get(Qualifier.of(command)).execute(command, nodeContext);
                                    callback.onSuccess(result);
                                } catch (Throwable e) {
                                    callback.onFailure(e);
                                }
                            }
                        });
                    }
                };
        exchangeClient = new ExchangeClient(executor, incomingCommandRunner, nodeContext);
        exchangeClient.setNodeContext(nodeContext);
        exchangeClient.setHttpClient(httpClient);
        exchangeClient.setUrlBase(urlBase);
        exchangeClient.setUrlExchangePack(urlExchangePack);
        exchangeClient.setUrlRegistration(urlRegistration);
    }

    public void start() {
        exchangerThread.alive = true;
        executor.execute(exchangerThread);
    }

    public void stop() {
        exchangerThread.alive = false;
    }

    public class ExchangerThread implements Runnable {

        private volatile boolean alive;

        public ExchangerThread() {
            this.alive = true;
        }

        @Override
        public void run() {

            while (alive) {
                try {
                    PackResponse packResponse = exchangeClient.exchange();
                    long newPollRate = packResponse != null ? packResponse.getNewPollRate() : 0;
                    if (newPollRate > 0 && pollRate != newPollRate) {
                        log.info("Poll rate {} changed to {} ms", pollRate, newPollRate);
                        pollRate = newPollRate;
                    }
                } catch (IOException e) {
                    log.error("Error in exchange client", e);
                    throw new TechnicalException("IOException", e);
                } catch (NodeNotFound e) {
                    log.warn("Agent {} didn't registered on current coordinator! Reset agent registration.", nodeContext.getId());
                    exchangeClient.getPackExchanger().clean();
                    AgentStarter.resetAgent(Agent.this);
                } catch (Throwable e) {
                    alive = false;
                    log.error("Agent "+nodeContext.getId()+" got an exception from coordinator", e);
                }

                log.debug("Pack exchange completed. Poll rate on agent {} is {} ms", nodeContext.getId(), pollRate);
                TimeUtils.sleepMillis(pollRate);
            }
        }
    }
}