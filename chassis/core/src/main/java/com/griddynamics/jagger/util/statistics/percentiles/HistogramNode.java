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

import java.util.ArrayList;

// This class is a derivation of hist4j https://github.com/flaptor/hist4j

/**
 * A HistogramNode is the building block for the histogram data structure and
 * provides a common interface for both the data and the fork nodes.
 * @author Jorge Handl
 */
public interface HistogramNode {

    /**
     * Method for clearing the node
     */
    void reset ();

    /**
     * Method for adding a value to the histogram
     */
    HistogramNode addValue(AdaptiveHistogram root, double value);

    /**
     * Method for getting the number of values stored in the same bucket as a reference value
     */
    long getCount(double value);

    /**
     * Method for getting the cumulative density function for a given value
     */
    long getAccumCount(double value);

    /**
     * Method for getting the value for which the cumulative density function reaches the desired value
     */
    Double getValueForAccumCount(long[] accumCount);

    /**
     * Method for applying a convertion function to the values stored in the histogram
     */
    void apply(AdaptiveHistogram.ValueConversion valueConversion);

    /**
     * Method for getting a table representing the histogram data
     */
    void toTable(ArrayList<Cell> table);
}
