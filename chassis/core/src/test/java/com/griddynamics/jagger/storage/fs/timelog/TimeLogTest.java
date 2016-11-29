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

package com.griddynamics.jagger.storage.fs.timelog;

import org.testng.annotations.*;
import static org.testng.AssertJUnit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.*;

public class TimeLogTest {

    @Test(enabled = false)
    public void testSingleStreamAggregation() {

        NumericalTimeLogReader reader = new NumericalTimeLogReader(Collections.singletonList(getDataInputStream1()));

        Iterator<Window<NumericalTimeLogReader.StatisticalAggregator>> cursor = reader.aggregateStatistics(100, 3);

        Window<NumericalTimeLogReader.StatisticalAggregator> window = cursor.next();
        assertTrue(testWindow(window, new double[] {2, 20, 200}, new double[] {2, 20, 200}, new double[] {2, 20, 200}, new long[] {4, 4, 4}));

        window = cursor.next();
        assertTrue(testWindow(window, new double[] {3, 30, 300}, new double[] {2, 20, 200}, new double[] {4, 40, 400}, new long[] {2, 2, 2}));

        window = cursor.next();
        assertTrue(testWindow(window, new double[] {2, 20, 200}, new double[] {2, 20, 200}, new double[] {2, 20, 200}, new long[] {1, 1, 1}));

        window = cursor.next();
        assertTrue(testWindow(window, new double[] {1, 10, 100}, new double[] {1, 10, 100}, new double[] {1, 10, 100}, new long[] {1, 1, 1}));

        window = cursor.next();
        assertTrue(window.isEmpty());

        window = cursor.next();
        assertTrue(window.isEmpty());

        window = cursor.next();
        assertTrue(window.isEmpty());

        window = cursor.next();
        assertTrue(testWindow(window, new double[] {2, 20, 200}, new double[] {2, 20, 200}, new double[] {2, 20, 200}, new long[] {1, 1, 1}));
    }

    @Test(enabled = false)
    public void testMultipleStreamAggregation() {

        NumericalTimeLogReader reader = new NumericalTimeLogReader(Arrays.asList(getDataInputStream1(), getDataInputStream2()));

        Iterator<Window<NumericalTimeLogReader.StatisticalAggregator>> cursor = reader.aggregateStatistics(100, 3);

        Window<NumericalTimeLogReader.StatisticalAggregator> window = cursor.next();
        assertTrue(testWindow(window, new double[] {3, 30, 300}, new double[] {2, 20, 200}, new double[] {4, 40, 400}, new long[] {8, 8, 8}));

        window = cursor.next();
        assertTrue(testWindow(window, new double[] {3, 30, 300}, new double[] {2, 20, 200}, new double[] {4, 40, 400}, new long[] {2, 2, 2}));

        window = cursor.next();
        assertTrue(testWindow(window, new double[] {3, 30, 300}, new double[] {2, 20, 200}, new double[] {4, 40, 400}, new long[] {2, 2, 2}));
    }

    private static boolean testWindow(Window<NumericalTimeLogReader.StatisticalAggregator> window, double[] averages, double[] minimums, double[] maximums, long[] samples) {
        int i = 0;
        for(NumericalTimeLogReader.StatisticalAggregator aggregator : window.getAggregators()) {
            if(  Double.compare(aggregator.getMean(), averages[i]) != 0 ||
                 Double.compare(aggregator.getMax(), maximums[i]) != 0 ||
                 Double.compare(aggregator.getMin(), minimums[i]) != 0 ||
                 aggregator.getNumberOfSamples() != samples[i]) {
                return false;
            }
            i++;
        }
        return true;
    }

    private static DataInputStream getDataInputStream1() {
        ByteArrayOutputStream boStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boStream);
        NumericalTimeLogWriter writer = new NumericalTimeLogWriter(dos);
        writer.startFlushing();

        writer.write(100, new double[]{2, 20, 200});
        writer.write(100, new double[]{2, 20, 200});
        writer.write(120, new double[]{2, 20, 200});
        writer.write(199, new double[]{2, 20, 200});

        writer.write(201, new double[]{2, 20, 200});
        writer.write(250, new double[]{4, 40, 400});

        writer.write(300, new double[]{2, 20, 200});

        writer.write(400, new double[]{1, 10, 100});

        writer.write(830, new double[]{2, 20, 200});

        writer.close();

        ByteArrayInputStream biStream = new ByteArrayInputStream(boStream.toByteArray());

        return new DataInputStream(biStream);
    }

    private static DataInputStream getDataInputStream2() {
        ByteArrayOutputStream boStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boStream);
        NumericalTimeLogWriter writer = new NumericalTimeLogWriter(dos);
        writer.startFlushing();

        writer.write(100, new double[]{4, 40, 400});
        writer.write(150, new double[]{4, 40, 400});
        writer.write(151, new double[]{4, 40, 400});
        writer.write(167, new double[]{4, 40, 400});

        writer.write(303, new double[]{4, 40, 400});

        writer.close();

        ByteArrayInputStream biStream = new ByteArrayInputStream(boStream.toByteArray());

        return new DataInputStream(biStream);
    }
}
