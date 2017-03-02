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

package com.griddynamics.jagger.providers;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.AbstractIterator;
import com.griddynamics.jagger.providers.creators.ObjectCreator;
import com.griddynamics.jagger.providers.creators.StringCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Reads data from files
 * @author Nikolay Musienko
 * @n
 * @par Details:
 * @details Reads data from files, which uses special delimiter.
 * @n
 * For example : if your file contains next info -
 * @n
 * @code
 * NYC 89,5$|MOS 67,4$|SPB 109,6$
 * @endcode
 * @n
 * And your delimiter equals '|'
 * @n
 * Provider will create 3 records - 'NYC 89,5$', 'MOS 67,4$', 'SPB 109,6$'.
 * You can create a special object creator, which will translate such records to your java object.
 * This object may looks like this
 * @n
 * @code
 * class InternationalTicket{
 *     private String city;
 *     private double price;
 *     ....
 *     ....
 * }
 * @endcode
 */
public class FileProvider<T> implements Iterable<T>, Serializable {

    private String path;
    private String delimeter;
    private ObjectCreator<T> objectCreator;

    /** Creates new file provider
     * @author Nikolay Musienko
     * @n
     * @param path - full name of file
     * @param delimeter - a symbol, which separate data
     * @param objectCreator - translate data to java objects*/
    public FileProvider(String path, String delimeter, ObjectCreator<T> objectCreator) {
        this.path = path;
        this.delimeter = delimeter;
        this.objectCreator = objectCreator;
    }

    /** Creates new file provider.
     * @author Nikolay Musienko
     * @n
     * @par Details:
     * @details Uses system line separator as default delimiter
     *
     * @param path - full name of file
     * @param objectCreator - translate data to java objects*/
    public FileProvider(String path, ObjectCreator<T> objectCreator) {
        this(path, System.getProperty("line.separator"), objectCreator);
    }

    /** Creates new file provider
     * @author Nikolay Musienko
     * @n
     * @par Details:
     * @details Uses StringCreator as default objectCreator
     *
     * @param path - full name of file*/
    public FileProvider(String path) {
        this(path, (ObjectCreator<T>) new StringCreator());
    }

    /** Returns delimiter
     * @author Nikolay Musienko
     * @return delimiter*/
    public String getDelimeter() {
        return delimeter;
    }

    /** Set provider delimiter
     * @author Nikolay Musienko
     * @param delimeter - provider delimiter*/
    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }

    /** Returns object creator, which translate row data to java objects
     * @author Nikolay Musienko
     * @return object creator*/
    public ObjectCreator<T> getObjectCreator() {
        return objectCreator;
    }

    /** Set object creator, which translate row data to java objects
     * @author Nikolay Musienko
     * @param objectCreator - object creator*/
    public void setObjectCreator(ObjectCreator<T> objectCreator) {
        this.objectCreator = objectCreator;
    }

    /** Returns file name
     * @author Nikolay Musienko
     * @return file name*/
    public String getPath() {
        return path;
    }

    /** Set file name
     * @author Nikolay Musienko
     * @param filePath - full name of file*/
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

