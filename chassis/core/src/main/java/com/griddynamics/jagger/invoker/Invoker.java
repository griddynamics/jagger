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

package com.griddynamics.jagger.invoker;

import java.io.Serializable;

/** Responsible for action invocation on specified endpoint and query
 * @author Mairbek Khadikov
 * @n
 * @par Details:
 * @details Create a request to some target with specified query. The result of invocation can be collected by metrics and validators. Note that Invoker is used in multi thread environment, so realize thread-safe implementation @n
 * @n
 * To view all invokers implementations click here @ref Main_Invokers_group
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Invokers_Base_group */
public interface Invoker<Q,R,E> extends Serializable {


	/** Makes an invocation to target
     * @author Mairbek Khadikov
     * @n
     * @par Details:
     * @details If method throw some exception current invocation will be marked as failed
     * @n
     * @param query    - input data for the invocation
	 * @param endpoint - endpoint
     *
     * @return invocation result
     * @throws InvocationException when invocation failed */
      R invoke(Q query, E endpoint) throws InvocationException;

}

/* Below is doxygen documentation for Jagger customization */

/// @defgroup Main_Custom_Components_group Custom component
///
/// @li @ref Main_HowToCustomizeAggregators_group
/// @li @ref Main_HowToCustomizeInvokers_group
/// @li @ref Main_HowToCustomizeProviders_group
/// @li @ref Main_HowToCustomizeDistributors_group
/// @li @ref Main_HowToCustomizeCollectors_group
/// @li @ref Main_HowToCustomizeListeners_group

/* **************** Invokers page *************************  */
/// @defgroup Main_Invokers_General_group Invokers main page
///
/// @details Invokers take query and try to create invocation to endpoint. @n
/// Every invoker returns some result. Usually, query is used as http request and endpoint is used as url of target service @n
/// Invokers are used in @xlink{scenario-query-pool} element.
/// @n
/// @li General information: @ref Main_Invokers_Base_group
/// @li Available implementations: @ref Main_Invokers_group
/// @li How to customize: @ref Main_HowToCustomizeInvokers_group

/* **************** How to customize invoker ************************* */
/// @defgroup Main_HowToCustomizeInvokers_group Custom invokers
///
/// @details
/// @ref Main_Invokers_General_group
/// @n
/// @n
/// To add custom invoker you need to do:
///
/// 1. Create class which implements interface @ref Invoker<Q,R,E>
/// @dontinclude  PageVisitorInvoker.java
/// @skipline  public class PageVisitorInvoker
/// @n
///
/// 2. Create bean in XML file in the directory "suite/invokers/" with this class
/// @dontinclude  invokers.conf.xml
/// @skip  begin: following section is used for docu generation - invoker bean
/// @until end: following section is used for docu generation - invoker bean
/// @n
///
/// 3. Create component @xlink{invoker} with type @xlink{invoker-class} and set attribute @xlink{invoker-class,class} with full class name of invoker
/// @dontinclude  test.description.conf.xml
/// @skip  begin: following section is used for docu generation - invoker usage
/// @until end: following section is used for docu generation - invoker usage
/// @n
/// @b Note:
/// @li full examples of the code are available in maven archetype-examples
/// @li instead of ${package} write the name of your package





/* **************** Base components ************************* */
/// @defgroup Main_Invokers_Base_group Invoker
/// @defgroup Main_Distributors_Base_group Distributor
/// @details @ref Main_Distributors_General_group
/// @defgroup Main_Collectors_Base_group Collector
/// @details @ref Main_Collectors_General_group
/// @defgroup Main_Services_Base_group Jagger service
/// @details @ref Main_Services_General_group
/// @defgroup Main_Listeners_Base_group Listener
/// @details @ref Main_Listeners_General_group
/// @defgroup Main_Aggregators_Base_group Aggregator
/// @details @ref Main_Aggregators_General_group


/* **************** Implementations ************************* */
/// @defgroup Main_Invokers_group Implementations of invokers
/// @defgroup Main_Providers_group Implementations of providers
/// @details @ref Main_Providers_General_group
/// @defgroup Main_Distributors_group Implementations of distributors
/// @details @ref Main_Distributors_General_group
/// @defgroup Main_Collectors_group Implementations of collectors
/// @details @ref Main_Collectors_General_group
/// @defgroup Main_Services_group Implementations of Jagger services
/// @details @ref Main_Services_General_group
/// @defgroup Main_Listeners_group Implementations of listeners
/// @details @ref Main_Listeners_General_group
/// @defgroup Main_Aggregators_group Implementations of aggregators
/// @details @ref Main_Aggregators_General_group
/// @defgroup Main_Terminators_group Implementations of termination strategies