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

package com.griddynamics.jagger.providers.csv;

import com.griddynamics.jagger.providers.CsvProvider;
import org.apache.commons.csv.CSVStrategy;
import org.testng.Assert;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.testng.annotations.Test;

/**
 * @author Nikolay Musienko
 *         Date: 22.04.13
 */

public class CSVProviderTest {

    @Test
    public static void test() throws Exception {
        CsvProvider<RequestPath> iterable = new CsvProvider<RequestPath>();
        iterable.setObjectCreator(new RequestPathCvsWrapper());
        iterable.setPath("src/test/resources/requests.csv");
        iterable.setStrategy(CSVStrategy.EXCEL_STRATEGY);
        iterable.setReadHeader(true);
        RequestPath[] requestPaths = new RequestPath[]{
                new RequestPath("http://localhost", "sleep/10"),
                new RequestPath("http://localhost", "sleep/10"),
                new RequestPath("http://localhost:8080", "sleep/10")
        };
        testIterable(iterable, requestPaths);
        testIterable(iterable, requestPaths);
        iterable = new CsvProvider<RequestPath>();
        iterable.setObjectCreator(new RequestPathCvsWrapper());
        iterable.setPath("src/test/resources/requests.csv");
        iterable.setReadHeader(true);
        testIterable(iterable, requestPaths);
    }

    private static void testIterable(Iterable<RequestPath> i, RequestPath[] requestPaths) {
        Iterator<RequestPath> it = i.iterator();
        Assert.assertEquals(it.hasNext(), true);
        Assert.assertEquals(it.hasNext(), true);
        Assert.assertEquals(it.next(), requestPaths[0]);
        Assert.assertEquals(it.hasNext(), true);
        Assert.assertEquals(it.next(), requestPaths[1]);
        Assert.assertEquals(it.hasNext(), true);
        Assert.assertEquals(it.next(), requestPaths[2]);
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
