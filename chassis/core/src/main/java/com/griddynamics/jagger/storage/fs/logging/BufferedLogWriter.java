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

package com.griddynamics.jagger.storage.fs.logging;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author Alexey Kiselyov, Vladimir Shulga
 *         Date: 20.07.11
 */
public abstract class BufferedLogWriter implements LogWriter {

    private final Logger log = LoggerFactory.getLogger(BufferedLogWriter.class);

    private final Multimap<String, Serializable> queue = LinkedHashMultimap.create();
    private FileStorage fileStorage;
    private int flushSize;
    private volatile boolean isFlushInProgress;

    @Override
    public void log(String sessionId, String dir, String kernelId, Serializable logEntry) {
        String path = Namespace.of(sessionId, dir, kernelId).toString();
        log(path, logEntry);
    }

    @Override
    public void log(String path, Serializable logEntry) {
        Preconditions.checkNotNull(logEntry, "Null is not supported");
        
        synchronized (queue) {
            queue.put(path, logEntry);
        }

        if (!isFlushInProgress && (queue.size() >= flushSize)) {
            flush();
        }
    }

    @Override
    public synchronized void flush() {
        long startTime = System.currentTimeMillis();
        try {
            isFlushInProgress = true;
            Multimap<String, Serializable> forFlush;
            synchronized (queue) {
                forFlush = LinkedHashMultimap.create(queue);
                queue.clear();
            }

            log.debug("Flush queue. flushId {}. Current queue size is {}", startTime, forFlush.size());
            for (String logFilePath : forFlush.keySet()) {
                Collection<Serializable> fileQueue = forFlush.get(logFilePath);
                if (fileQueue.isEmpty()) {
                    continue;
                }
                OutputStream os = null;
                try {
                    if (this.fileStorage.exists(logFilePath)) {
                        os = this.fileStorage.append(logFilePath);
                    } else {
                        os = this.fileStorage.create(logFilePath);
                    }
                    os = new BufferedOutputStream(os);
                    log(fileQueue, os);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } finally {
            log.debug("Flush queue finished. flushId {}. Flush took: {}", startTime, (System.currentTimeMillis() - startTime));
            isFlushInProgress = false;
        }
    }

    protected abstract void log(Collection<Serializable> fileQueue, OutputStream os) throws IOException;

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Required
    public void setFlushSize(int flushSize) {
        this.flushSize = flushSize;
    }
}
