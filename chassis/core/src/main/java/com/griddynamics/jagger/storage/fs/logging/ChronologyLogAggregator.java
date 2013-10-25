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

import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.io.Closeables;
import com.griddynamics.jagger.storage.FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Alexey Kiselyov
 *         Date: 20.07.11
 */
public class ChronologyLogAggregator implements LogAggregator {
    private Logger log = LoggerFactory.getLogger(ChronologyLogAggregator.class);

    private FileStorage fileStorage;

    private LogReader logReader;

    private LogWriter logWriter;

    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }

    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    @Override
    public AggregationInfo chronology(String dir, String targetFile) throws IOException {
        log.info("Try to aggregate {} into file {}", dir, targetFile);
        Collection<Iterable<LogEntry>> readers = new ArrayList<Iterable<LogEntry>>();
        Set<String> fileNameList = fileStorage.getFileNameList(dir);
        if (fileNameList.isEmpty()) {
            log.info("Nothing to aggregate. Directory {} is empty.", dir);
            fileStorage.create(targetFile);
            return new AggregationInfo(0, 0, 0);
        }
        for (String fileName : fileNameList) {
            try {
                readers.add(logReader.read(fileName, LogEntry.class));
            } catch (Exception e) {
                // TODO
                log.warn(e.getMessage(), e);
            }
        }

        int count = 0;
        long minTime = 0;
        long maxTime = 0;
        BufferedLogWriter.LogWriterOutput objectOutput=null;
        try {
            if (fileStorage.delete(targetFile, false)) {
                log.warn("Target file {} was not deleted!", targetFile);
            }
            objectOutput= logWriter.getOutput(fileStorage.create(targetFile));

            MinMaxPriorityQueue<StreamInfo> queue = MinMaxPriorityQueue.create();
            for (Iterable<LogEntry> inputStream : readers) {
                LogEntry logEntry;
                Iterator<LogEntry> it = inputStream.iterator();
                if (it.hasNext()) {
                    logEntry = it.next();
                } else {
                    continue;
                } 
                queue.add(new StreamInfo(it, logEntry));
            }

            while (!queue.isEmpty()) {
                StreamInfo<LogEntry> streamInfo = queue.removeFirst();
                objectOutput.writeObject(streamInfo.lastLogEntry);

                if (count == 0) {
                    minTime = streamInfo.lastLogEntry.getTime();
                    maxTime = streamInfo.lastLogEntry.getTime();
                } else {
                    maxTime = streamInfo.lastLogEntry.getTime();
                }

                count++;
                LogEntry logEntry;
                if (streamInfo.stream.hasNext()) {
                    logEntry = streamInfo.stream.next();
                } else {
                    continue;
                }
                streamInfo.lastLogEntry = logEntry;
                queue.add(streamInfo);
            }
        } finally {
            Closeables.closeQuietly(objectOutput);
        }

        return new AggregationInfo(minTime, maxTime, count);
    }

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }
}
