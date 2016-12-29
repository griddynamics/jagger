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

package com.griddynamics.jagger.engine.e1.collector;

/** Aggregates raw data, available after test, to final value that will be saved to database
 * @author Nikolay Musienko
 * @n
 * @par Details:
 * @details Simpliest example - averaging of results @n
 * Aggregation is provided on some time interval (set in the framework configuration) @n
 * Aggregation sequence looks as follows: @n
 * @code
 * loop time intervals {
 *     loop points of raw data in this interval {
 *         Aggregator.append(raw data point);
 *     }
 *     result for this interval = Aggregator.getAggregated();
 *     Aggregator.reset();
 * }
 * @endcode
 * @n
 * To view aggregators implementations click here @ref Main_Aggregators_group
 * @n
 * @ingroup Main_Aggregators_group */
public interface MetricAggregator<C extends Number> {

    /** Method is executed for every value in raw data
     * @param calculated - single raw data value */
    void append(C calculated);

    /** Method is executed to get final result for some time interval */
    C getAggregated();

    /** Method is called to reset aggregator, before going to next time interval */
    void reset();

    /*
     * !NOTE that getName() method returns display name of aggregator(not id).
     * id of aggregator creates on display name base with discarding all reserved symbols.
     * Reserved symbols = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" | "$" | ","
     */
    /** Get display name of aggregator
     * @return display name of aggregator */
    String getName();  //display name
}
