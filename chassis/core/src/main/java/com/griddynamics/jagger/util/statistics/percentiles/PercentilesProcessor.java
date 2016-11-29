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

package com.griddynamics.jagger.util.statistics.percentiles;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

public class PercentilesProcessor {
    public enum EstimationStrategy { EXACT, HEURISTIC, AUTO }

    private final EstimationStrategy initialStrategy;
    private EstimationStrategy currentStrategy;

    private DescriptiveStatistics descriptiveStatistics;
    private AdaptiveHistogram adaptiveHistogram;

    private int exactProcessorMaxCapacity = 100000;

    public PercentilesProcessor() {
        this(EstimationStrategy.AUTO);
    }

    /**
     * Creates processor with the specifiedVal estimation strategy. See {@link EstimationStrategy}
     * If it is expected that samples count will be large (100M or more)
     * then {@link EstimationStrategy#HEURISTIC} should be used. In other cases {@link EstimationStrategy#AUTO}
     * should be used. {@link EstimationStrategy#EXACT} typically should not be used.
     */
    public PercentilesProcessor(EstimationStrategy estimationStrategy) {
        initialStrategy = estimationStrategy;
        reset();
    }

    public void addValue(double value) {
        switch (currentStrategy) {
            case HEURISTIC: adaptiveHistogram.addValue(value); break;
            case EXACT: descriptiveStatistics.addValue(value); break;
            case AUTO:
                if(descriptiveStatistics.getN() >= exactProcessorMaxCapacity) {
                    adaptiveHistogram = new AdaptiveHistogram();
                    double[] values = descriptiveStatistics.getValues();
                    for(int i = 0; i < values.length; i++) {
                        adaptiveHistogram.addValue(values[i]);
                    }
                    adaptiveHistogram.addValue(value);
                    currentStrategy = EstimationStrategy.HEURISTIC;
                } else {
                    descriptiveStatistics.addValue(value);
                }
        }
    }

    public double getPercentile(double p) {
        double result = 0;
        switch (currentStrategy) {
            case HEURISTIC: result = adaptiveHistogram.getValueForPercentile(p); break;
            case AUTO:
            case EXACT: result = descriptiveStatistics.getPercentile(p);
        }

        return result;
    }

    public void reset() {
        currentStrategy = initialStrategy;

        switch(currentStrategy) {
            case HEURISTIC: adaptiveHistogram = new AdaptiveHistogram(); break;
            case AUTO:
            case EXACT: descriptiveStatistics = new DescriptiveStatistics();
        }
    }

    public void setExactProcessorMaxCapacity(int capacity) {
        this.exactProcessorMaxCapacity = capacity;
    }
}
