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

package com.griddynamics.jagger.storage.fs;

import com.griddynamics.jagger.AttendantServer;
import com.griddynamics.jagger.storage.FileStorage;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexey Kiselyov
 *         Date: 19.07.11
 */
public class LocalFileStorage implements FileStorage {

    private static final String CONTEXT = ".context";
    private String workspace;

    public LocalFileStorage() {
    }

    @Override
    public Collection<AttendantServer> getAttendantServers() {
        return Collections.<AttendantServer>emptySet();
    }

    @Override
    public boolean exists(String path) {
        return (new File(this.workspace, path)).exists();
    }

    @Override
    public Set<String> getFileNameList(String path) {
        final String root = new File(this.workspace).getPath();
        File[] files = (new File(this.workspace, path)).listFiles();
        if (files == null)
            return Collections.<String>emptySet();
        return new HashSet<String>(Collections2.<File, String>transform(
                Arrays.<File>asList(files),
                new Function<File, String>() {
                    @Override
                    public String apply(File input) {
                        return input.getPath().substring(root.length());
                    }
                }
        ));
    }

    @Override
    public OutputStream append(String path) throws IOException {
        return updateFile(path, true);
    }

    @Override
    public OutputStream create(String path) throws FileNotFoundException {
        return updateFile(path, false);
    }

    @Override
    public InputStream open(String fileName) throws FileNotFoundException {
        return new FileInputStream(new File(new File(this.workspace, fileName), CONTEXT));
    }

    @Override
    public boolean delete(String path, boolean recursive) throws IOException {
        File file = new File(this.workspace, path);
        if (recursive) {
            return deleteRecursive(file);
        }
        
        File corpse = new File(file, CONTEXT);
        if (corpse.exists() && !corpse.delete()) {
            return false;
        }
        return file.delete();
    }

    static public boolean deleteRecursive(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteRecursive(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return path.delete();
    }

    private FileOutputStream updateFile(String path, boolean overwrite) throws FileNotFoundException {
        File file = new File(this.workspace, path);
        file.mkdirs();
        return new FileOutputStream(new File(file, CONTEXT), overwrite);
    }

    @Required
    public void setWorkspace(String workspace) throws IOException {
        new File(workspace).mkdirs();
        if (!new File(workspace).exists()) throw new SecurityException("Can't create workspace directory");
        this.workspace = workspace;
    }
}
