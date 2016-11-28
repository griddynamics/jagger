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

package com.griddynamics.jagger.storage.fs.timelog;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.griddynamics.jagger.exception.TechnicalException;
import org.apache.log4j.Logger;

/**
 * This class in intended for writing logs in format (timestamp | value1 | value2 | ...)
 * where values are floating point numbers.
 *
 */
public class NumericalTimeLogWriter {

    private static final Logger log = Logger.getLogger(NumericalTimeLogWriter.class);

    private String delimiter = "|";
    private int maxBufferSize = 10000;

    private final BlockingQueue<Entry> buffer = new ArrayBlockingQueue<Entry>(maxBufferSize);
    private final AtomicBoolean flushActive = new AtomicBoolean(false);

    private List<DataOutputStream> streams;

    /**
     * Creates log writer for multiple output stream
     * @param streams list of output streams. Data output will be randomly distributed among streams
     */
    public NumericalTimeLogWriter(List<DataOutputStream> streams) {
        this.streams = streams;
    }

    public NumericalTimeLogWriter(DataOutputStream stream) {
        this(Collections.singletonList(stream));
    }

    /**
     * Add new entry to the output queue.
     */
    public void write(long timestamp, double[] values) {
        try {
            buffer.put(new Entry(timestamp, values));
        } catch (InterruptedException e) {
            throw new TechnicalException(e);
        }
    }

    private void writeEntry(Entry entry, DataOutputStream stream) {
        try {
            String line = entry.timestamp + delimiter;
            for(Object value : entry.values) {
                line += value + delimiter;
            }
            stream.writeUTF(line + '\n');
        } catch(IOException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Starts background process of data flushing to the output streams. Should be called before writing -
     * otherwise writing will be blocked after {@link #maxBufferSize} writes.
     */
    public void startFlushing() {
        flushActive.set(true);

        for(int  i = 0; i < streams.size(); i++) {
            final int streamId = i;
            new Thread() {
                public void run() {
                    while(flushActive.get()) {
                        try {
                            Entry entry = buffer.take();
                            writeEntry(entry, streams.get(streamId));
                        } catch (InterruptedException e) {
                            log.error("Flushing thread was interrupted", e);
                        }
                    }
                }
            }.start();
        }
    }

    private void terminateFlush() {
        flushActive.set(false);
    }

    /**
     * Stop flushing threads, drain buffer and close all output data streams.
     */
    public void close() {
        terminateFlush();

        try {
            while(!buffer.isEmpty()) {
                Entry entry = buffer.take();
                writeEntry(entry, streams.get(0));
            }
        } catch (InterruptedException e) {
            log.error("Final flushing was interrupted. About [" + buffer.size() + "] entries can be lost.", e);
        }

        for(DataOutputStream stream : streams) {
            try {
                stream.close();
            } catch (Exception e) {
                log.error("Failed to close stream", e);
            }
        }
    }

    private static class Entry {
        public long timestamp;
        public double values[];

        public Entry(long timestamp, double values[]) {
            this.timestamp = timestamp;
            this.values = values;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

}

