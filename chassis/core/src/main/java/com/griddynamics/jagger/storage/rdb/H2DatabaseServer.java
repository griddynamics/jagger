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

package com.griddynamics.jagger.storage.rdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.h2.tools.Server;

import com.griddynamics.jagger.Terminable;
import com.griddynamics.jagger.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2DatabaseServer implements Runnable, Terminable {
    private static final Logger log = LoggerFactory.getLogger(H2DatabaseServer.class);

    private Server server;

    private Properties startupProperties;

    @Override
    public void terminate() {
        log.debug("termination signal received");
        if(server != null) {
            server.stop();
        }
    }

    @Override
    public void run() {
        log.debug("H2DatabaseServer stared");
        try {
            String[] assembleCommandLine = assembleCommandLine();
            server = Server.createTcpServer(assembleCommandLine).start();
        } catch(SQLException e) {
            throw new TechnicalException(e);
        }
    }

    private String[] assembleCommandLine() {
        List<String> result = new ArrayList<String>();

        for(Object key : startupProperties.keySet()) {
            result.add("-" + key);
            String value = startupProperties.getProperty(key.toString());
            if(value != null && value.trim().length() != 0) {
                result.add(value.trim());
            }
        }

        return result.toArray(new String[result.size()]);
    }

    public void setStartupProperties(Properties startupProperties) {
        this.startupProperties = startupProperties;
    }
}
