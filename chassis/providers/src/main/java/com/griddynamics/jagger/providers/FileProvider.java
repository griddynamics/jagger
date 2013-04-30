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

package com.griddynamics.jagger.providers;

import com.google.common.base.Throwables;
import com.griddynamics.jagger.exception.TechnicalException;
import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Nikolay Musienko
 *         Date: 22.04.13
 */

public class FileProvider implements Iterable<String> {

    private String path;

    public FileProvider(String filePath) {
        this.path = filePath;
    }

    public FileProvider() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String filePath) {
        this.path = filePath;
    }

    public Iterator<String> iterator() {

        return new Iterator<String>() {

            private BufferedReader reader;
            String next;
            private boolean loaded = false;

            {
                init();
            }

            private void init(){
                if (path == null) {
                    throw new TechnicalException("File path can't be NULL!");
                }
                try {
                    reader = new BufferedReader(new FileReader(new File(path)));
                } catch (FileNotFoundException e) {
                    throw Throwables.propagate(e);
                }
                next = readNext();
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public String next() {
                String ret = next;
                if(ret == null){
                    throw new NoSuchElementException("Iteration has no more elements");
                }
                next = readNext();
                return ret;
            }

            private String readNext() {
                if (loaded) {
                    return null;
                }
                try {
                    return reader.readLine();
                } catch (EOFException e) {
                    loaded = true;
                    return null;
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Read only iterator!");
            }
        };
    }
}