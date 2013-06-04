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

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import com.griddynamics.jagger.providers.creators.ObjectCreator;
import com.griddynamics.jagger.providers.creators.StringCreator;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * @author Nikolay Musienko
 *         Date: 22.04.13
 */

public class FileProvider<T> implements Iterable<T>, Serializable {

    private String path;
    private String delimeter;
    private ObjectCreator<T> objectCreator;

    public FileProvider(String path, String delimeter, ObjectCreator<T> objectCreator) {
        this.path = path;
        this.delimeter = delimeter;
        this.objectCreator = objectCreator;
    }

    public FileProvider(String path, ObjectCreator<T> objectCreator) {
        this(path, System.getProperty("line.separator"), objectCreator);
    }

    public FileProvider(String path) {
        this(path, (ObjectCreator<T>) new StringCreator());
    }

    public String getDelimeter() {
        return delimeter;
    }

    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }

    public ObjectCreator<T> getObjectCreator() {
        return objectCreator;
    }

    public void setObjectCreator(ObjectCreator<T> objectCreator) {
        this.objectCreator = objectCreator;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String filePath) {
        this.path = filePath;
    }

    public Iterator<T> iterator() {
        Preconditions.checkNotNull(delimeter);
        Preconditions.checkNotNull(path);
        Preconditions.checkNotNull(objectCreator);

        return new AbstractIterator<T>() {

            private Scanner scanner;

            {
                init();
            }

            private void init() {
                try {
                    scanner = new Scanner(new File(path)).useDelimiter(delimeter);
                } catch (FileNotFoundException e) {
                    throw Throwables.propagate(e);
                }
            }
            @Override
            protected T computeNext() {
                try {
                    return objectCreator.createObject(scanner.next());
                } catch (NoSuchElementException e) {
                    return endOfData();
                }
            }
        };
    }
}