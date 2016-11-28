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

package com.griddynamics.jagger.storage;

import com.griddynamics.jagger.AttendantServer;

import java.io.*;
import java.util.Collection;
import java.util.Set;

/**
 * @author Alexey Kiselyov
 *         Date: 19.07.11
 */
public interface FileStorage {

    Collection<AttendantServer> getAttendantServers();

    /**
     * Check if exists.
     *
     * @param path the path to file
     * @return true if exists else false
     */
    boolean exists(String path) throws IOException;

    /**
     * List the names of the files/directories in the given path if the path is a directory.
     *
     * @param path given path
     * @return the file names of the files/directories in the given patch returns null, if
     *         Path <code>path</code> does not exist in the <code>FileStorage</code>
     */
    Set<String> getFileNameList(String path) throws IOException;

    /**
     * Open stream to write to append to an existing file (optional operation).
     *
     * @param path the existing file to be appended.
     * @return OutputStream opened file
     * @throws FileNotFoundException
     */
    OutputStream append(String path) throws IOException;

    /**
     * Open stream to write to an existing file (optional operation). Files are overwritten by default.
     *
     * @param path
     * @return OutputStream created file
     * @throws FileNotFoundException
     */
    OutputStream create(String path) throws IOException;

    InputStream open(String fileName) throws IOException;

    /**
     * Delete a file.
     *
     * @param path      the path to delete.
     * @param recursive if path is a directory and set to true, the directory is deleted else throws an exception.
     *                  In case of a file the recursive can be set to either true or false.
     * @return true if delete is successful else false.
     * @throws IOException
     */
    boolean delete(String path, boolean recursive) throws IOException;
}
