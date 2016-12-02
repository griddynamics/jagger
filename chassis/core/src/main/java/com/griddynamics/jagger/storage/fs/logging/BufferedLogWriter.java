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

package com.griddynamics.jagger.storage.fs.logging;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Closeables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alexey Kiselyov, Vladimir Shulga
 * Date: 20.07.11
 */
public abstract class BufferedLogWriter implements LogWriter {
    private final Logger log = LoggerFactory.getLogger(BufferedLogWriter.class);

    private final int flushSize;
    private final int bufferSize;
    private final Log FLUSH_LOG = new Log("FLUSH_LOG", null);

    private FileStorage fileStorage;
    private ArrayBlockingQueue<Log> buffer;
    private Object lock = new Object();
    private ExecutorService executor = Executors.newSingleThreadExecutor(
                                                    new ThreadFactoryBuilder().setDaemon(true).build());

    public BufferedLogWriter(int flushSize, int bufferSize, FileStorage fileStorage){
        this.flushSize = flushSize;
        this.fileStorage = fileStorage;
        this.bufferSize = bufferSize;

        buffer = new ArrayBlockingQueue<Log>(bufferSize, false);
        executor.submit(new FlushingWriter());
    }

    @Override
    public void log(String sessionId, String dir, String kernelId, Serializable logEntry) {
        String path = Namespace.of(sessionId, dir, kernelId).toString();
        log(path, logEntry);
    }

    public void log(String path, Serializable logEntry) {
        Preconditions.checkNotNull(logEntry, "Null is not supported");
        try {
            buffer.put(new Log(path, logEntry));
        } catch (InterruptedException e) {
            log.error("Failed to write {} by path: {}", new Object[]{logEntry, path}, e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public synchronized void flush() {
        try {
            synchronized (lock){
                buffer.put(FLUSH_LOG);

                lock.wait();
            }
        } catch (InterruptedException e) {
            log.error("Failed during flushing", e);
            Thread.currentThread().interrupt();
        }
    }

    private class FlushingWriter implements Runnable{

        @Override
        public void run() {
            ArrayList<Log> temp = new ArrayList<Log>(flushSize+flushSize/2);

            boolean shutdown = false;

            while (!shutdown){

                Multimap<String, Serializable> map = LinkedHashMultimap.create();
                int size = 0;
                boolean need_to_flash = false;

                try {

                    while (size < flushSize){
                        temp.add(buffer.take());
                        size++;

                        size += buffer.drainTo(temp);
                        need_to_flash = drainToMap(temp, map);

                        if (need_to_flash){
                            break;
                        }
                    }


                } catch (InterruptedException e) {
                    shutdown = true;

                }finally {
                    writeToFileStorage(map, need_to_flash);
                }
            }
        }

        private boolean drainToMap(Collection<Log> logs, Multimap<String, Serializable> map){
            boolean need_to_flush = false;

            for (Log current : logs){
                if (isFlushLog(current)){
                    need_to_flush = true;
                    continue;
                }
                map.put(current.getPath(), current.getValue());
            }
            logs.clear();
            return need_to_flush;
        }

        private boolean isFlushLog(Log current){
            return current == FLUSH_LOG;
        }

        private void writeToFileStorage(Multimap<String, Serializable> map, boolean unlockFlush){
            long startTime = System.currentTimeMillis();
            log.debug("Flush queue. flushId {}. Current queue size is {}", startTime, map.size());

            try {
                for (String logFilePath : map.keySet()) {
                    Collection<Serializable> fileQueue = map.get(logFilePath);
                    if (fileQueue.isEmpty()) {
                        continue;
                    }
                    OutputStream os = null;
                    LogWriterOutput objectOutput = null;
                    try {
                        if (fileStorage.exists(logFilePath)) {
                            os = fileStorage.append(logFilePath);
                        } else {
                            os = fileStorage.create(logFilePath);
                        }
                        os = new BufferedOutputStream(os);
                        objectOutput = getOutput(os);
                        for(Serializable serializable: fileQueue){
                            objectOutput.writeObject(serializable);
                        }
                    }catch (Exception e){
                        log.error("Error during saving data with path "+ logFilePath + " to fileStorage", e);
                    } finally {
                        try {
                            Closeables.closeQuietly(objectOutput);
                            Closeables.close(os, true);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }
            } finally {
                log.debug("Flush queue finished. flushId {}.Flush took: {}", startTime, (System.currentTimeMillis() - startTime));

                if (unlockFlush)
                    synchronized (lock){
                        lock.notifyAll();
                    }
            }
        }
    }

    private class Log{
        private String path;
        private Serializable value;

        public Log(String path, Serializable value){
            this.path = path;
            this.value = value;
        }

        public String getPath(){
            return path;
        }

        public Serializable getValue(){
            return value;
        }
    }
}