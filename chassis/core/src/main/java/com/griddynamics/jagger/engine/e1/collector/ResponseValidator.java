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
 * @details Validates the result of invocation of specified query and endpoint. Save validation result to database.
 * Validators execute one by one. If one fails, no another will be executed. @n
 * @n
 * To view all collectors implementations click here @ref Main_Collectors_group
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
     * @details  Validates the result of invocation with specified query and endpoint. If return false current invoke will be marks as failed.
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
/// @defgroup Main_Collectors_General_group General information about collectors
///
/// @details Collectors calculate information based on invocation response and validate result of invocation. @n
/// If validation fails, invocation will be mark as failed. So validator can affect success rate.
/// @n
/// @n
/// The results of collectors will be available in report and webUI.
/// @li General information: @ref Main_Collectors_Base_group
/// @li Available implementations: @ref Main_Collectors_group
/// @li How to customize: @ref Main_HowToCustomizeCollectors_group

/* **************** How to customize collector ************************* */
/// @defgroup Main_HowToCustomizeCollectors_group Custom collectors
///
/// @details
/// @ref Main_Collectors_General_group
/// @n
/// @n
/// There are two ways to collect some information from responses.
/// @li Create custom @xlink{metric} calculator, which will calculate something and then aggregate this.
/// @li Create sutom @xlink{validator}, which will validate results of invocations.
/// @n
///
/// How to create custom validator -
/// 1. Create class which implements @ref ResponseValidator<Q,E,R>
/// @dontinclude  ResponseFromFileValidator.java
/// @skipline  public class ResponseFromFileValidator
/// @n
///
/// 2. If your validator doesn't have any properties, create @xlink{validator-custom} collector in @xlink{test-description,info-collectors} block.
/// Set the name of validator class to attribute @xlink{validator-custom,validator}.
/// @dontinclude  test.suite.scenario.config.xml
/// @skip  begin: following section is used for docu generation - validator-custom
/// @until end: following section is used for docu generation - validator-custom
/// @n
///
/// How to create custom metric calculator -
/// 1. Create class which implements @ref MetricCalculator<R>
/// @dontinclude  ResponseSize.java
/// @skipline  public class ResponseSize
/// @n
///
/// 2. Create bean of this class in some configuration file. Put some id for it.
/// @dontinclude  collectors.conf.xml
/// @skip  begin: following section is used for docu generation - metric calculator
/// @until end: following section is used for docu generation - metric calculator
/// @n
///
/// 3. Add @xlink{metric-custom} collector to @xlink{test-description,info-collectors} block. Set id of bean to @xlink{metric-custom,calculator} attribute.
/// @dontinclude  defaults.config.xml
/// @skip  begin: following section is used for docu generation - metric calculator usage
/// @until end: following section is used for docu generation - metric calculator usage
/// @n
///
/// @b Note:
/// @li full examples of the code are available in maven archetype-examples
/// @li instead of ${package} write the name of your package
