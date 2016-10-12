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

import org.testng.Assert;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.testng.annotations.Test;

/**
 * @author Nikolay Musienko
 *         Date: 22.04.13
 */

public class FileProviderTest {

    @Test
    public static void test() throws Exception {
        final String file;
        if (System.lineSeparator().equals("\r\n")) {
            file = FileProviderTest.class.getResource("/file-reader-windows.txt").getFile();
        } else {
            file = FileProviderTest.class.getResource("/file-reader-unix.txt").getFile();
        }

        FileProvider<String> firstProvider = new FileProvider<String>(file);
        testIterable(firstProvider);
        testIterable(firstProvider);
        FileProvider<String> secondProvider = new FileProvider<String>(file);
        testIterable(secondProvider);
        testIterable(firstProvider);
    }

    private static void testIterable(Iterable<String> i) {
        Iterator<String> it = i.iterator();
        Assert.assertEquals(it.hasNext(), true);
        Assert.assertEquals(it.hasNext(), true);
        Assert.assertEquals(it.next(), "test1");
        Assert.assertEquals(it.hasNext(), true);
        Assert.assertEquals(it.next(), "test2");
        Assert.assertEquals(it.hasNext(), true);
        Assert.assertEquals(it.next(), "test3");
        Assert.assertEquals(it.hasNext(), false);
        try {
            it.next();
            Assert.fail("Iterator must throws NoSuchElementException at empty data set!");
        } catch (NoSuchElementException e){
            //OK
        }
        Assert.assertEquals(it.hasNext(), false);
    }

}
