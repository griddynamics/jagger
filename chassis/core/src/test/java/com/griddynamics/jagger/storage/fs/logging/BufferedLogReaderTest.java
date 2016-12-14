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

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.google.common.base.Throwables;
import com.griddynamics.jagger.storage.FileStorage;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BufferedLogReaderTest {

    @Test
    public void shouldReadOneObject() throws Exception {
        byte[] bytes = serialize(1);
        BufferedLogReader.LogReaderInput input = inputOf(bytes);
        BufferedLogReader.IteratorImpl<Integer> iterator = new BufferedLogReader.IteratorImpl<Integer>(input, Integer.class);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), equalTo(1));
        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void shouldReadEmptyStream() throws Exception {
        byte[] bytes = new byte[]{};
        BufferedLogReader.LogReaderInput input = inputOf(bytes);
        BufferedLogReader.IteratorImpl<Integer> iterator = new BufferedLogReader.IteratorImpl<Integer>(input, Integer.class);

        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void shouldReadTwoObjects() throws Exception {
        byte[] bytes = serialize(1, 2);
        BufferedLogReader.LogReaderInput input = inputOf(bytes);
        BufferedLogReader.IteratorImpl<Integer> iterator = new BufferedLogReader.IteratorImpl<Integer>(input, Integer.class);

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), equalTo(1));

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), equalTo(2));

        assertThat(iterator.hasNext(), is(false));
    }

    @Test
    public void shouldFailWhenFileDoesNotExist() throws Exception {
        FileStorage fileStorage = mock(FileStorage.class);
        when(fileStorage.exists("1/2/3")).thenReturn(false);

        BufferedLogReader logReader = new HessianBufferedLogReader();
        logReader.setFileStorage(fileStorage);

        IllegalArgumentException expected = null;
        try {
            logReader.read("1", "2", "3", Integer.class);
        } catch (IllegalArgumentException e) {
            expected = e;
        }
        assertThat(expected, notNullValue());
        verify(fileStorage).exists("1/2/3");
    }

    private static BufferedLogReader.LogReaderInput inputOf(byte[] bytes) {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        return new HessianBufferedLogReader().getInput(input);
    }

    private static <T> byte[] serialize(T... objects) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
        Hessian2Output hessian2Output = new Hessian2Output(out);
        for (T object : objects) {
            try {
                hessian2Output.writeObject(object);
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }

        try {
            hessian2Output.close();
            out.close();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        return out.toByteArray();
    }
}
