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

package com.griddynamics.jagger.storage.fs.hdfs;

import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.storage.fs.hdfs.utils.HadoopUtils;
import com.griddynamics.jagger.util.BlockingBean;
import com.griddynamics.jagger.util.ThreadExecutorUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class HDFSClientBean implements BlockingBean, HDFSClient {

    private static Logger log = Logger.getLogger(HDFSClientBean.class);

    private volatile boolean ready = false;

    private Properties startupProperties;

    private FileSystem fileSystem;

    private Configuration configuration;

    public void initialize() {

        configuration = HadoopUtils.toConfiguration(startupProperties);

        ThreadExecutorUtil.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        fileSystem = FileSystem.get(configuration);
                        ready = true;
                    } catch (Exception e) {
                        log.warn("Failed to connect to HDFS server: " + e.getMessage());
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e1) {
                            log.warn("Interrupted");
                            return;
                        }
                    }
                } while (!ready);
            }
        });
    }

    public void close() {
        try {
            if (this.fileSystem != null) {
                this.fileSystem.close();
            } else {
                ready = true;
            }
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public FileSystem getFileSystem() {
        return this.fileSystem;
    }

    public void setStartupProperties(Properties startupProperties) {
        this.startupProperties = startupProperties;
    }

    @Override
    public boolean isBlock() {
        return !this.ready;
    }

    @Override
    public void waitForReady() {
    }
}
