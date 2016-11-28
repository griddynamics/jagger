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

package com.griddynamics.jagger.coordinator.http.server;

import com.griddynamics.jagger.coordinator.http.*;
import com.griddynamics.jagger.util.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Responsible for exchanging of packages between nodes via http protocol..
 *
 * @author Mairbek Khadikov
 */
public class PackExchangeServlet extends HttpServlet {
    private static final String PARAM = "message";

    private static final Logger log = LoggerFactory.getLogger(PackExchangeServlet.class);

    private TransportHandler transportHandler;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Pack exchange requested");

        try {
            PackRequest packRequest = SerializationUtils.fromString(req.getParameter(PARAM));

            PackExchanger exchanger = transportHandler.getExchanger(packRequest.getNode());

            if (exchanger == null) {
                throw new NodeNotFound("Node with id " + packRequest.getNode() + " is not registered");
            }

            log.debug("Processing incoming pack request {}", packRequest);
            exchanger.process(packRequest.getPack());
            log.debug("Pack request procession completed");

            log.debug("Retrieving out pack");
            Pack pack = exchanger.retrieve();
            log.debug("Out pack {}", pack);

            log.debug("Going to send pack");
            ServletUtil.sendResponseObject(resp, PackResponse.success(pack));
            log.debug("Pack successfully send");
        } catch (Throwable e) {
            log.warn("Pack exchange failed", e);
            ServletUtil.sendResponseObject(resp, PackResponse.fail(e));
        }
    }

    @Required
    public void setTransportHandler(TransportHandler transportHandler) {
        this.transportHandler = transportHandler;
    }
}
