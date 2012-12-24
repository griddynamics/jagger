/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.coordinator.async.AsyncRunner;
import com.griddynamics.jagger.coordinator.http.*;
import com.griddynamics.jagger.util.SerializationUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
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
        if (out.getCommands().isEmpty() && out.getResults().isEmpty()){
            //Nothing to send
            return null;
        }
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
        } catch (SocketException e) {
            packExchanger.getCommandsToSend().addAll(out.getCommands());
            packExchanger.getResultsToSend().addAll(out.getResults());
            log.warn("Connection lost! Pack {} will be sent again in the next exchange session!", out);
        } catch (IOException e) {
            log.error("IOException during deserialization of this data ({})", str);
            throw e;
        }
        return packResponse;
    }

    private String exchangeData(String url, Serializable obj) throws IOException {
        PostMethod method = new PostMethod(urlBase + url);
        NameValuePair pair = new NameValuePair();
        pair.setName(MESSAGE);
        pair.setValue(SerializationUtils.toString(obj));

        method.setQueryString(new NameValuePair[]{pair});
        try {
            int returnCode = httpClient.executeMethod(method);
            log.debug("Exchange response code {}", returnCode);
            return method.getResponseBodyAsString();
        } finally {
            try {
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
