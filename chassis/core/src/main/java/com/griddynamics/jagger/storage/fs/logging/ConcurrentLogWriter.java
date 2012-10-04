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

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.common.io.Flushables;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.Namespace;
import com.griddynamics.jagger.util.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author imamontov
 */
public class ConcurrentLogWriter implements LogWriter {

    private static final Pair<String, Serializable> PAIR = Pair.of("", (Serializable) "");
    private final BlockingQueue<Pair<String, Serializable>> queue = new LinkedBlockingDeque<Pair<String, Serializable>>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private FileStorage fileStorage;
    private Future<?> taskFuture;

    protected ConcurrentLogWriter() {
    }

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void log(String sessionId, String dir, String kernelId, Serializable logEntry) {
        String path = Namespace.of(sessionId, dir, kernelId).toString();
        log(path, logEntry);
    }

    @Override
    public void log(String path, Serializable logEntry) {
        try {
            queue.put(Pair.of(path, logEntry));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void flush() {
    }

    public void start() {
        taskFuture = executor.submit(new LoggerThread(queue));
    }

    public void stop() {
        try {
            queue.put(PAIR);
            taskFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private final class LoggerThread implements Runnable {

        private final BlockingQueue<Pair<String, Serializable>> queue;
        private boolean isInterrupted = false;
        private Map<String, HessianOutputStream> streams = Maps.newHashMap();

        private LoggerThread(BlockingQueue<Pair<String, Serializable>> queue) {
            this.queue = queue;
        }

        private HessianOutputStream getOrOpen(String name) throws IOException {
            HessianOutputStream outputStream = streams.get(name);
            OutputStream os;
            if (outputStream == null) {
                if (fileStorage.exists(name)) {
                    os = fileStorage.append(name);
                } else {
                    os = fileStorage.create(name);
                }
                outputStream = new HessianOutputStream(new BufferedOutputStream(os));
                streams.put(name, outputStream);
            }
            return outputStream;
        }

        @Override
        public void run() {
            while (!isInterrupted || queue.size() > 0) {
                Pair<String, Serializable> item;
                try {
                    item = queue.take();
                    if (item == PAIR) {
                        break;
                    }
                    String logFilePath = item.getFirst();
                    HessianOutputStream os = getOrOpen(logFilePath);
                    os.writeObject(item.getSecond());
                } catch (InterruptedException e) {
                    isInterrupted = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            flushAndClose();
        }

        public void flushAndClose() {
            for (HessianOutputStream outputStream : streams.values()) {
                Flushables.flushQuietly(outputStream);
                Closeables.closeQuietly(outputStream);
            }
            streams.clear();
        }
    }
}
