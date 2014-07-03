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

package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObject;

// @todo add an ability to use validators with properties
/** Validates the result of invocation
 * @author Grid Dynamics
 * @n
 * @par Details:
 * @details Simplified type of collector. @n
 * Validates the result of invocation of specified query and endpoint. Save validation result to database. @n
 * Validators execute one by one. If one fails, no other will be executed. @n
 * @n
 * To view default implementations of collectors click here @ref Main_Collectors_group
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Collectors_Base_group */
public abstract class ResponseValidator<Q, E, R> extends KernelSideObject {

    /** Default constructor for validators
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details This constructor will be called by validator provider, which creates a lot of validators instances
     *
     * @param taskId        - id of current task
     * @param sessionId     - id of current session
     * @param kernelContext - context for current Node */
    public ResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    /** Returns the name of validator
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details Returns the name of validator. This name will be displayed at webUI and jagger report.
     *
     * @return the name of validator */
    public abstract String getName();

    /** Validates the result of invocation
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details  Validates the result of invocation with specified query and endpoint. If return false current invoke will be marked as failed.
     *
     * @param query     - the query of current invocation
     * @param endpoint  - the endpoint of current invocation
     * @param result    - the result of invocation
     * @param duration  - the duration of invocation
     *
     * @return true if validation is successful */
    public abstract boolean validate(Q query, E endpoint, R result, long duration);

}

/* **************** Collectors page ************************* */
/// @defgroup Main_Collectors_General_group Collectors main page
///
/// @li General information about interfaces: @ref Main_Collectors_Base_group
/// @li Available implementations: @ref Main_Collectors_group
/// @li How to customize: @ref Main_HowToCustomizeCollectors_group
/// @li @ref Section_collectors_execution_flow
/// @n
/// @n
/// @details
/// @par General info
/// Collectors calculate information based on invocation response and validate result of invocation. @n
/// Collectors are executed after every invoke during test run. Test run sequence you can find here: @ref Main_Test_Flow_group @n
///
/// @par Example of collectors setup in XML:
/// Following XML code should be included in @xlink{test-description} section @n
/// Order of collectors execution is the same as order of their declaration in XML @n
/// If @xlink{test-description} has @xlink{test-description,parent}, collectors from @xlink{test-description,parent} will be executed first @n
///
/// Simple example: @n
/// @dontinclude  defaults.conf.xml
/// @skip  begin: following section is used for docu generation - metric calculator usage
/// @until end: following section is used for docu generation - metric calculator usage
///
/// Example with additional setting for aggregation: @n
/// @dontinclude  tasks-new.conf.xml
/// @skip  begin: following section is used for docu generation - metrics with aggregators
/// @until end: following section is used for docu generation - metrics with aggregators
///
/// @par Info collector XML elements
/// @xlink_complex{metricAbstract} - what metrics can be used in information collector XML element. See <b> 'Sub Types' </b> section of man page @n
/// @xlink_complex{validatorAbstract} - what validators can be used in information collector XML element. See <b> 'Sub Types' </b> section of man page @n
/// How metrics and validators mentioned above are implemented you can see in section: @ref Main_Collectors_group @n
/// What is the difference between metrics, validators, collectors you can see in section: @ref Main_HowToCustomizeCollectors_group
/// @n
/// @section Section_collectors_execution_flow Collectors execution sequence
/// Click on diagram components to learn more about every component
/// @dotfile jagger_collectors.dot "Simplified Collectors execution sequence"
///

/* **************** How to customize collector ************************* */
/// @defgroup Main_HowToCustomizeCollectors_group Custom collectors
///
/// @details
/// @ref Main_Collectors_General_group
/// @n
/// @n
/// There are three ways to collect some information from responses. One can create custom metric calculator, @n
/// validator or collector. Metric calculator and validator are simplified versions of collectors.@n
/// See comparison below. Order of execution is under link on the top of the page @n
/// @code
///   Param                                           Metric calculator               Validator                  Collector
///
///
/// - What information is coming from invoker         Response from SUT               Endpoint                   Endpoint
///                                                                                   Query                      Query
///                                                                                   Response from SUT          Response from SUT
///                                                                                                              Invoke duration
///                                                                                                              Was invoke fail or pass
///
///
/// - When is executed                                Invoke was successful           Invoke was successful      Always
///                                                   All validators - successful     Previous validators pass
///
///
/// - What custom code is required                    Metric class                    Validator class            Collector class
///                                                   Aggregator class [optional]                                Collector provider class
///                                                                                                              Aggregator class [optional]
///
///
/// - Can save custom metric to DB                    Yes                             No                         Yes
/// - Can set invoke result to fails                  No                              Yes                        No
/// @endcode
/// @n
/// @example_begin
/// @example_addmenu{0,Custom validator}
/// @example_addmenu{1,Custom metric calculator}
/// @example_addmenu{2,Custom collector}
/// @example_begin_content{0}
/// <ol>
/// <li> Create class which implements @ref ResponseValidator<Q,E,R> @n
/// Will validate responce from SUT after every successful invocation
/// @dontinclude  ResponseFromFileValidator.java
/// @skip  begin: following section is used for docu generation - validator-custom source
/// @until end: following section is used for docu generation - validator-custom source
/// @n
///
/// <li> If your validator doesn't have any properties, create @xlink{validator-custom} collector in @xlink{test-description,info-collectors} block in @xlink{test-description}. @n
/// Set the name of validator class to attribute @xlink{validator-custom,validator}.
/// @dontinclude  test.description.conf.xml
/// @skip  begin: following section is used for docu generation - validator-custom
/// @until end: following section is used for docu generation - validator-custom
/// </ol>
/// @n
/// @example_end_content
/// @example_begin_content{1}
/// <ol>
/// <li> Create class which implements @ref MetricCalculator<R>@n
/// Will calculate some parameters according to SUT response
/// @dontinclude  ResponseSize.java
/// @skip  begin: following section is used for docu generation - metric calculator source
/// @until end: following section is used for docu generation - metric calculator source
/// @n
///
/// <li> Create bean of this class in some configuration file. Put some id for it.
/// @dontinclude  collectors.conf.xml
/// @skip  begin: following section is used for docu generation - metric calculator
/// @until end: following section is used for docu generation - metric calculator
/// @n
///
/// <li> Add @xlink{metric-custom} collector to @xlink{test-description,info-collectors} block.@n
/// Set id of custom metric class bean to @xlink{metric-custom,calculator} attribute @n
/// Attribute @xlink{metric-custom,id} will be used as metric name in reports @n
/// Set attribute @xlink{metric-custom,plotData} to true if you want to plot 'metric vs time' plot in report
/// @dontinclude  defaults.conf.xml
/// @skip  begin: following section is used for docu generation - metric calculator usage
/// @until end: following section is used for docu generation - metric calculator usage
/// @n
///
/// <li> Optional. Add custom aggregator to metric. @n
/// How to create aggregator provider - see 'Custom collector' example
/// @code
/// <beans:bean id="minAggregator" class="test.MinAggregatorProvider"/>
///         ...
/// <metric id="customMetric1" xsi:type="metric-custom" calculator="customMetric" plotData="true">
/// <metric-aggregator xsi:type="metric-aggregator-ref" ref="minAggregator"/>
/// </metric>
/// @endcode
/// </ol>
/// @example_end_content
/// @example_begin_content{2}
/// <ol>
/// <li> Create collector class which implements @ref MetricCollector<Q,R,E>@n
/// Will proceed data after every invoke and save to Kernel storage
/// @dontinclude  ExampleSuccessRateCollector.java
/// @skipline  public class ExampleSuccessRateCollector
/// @n
///
/// <li> Create collector provider class which implements @ref MetricCollectorProvider<Q,R,E>@n
/// Will provide an instance of custom collector to Jagger
/// @dontinclude  ProviderOfExampleSuccessRateCollector.java
/// @skipline  public class ProviderOfExampleSuccessRateCollector
/// @n
///
/// <li> Create aggregator class which implements @ref MetricAggregatorProvider@n
/// Will proceed data after all tests are over and prepare data for DB @n
/// Collector provider class is associating aggregator with collector.
/// @dontinclude  ProviderOfExampleSuccessRateAggregator.java
/// @skipline  public class ProviderOfExampleSuccessRateAggregator
/// @n
///
/// <li> Create bean of provider class in some configuration file. Put some id for it.
/// @dontinclude  collectors.conf.xml
/// @skip  begin: following section is used for docu generation - custom collector
/// @until end: following section is used for docu generation - custom collector
/// @n
///
/// <li> Add @xlink{metric-ref} collector to @xlink{test-description,info-collectors} block.@n
/// Set id of bean to @xlink{metric-ref,ref} attribute.
/// @dontinclude  defaults.conf.xml
/// @skip  begin: following section is used for docu generation - metric calculator usage
/// @until end: following section is used for docu generation - metric calculator usage
/// </ol>
/// @example_end_content
/// @example_end
/// @b Note:
/// @li full examples of the code are available in maven archetype-examples
/// @li instead of ${package} write the name of your package
