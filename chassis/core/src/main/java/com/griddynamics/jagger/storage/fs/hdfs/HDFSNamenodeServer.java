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

package com.griddynamics.jagger.storage.fs.hdfs;

import com.griddynamics.jagger.AttendantServer;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.storage.fs.hdfs.utils.HadoopUtils;
import org.apache.hadoop.hdfs.protocol.FSConstants.SafeModeAction;
import org.apache.hadoop.hdfs.server.common.InconsistentFSStateException;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class HDFSNamenodeServer implements AttendantServer {
    private static Logger log = Logger.getLogger(HDFSNamenodeServer.class);

    private NameNode nameNode;
    private Properties startupProperties;

    public void start() {
        log.info("Starting NameNode...");
        try {
            try {
                nameNode = NameNode.createNameNode(null, HadoopUtils.toConfiguration(startupProperties));
            } catch (InconsistentFSStateException e) {
                log.info("HDFS is in inconsistent state. Reformatting...");
                formatStorage();
                nameNode = NameNode.createNameNode(null, HadoopUtils.toConfiguration(startupProperties));
            }
            nameNode.setSafeMode(SafeModeAction.SAFEMODE_LEAVE);
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
        log.info("NameNode started.");
    }

    public void shutdown() {
        if (nameNode != null) {
            nameNode.stop();
        }
    }

    public void formatStorage() {
        log.info("Format Storage...");
        try {
            NameNode.format(HadoopUtils.toConfiguration(startupProperties));
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
        log.info("Storage formatted");
    }

    public void setStartupProperties(Properties startupProperties) {
        this.startupProperties = startupProperties;
    }

    @Override
    public void initialize() {
        //
    }

    @Override
    public void run() {
        //
    }

    @Override
    public void terminate() {
        //
    }
}
