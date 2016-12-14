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
 * Aggregation is provided on some time interval. How this time interval is selected you can read here: @ref Section_aggregators_time_intervals @n
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
 * @ingroup Main_Aggregators_Base_group */
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

/* **************** Aggregators page ************************* */
/// @defgroup Main_Aggregators_General_group Aggregators main page
///
/// @li General information about interfaces: @ref Main_Aggregators_Base_group
/// @li Available implementations: @ref Main_Aggregators_group
/// @li How to customize: @ref Main_HowToCustomizeAggregators_group
/// @li @ref Section_aggregators_time_intervals
/// @n
/// @n
/// @details
/// @par General info
/// Aggregators are processing raw data to get final measurement results that will be saved to database. @n
/// Aggregators are executed after all measurements are finished. Main goal for them is to reduce number of data values available after measurement @n
/// Simplest example: after measurement there are 1000 points, but you want to save only 200 points to database @n
/// So you are applying averaging aggregator that takes average value for every 5 points from raw data and saves it to DB as single value. @n
///
/// @par Example of aggregators setup in XML:
///
/// Simple example: @n
/// @dontinclude  tasks-new.conf.xml
/// @skip  begin: following section is used for docu generation - standard aggregator usage
/// @until end: following section is used for docu generation - standard aggregator usage
///
/// Example with additional setting for aggregation: @n
/// @dontinclude  tasks-new.conf.xml
/// @skip  begin: following section is used for docu generation - metrics with aggregators
/// @until end: following section is used for docu generation - metrics with aggregators
///
/// @par Aggregators XML elements
/// @xlink_complex{metricAggregatorAbstract} - what aggregators can be used in XML elements. See <b> 'Sub Types' </b> section of man page @n
/// How aggregators mentioned above are implemented you can see in section: @ref Main_Aggregators_group @n
/// @n
///
/// @section Section_aggregators_time_intervals Aggregation interval
/// Aggregation interval defines how many values will be saved to database and displayed in plots. @n
/// In property file you can decide what parameter you will set.@n
/// You can set either number of points on the plot or directly aggregation interval. @n
/// @dontinclude  environment.properties
/// @skip  begin: following section is used for docu generation - Aggregation interval
/// @until end: following section is used for docu generation - Aggregation interval
/// @n
/// In the picture below you can see comparison of both settings:@n
/// for session 26 - point count was set @n
/// for session 29 - interval @n
/// @image html jagger_point_count_vs_time_interval.png "Aggregation interval setup"


/* **************** How to customize aggregators ************************* */
/// @defgroup Main_HowToCustomizeAggregators_group Custom aggregators
///
/// @details
/// @ref Main_Aggregators_General_group
/// @n
/// @n
/// 1. Create class which implements @ref MetricAggregatorProvider @n
/// This class should provide instance of your aggregator - class that implements interface @ref MetricAggregator<C extends Number> @n
/// @dontinclude  MaxMetricAggregatorProvider.java
/// @skip  begin: following section is used for docu generation - custom aggregator source
/// @until end: following section is used for docu generation - custom aggregator source
/// @n
///
/// 2. Create bean of this class in some configuration file. Put some id for it.
/// @dontinclude  calculatorsAndAggregators.conf.xml
/// @skip  begin: following section is used for docu generation - custom aggregator
/// @until end: following section is used for docu generation - custom aggregator
/// @n
///
/// 3. Add metric aggregator of type @xlink_complex{metric-aggregator-ref} to you @xlink{metric-custom} block.@n
/// @dontinclude  tasks-new.conf.xml
/// @skip  begin: following section is used for docu generation - custom aggregator usage
/// @until end: following section is used for docu generation - custom aggregator usage
