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

/** Responsible for action invocation on specified endpoint
 * @author Mairbek Khadikov
 * @n
 * @par Details:
 * @details ???
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Invokers_Base_group */
public interface Invoker<Q,R,E> extends Serializable {

	/** Executes action with given input parameters.
     * @author Mairbek Khadikov
     * @n
     * @par Details:
     * @details ???
     *
	 *  @param query    - input data for the invocation
	 *  @param endpoint - endpoint
     *
     *  @return invocation result
     *  @throws InvocationException when invocation failed */
      R invoke(Q query, E endpoint) throws InvocationException;

}

/* Below is doxygen documentation for Jagger customization */

/// @mainpage Custom components for Jagger
/// @n
/// @section Main_general_sec General approach to customize Jagger components
///
/// @li @ref Main_Customize_group
///
/// @n
/// @section Main_detailes_sec Detailed description how customize Jagger components
///
/// @li @ref Main_HowToCustomizeInvokers_group
/// @li @ref Main_HowToCustomizeProviders_group
/// @li @ref Main_HowToCustomizeDistributors_group
/// @li @ref Main_HowToCustomizeCollectors_group
/// @li @ref Main_HowToCustomizeCalculators_group
/// @li @ref Main_HowToCustomizeDecisionMakers_group
/// @li @ref Main_HowToCustomizeTerminators_group
///
/// @n
/// @section Main_override_sec Jagger implementations of base components that can be overrode
/// All base components have some default implementations in Jagger. You can override base components or @n
/// listed below implementations to create custom components @n
///
/// @li @ref Main_Invokers_group
/// @li @ref Main_Providers_group
/// @li @ref Main_Distributors_group
/// @li @ref Main_Collectors_group
/// @li @ref Main_Calculators_group
/// @li @ref Main_DecisionMakers_group
/// @li @ref Main_Terminators_group
///
///

/// @defgroup Main_Customize_group How to customize Jagger components
///
/// @par Intro
///
/// All Jagger components that can be customized are either classes or interfaces @n
/// You can override either some abstract base class or some implementation of the class available in Jagger @n
/// @n
/// @par General approach
///
/// Code of examples in this section is truncated to give general overview. @n
/// Detailed examples are presented for every component that can be overrode in appropriate section @n
///
/// @b 1. Create custom class which implements some Jagger interface @n
///    or extends some Jagger class @n
/// - Example for interface:
/// @dontinclude  PageVisitorInvoker.java
/// @skipline  public class PageVisitorInvoker
/// - Example for class:
/// @dontinclude  RandomQueryDistributor.java
/// @skipline  public class RandomQueryDistributor
///
/// @b 2. Create bean in configuration XML file with this class @n
/// - Example for interface:
/// @dontinclude  invokers.conf.xml
/// @skip  begin: following section is used for docu generation - invoker bean
/// @until end: following section is used for docu generation - invoker bean
/// - Example for class:
/// @dontinclude  distributor.conf.xml
/// @skip  begin: following section is used for docu generation - distributor bean
/// @until end: following section is used for docu generation - distributor bean
///
/// @b 3. Refer this class in test description XML file @n
/// - Example for interface:
/// @dontinclude  test.suite.scenario.config.xml
/// @skip  begin: following section is used for docu generation - invoker usage
/// @until end: following section is used for docu generation - invoker usage
/// - Example for class:
/// @dontinclude  test.suite.scenario.config.xml
/// @skip  begin: following section is used for docu generation - distributor usage
/// @until end: following section is used for docu generation - distributor usage

/* **************** How to customize invoker ************************* */
/// @defgroup Main_HowToCustomizeInvokers_group Custom invokers
///
/// @details
/// @b Note: full examples of the code are available in maven archetype-examples
///
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
/// 3. Create component "invoker" with type "invoker-class" and set attribute "class" with full class name of invoker
/// @dontinclude  test.suite.scenario.config.xml
/// @skip  begin: following section is used for docu generation - invoker usage
/// @until end: following section is used for docu generation - invoker usage


/* **************** Base components ************************* */
/// @defgroup Main_Invokers_Base_group Invoker
/// @details Invokers define how to transfer some query (request) to some endpoint (system under test)

/// @defgroup Main_Providers_Base_group Provider
/// @details ??? Some general description is required here

/// @defgroup Main_Distributors_Base_group Distributor
/// @details User is providing list of queries and endpoints he wants to check during some test. @n
/// Distributors define how to combine query (request) & endpoint (system under test) to pairs @n
/// according to some algorithm and pass these pairs to Invoker

/// @defgroup Main_Collectors_Base_group Collector
/// @details ??? Some general description is required here

/// @defgroup Main_Calculators_Base_group Calculator
/// @details ??? Some general description is required here

/// @defgroup Main_DecisionMakers_Base_group Decision Maker
/// @details ??? Some general description is required here

/// @defgroup Main_Terminators_Base_group Termination Strategy
/// @details ??? Some general description is required here


/* **************** Implementations ************************* */
/// @defgroup Main_Invokers_group Implementations of invokers
/// @details ??? Some general description is required here

/// @defgroup Main_Providers_group Implementations of providers
/// @details ??? Some general description is required here

/// @defgroup Main_Distributors_group Implementations of distributors
/// @details ??? Some general description is required here

/// @defgroup Main_Collectors_group Implementations of collectors
/// @details ??? Some general description is required here

/// @defgroup Main_Calculators_group Implementations of calculators
/// @details ??? Some general description is required here

/// @defgroup Main_DecisionMakers_group Implementations of decision makers
/// @details ??? Some general description is required here

/// @defgroup Main_Terminators_group Implementations of termination strategies
/// @details ??? Some general description is required here


/* *********************** Currently not used ****************************** */
/// @n
/// @section Main_override_base_sec Jagger base components that can be overrode
///
/// @li @ref Main_Invokers_Base_group
/// @li @ref Main_Providers_Base_group
/// @li @ref Main_Distributors_Base_group
/// @li @ref Main_Collectors_Base_group
/// @li @ref Main_Calculators_Base_group
/// @li @ref Main_DecisionMakers_Base_group
/// @li @ref Main_Terminators_Base_group
///
