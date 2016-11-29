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

package com.griddynamics.jagger.coordinator.http.client;

import com.google.common.base.Throwables;
import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.async.AsyncRunner;
import com.griddynamics.jagger.coordinator.http.*;
import com.griddynamics.jagger.util.SerializationUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;

public class ExchangeClient {
    private static final Logger log = LoggerFactory.getLogger(ExchangeClient.class);

    private static final String MESSAGE = "message";
    private HttpClient httpClient;
    private String urlBase;
    private String urlExchangePack;
    private String urlRegistration;
    private NodeContext nodeContext;
    private DefaultPackExchanger packExchanger;

    public ExchangeClient(Executor executor, AsyncRunner<Command<Serializable>, Serializable> incomingCommandRunner,
                          NodeContext nodeContext) {
        this.packExchanger = new DefaultPackExchanger(executor, incomingCommandRunner);
        this.nodeContext = nodeContext;
    }

    public DefaultPackExchanger getPackExchanger() {
        return packExchanger;
    }

    public Ack registerNode(RegistrationPack registrationPack) throws IOException {
        return SerializationUtils.fromString(exchangeData(urlRegistration, registrationPack));
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setUrlExchangePack(String urlExchangePack) {
        this.urlExchangePack = urlExchangePack;
    }

    public void setUrlRegistration(String urlRegistration) {
        this.urlRegistration = urlRegistration;
    }

    public void setNodeContext(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    public PackResponse exchange() throws Throwable {
        log.debug("Exchange requested from agent {}", nodeContext.getId());
        Pack out = packExchanger.retrieve();
        log.debug("Going to send pack {} from agent {}", out, nodeContext.getId());
        PackRequest request = PackRequest.create(nodeContext.getId(), out);
        PackResponse packResponse = null;
        String str = null;
        try {
            str = exchangeData(urlExchangePack, request);
            packResponse = SerializationUtils.fromString(str);
            log.debug("Pack response {} from agent {}", packResponse, nodeContext.getId());
            if (Ack.FAIL.equals(packResponse.getAck())) {
                log.warn("Pack retrieving failed! Agent {}", nodeContext.getId());
                throw packResponse.getError();
            }
            Pack in = packResponse.getPack();
            packExchanger.process(in);
        } catch (IOException e) {
            if (!out.isEmpty()){
                packExchanger.getCommandsToSend().addAll(out.getCommands());
                packExchanger.getResultsToSend().addAll(out.getResults());
                log.warn("Connection lost! Pack {} will be sent again in the next exchange session!", out);
            }else{
                log.warn("Connection lost! Waiting for the next exchange session!");
            }
            log.warn(e.toString());
        }
        return packResponse;
    }

    private String exchangeData(String url, Serializable obj) throws IOException {
        HttpPost method = new HttpPost(urlBase + url);
        URIBuilder uri = new URIBuilder(URI.create(urlBase + url));
        uri.setParameter(MESSAGE, SerializationUtils.toString(obj));

        HttpEntity entity = null;
        try {
            method.setURI(uri.build());
            HttpResponse response = httpClient.execute(method);
            int returnCode = response.getStatusLine().getStatusCode();
            entity = response.getEntity();
            log.debug("Exchange response code {}", returnCode);
            return EntityUtils.toString(entity);
        } catch (URISyntaxException e) {
            log.error("URIException while building uri with: \nurlBase: " + urlBase +
                    "\nurl: " + url + "\nobj: " + obj.toString());
            throw Throwables.propagate(e);
        } catch (IOException e) {
            log.error("Exception during HTTP request execution "+e.toString());
            throw e;
        } finally {
            try {
                EntityUtils.consumeQuietly(entity);
                method.releaseConnection();
            } catch (Throwable e) {
                log.error("Cannot release connection", e);
            }
        }
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }
}
