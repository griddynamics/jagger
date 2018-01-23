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

package com.griddynamics.jagger.storage.fs;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.griddynamics.jagger.AttendantServer;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.fs.hdfs.HDFSClient;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexey Kiselyov
 *         Date: 21.07.11
 */
public class HdfsStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(HdfsStorage.class);
    private HDFSClient hdfsClient;

    private Collection<AttendantServer> attendantServers;

    @Required
    public void setAttendantServers(Collection<AttendantServer> attendantServers) {
        this.attendantServers = attendantServers;
    }

    @Override
    public Collection<AttendantServer> getAttendantServers() {
        return this.attendantServers;
    }

    @Override
    public boolean exists(String path) throws IOException {
        log.debug("exists [{}]", path);
        return hdfsClient.getFileSystem().exists(new Path(path));
    }

    @Override
    public Set<String> getFileNameList(String path) throws IOException {
        log.debug("getFileNameList [{}]", path);
        FileStatus[] listStatus = hdfsClient.getFileSystem().listStatus(new Path(path));
        if(listStatus==null) return Collections.emptySet();
        return new HashSet<String>(Collections2.<FileStatus, String>transform(
                Arrays.<FileStatus>asList(listStatus),
                new Function<FileStatus, String>() {
                    @Override
                    public String apply(FileStatus input) {
                        return input.getPath().toString();
                    }
                }
        ));
    }

    @Override
    public OutputStream append(String path) throws IOException {
        log.debug("append [{}]", path);
        return hdfsClient.getFileSystem().append(new Path(path));
    }

    @Override
    public OutputStream create(String path) throws IOException {
        log.debug("create [{}]", path);
        return hdfsClient.getFileSystem().create(new Path(path));
    }

    @Override
    public InputStream open(String fileName) throws IOException {
        log.debug("open [{}]", fileName);
        return hdfsClient.getFileSystem().open(new Path(fileName));
    }

    @Override
    public boolean delete(String path, boolean recursive) throws IOException {
        log.debug("delete [{}]", path);
        return hdfsClient.getFileSystem().delete(new Path(path), recursive);
    }

    @Required
    public void setHdfsClient(HDFSClient hdfsClient) {
        this.hdfsClient = hdfsClient;
    }
}
