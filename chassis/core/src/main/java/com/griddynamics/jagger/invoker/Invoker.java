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

/// @li @ref Main_HowToCustomizeInvokers_group
/// @li @ref Main_HowToCustomizeProviders_group

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





/* **************** Base components ************************* */
/// @defgroup Main_Invokers_Base_group Invoker

/* **************** Implementations ************************* */
/// @defgroup Main_Invokers_group Implementations of invokers
/// @defgroup Main_Providers_group Implementations of providers
/// @defgroup Main_Terminators_group Implementations of termination strategies