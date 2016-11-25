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

import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class BufferedLogReader implements LogReader {
    private static final Logger log = LoggerFactory.getLogger(BufferedLogReader.class);
    private FileStorage fileStorage;

    @Override
    public <T> FileReader<T> read(String sessionId, String logOwner, String kernelId, Class<T> clazz) {
        Namespace path = Namespace.of(sessionId, logOwner, kernelId);
        return read(path.toString(), clazz);
    }

    @Override
    public <T> FileReader<T> read(String path, Class<T> clazz) {
        InputStream in;
        try {
            if (!fileStorage.exists(path)) {
                throw new IllegalArgumentException("Path " + path + " doesn't exist");
            }
            in = new BufferedInputStream(fileStorage.open(path));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        return read(in, clazz);
    }

    protected <T> FileReader<T> read(final InputStream is, final Class<T> clazz) {
        return new FileReader<T>() {
            final IteratorImpl<T> iterator = new IteratorImpl<T>(getInput(is), clazz);

            @Override
            public Iterator<T> iterator() {
                return iterator;
            }

            @Override
            public void close() {
                try {
                    is.close();
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            }
        };
    }

    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    protected abstract LogReaderInput getInput(InputStream in);

    protected interface LogReaderInput {
        Object readObject() throws IOException;
    }

    /*package*/ static class IteratorImpl<T> implements Iterator<T> {

        private final LogReaderInput lr;
        private final Class<T> clazz;

        private T next;
        private boolean isIterationOver = false;

        public IteratorImpl(LogReaderInput lr, Class<T> clazz) {
            this.lr = lr;
            this.clazz = clazz;
        }

        @Override
        public boolean hasNext() {
            if (isIterationOver) {
                return false;
            }

            try {
                Object entry;
                do {
                    entry = lr.readObject();
                } while (entry == null); // TODO some bug with JBoss reader
                if (!clazz.isInstance(entry)) {
                    throw new IllegalStateException("entry " + entry + " is not instance of class " + clazz);
                }
                next = clazz.cast(entry);
            } catch (EOFException e) {
                isIterationOver = true;
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
            return !isIterationOver;
        }

        @Override
        public T next() {
            if (isIterationOver) {
                throw new NoSuchElementException();
            }
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Read only iterator!");
        }
    }
}
