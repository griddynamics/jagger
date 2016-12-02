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

import com.griddynamics.jagger.exception.TechnicalException;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

/**
 * This class in intended for processing of logs which are written in form (timestamp | value1 | value2 | ...)
 * where values are floating point numbers.
 * <p>
 * Let we have several nodes and each node writes a log in form (timestamp | cpuUtilization | memoryUtilization ).
 * We can feed all these files to NumericalTimeLogReader and request to calculate average for each time range, say 500ms.
 * NumericalTimeLogReader will provide an iterator to structure that logically can be described as follows:
 * <pre>
 * 0-500ms | averageCpuUtilization | averageMemoryUtilization
 * 501-1000ms | averageCpuUtilization | averageMemoryUtilization
 * 1001-1500ms | averageCpuUtilization | averageMemoryUtilization
 * ...
 * </pre>
 * <p>
 * Each log must be sorted by timestamp, otherwise result is undefined.
 * <p>
 * One can provide his own {@link Aggregator} and {@link EntryPredicate} to perform custom aggregation and
 * filter out undesired log entries
 */
public class NumericalTimeLogReader {

    private List<DataInputStream> inputStreams;

    public NumericalTimeLogReader(List<DataInputStream> inputStreams) {
        this.inputStreams = inputStreams;
    }

    public <T extends Aggregator> Iterator<Window<T>> aggregate(long timeWindow, List<T> aggregators, EntryPredicate predicate) {
        return new WindowIterator<T>(inputStreams, timeWindow, aggregators, predicate);
    }

    public Iterator<Window<StatisticalAggregator>> aggregateStatistics(long timeWindow, int numberOfValues) {
        return new WindowIterator<StatisticalAggregator>(inputStreams, timeWindow, getAggregators(numberOfValues), null);
    }

    private List<StatisticalAggregator> getAggregators(int number) {
        List<StatisticalAggregator> aggregators = new ArrayList<StatisticalAggregator>();
        for(int i = 0; i < number; i++) {
            aggregators.add( new StatisticalAggregator() );
        }
        return aggregators;
    }

    private static class WindowIterator<T extends Aggregator> implements Iterator<Window<T>> {
        private String delimiter = "\\|";
        private List<DataInputStream> inputStreams;
        private List<T> aggregators;
        private int numberOfValues;
        private EntryPredicate predicate;

        private String[][] entryBuffer;

        private long startTime = 0;
        private long timeWindow;
        private long windowCounter;

        public WindowIterator(List<DataInputStream> inputStreams, long timeWindow, List<T> aggregators, EntryPredicate predicate) {
            this.inputStreams = inputStreams;
            this.aggregators = aggregators;
            this.predicate = predicate;
            this.timeWindow = timeWindow;
            numberOfValues = aggregators.size();

            entryBuffer = new String[inputStreams.size()][];
        }

        public boolean hasNext() {
            try {
                for(int i = 0; i < inputStreams.size(); i++) {
                    if(inputStreams.get(i).available() > 0 || entryBuffer[i] != null) {
                        return true;
                    }
                }
            } catch (IOException e) {
                throw new TechnicalException(e);
            }
            return false;
        }

        public Window<T> next() {
            long windowStartTime = startTime + windowCounter*timeWindow;
            long windowEndTime = windowStartTime + timeWindow;

            for(Aggregator aggregator : aggregators) {
                aggregator.reset();
            }

            try {
                boolean isThereAvailableStream = true;
                while(isThereAvailableStream) {

                    isThereAvailableStream = false;

                    int streamId = 0;
                    for(DataInputStream stream : inputStreams) {

                        boolean lastEntryFlushed = entryBuffer[streamId] != null && flushEntry(entryBuffer[streamId], windowStartTime, windowEndTime);
                        if(lastEntryFlushed) {
                            entryBuffer[streamId] = null;
                        }

                        if(entryBuffer[streamId] == null && stream.available() > 0) {

                            String entry = stream.readUTF().trim();
                            String[] parsedEntry = entry.split(delimiter);

                            if(parsedEntry.length - 1 != numberOfValues) {
                                throw new TechnicalException("Invalid entry [" + entry + "]. " + numberOfValues + " fields expected.");
                            }

                            if(startTime == 0) {
                                startTime = Long.valueOf(parsedEntry[0]);
                                windowStartTime = startTime;
                                windowEndTime = windowStartTime + timeWindow;
                            }

                            if( flushEntry(parsedEntry, windowStartTime, windowEndTime) ) {
                                isThereAvailableStream = true;
                            } else {
                                entryBuffer[streamId] = parsedEntry;
                            }
                        }

                        streamId++;
                    }
                }
            } catch (IOException e) {
                throw new TechnicalException(e);
            }

            Window<T> window = new Window<T>();
            window.setAggregators(aggregators);
            window.setStartTime(new Date(windowStartTime));
            window.setEndTime(new Date(windowEndTime));

            windowCounter++;

            return window;
        }

        public void remove() {
            throw new TechnicalException("Operation Not Supported");
        }

        private boolean flushEntry(String[] entry, long windowStartTime, long windowEndTime) {
            long timeStamp = Long.valueOf(entry[0]);
            if(timeStamp >= windowStartTime && timeStamp < windowEndTime) {
                if(predicate == null || predicate.evaluate(entry)) {
                    for(int i = 0; i < aggregators.size(); i++) {
                        aggregators.get(i).add(Double.valueOf(entry[i + 1]));
                    }
                }
                return true;
            }

            return false;
        }
    }

    public static class StatisticalAggregator implements Aggregator {
        private SummaryStatistics statistics;

        public void add(double value) {
            statistics.addValue(value);
        }

        public long getNumberOfSamples() {
            return statistics.getN();
        }

        public void reset() {
            statistics = new SummaryStatistics();
        }

        public double getMin() {
            return statistics.getMin();
        }

        public double getMax() {
            return statistics.getMax();
        }

        public double getMean() {
            return statistics.getMean();
        }

        public double getStandardDeviation() {
            return statistics.getStandardDeviation();
        }
    }
}

