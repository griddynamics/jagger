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

import com.griddynamics.jagger.util.Pair;

import java.io.Serializable;
import java.util.Iterator;

/** An object which provides pairs of queries and endpoints for Invoker
 * @author Grid Dynamics
 * @n
 * @par Details:
 * @details LoadBalancer (distributor) can use query and endpoint providers to load data and create pairs by some algorithm. @n
 * (if you choose @ref QueryPoolLoadBalancer<Q,E> as an abstract implementation). @n
 * You can use no providers and load all necessary data in your implementation of LoadBalancer. @n
 * @n
 * To view all distributors implementations click here @ref Main_Distributors_group
 *
 * @param <Q> - Query type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Distributors_Base_group */
public interface LoadBalancer<Q, E> extends Iterable<Pair<Q, E>>, Serializable {

    /** Returns an iterator over pairs
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details Scenario take the next pair of queries and endpoints and try to execute invocation with this data
     *
     *  @return iterator over pairs */
    Iterator<Pair<Q, E>> provide();

    /** Returns number of queries
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details Can be used by calibrator to create calibration process
     *
     *  @return number of queries */
    int querySize();

    /** Returns number of endpoints
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details Can be used by calibrator to create calibration process
     *
     *  @return number of endpoints*/
    int endpointSize();

}

/* **************** Distributors page *************************  */
/// @defgroup Main_Distributors_General_group Distributors main page
///
/// @li General information about interface: @ref Main_Distributors_Base_group
/// @li Available implementations: @ref Main_Distributors_group
/// @li How to customize: @ref Main_HowToCustomizeDistributors_group
/// @li How to run test with randomized order of requests: @ref Section_distributors_random
/// @n
/// @n
/// @details
/// @par General info
/// Distributors provide pairs of endpoints and queries for invokers @n
/// Before start of the test distributor is combining all endpoints and queries according to user setup and stores these combination in internal list. @n
/// \b Important: mentioned list is shared by all threads that produce load. it is not possible to have separate list per workload thread @n
/// Before every invoke distributor is providing single pair of endpoint / query to invoker @n
///
/// @par Example of distributor setup in XML:
/// Following XML code should be included in @xlink{test-description} section @n
/// @dontinclude  test.description.conf.xml
/// @skip  begin: following section is used for docu generation - invoker usage
/// @until end: following section is used for docu generation - invoker usage
///
/// @par Variants of distributors available in XML:
/// @xlink{query-distributor} - documentation of distributor element in XML schema @n
/// @xlink_complex{queryDistributorAbstract} - types of distributors available in XML schema. See <b> 'Sub Types' </b> section of man page @n
/// How distributors mentioned above are implemented you can see in section: @ref Main_Distributors_group @n
/// @n
/// @section Section_distributors_random Access SUT with random order of endpoint / query pairs
/// Pairs of endpoint / query are collected into list single time during Jagger start up. @n
/// By default all threads are accessing elements of mentioned list in the same order: from first element to the last, doing this in cycle. @n
/// If it is necessary to execute endpoint / query pairs in random order, set @xlink_complex{queryDistributorRandomAbstract,randomSeed} attribute of f.e. @xlink{query-distributor-round-robin} equal to some integer value @n
/// @image html jagger_random_query_distribution.png "Access SUT with random order of endpoint / query pairs"


/* **************** How to customize distributor ************************* */
/// @defgroup Main_HowToCustomizeDistributors_group Custom distributors
///
/// @details
/// @ref Main_Distributors_General_group
/// @n
/// @n
/// To add custom distributor you need to do:
///
/// 1. Create class which implements @ref Main_Distributors_Base_group interface or extends one of classes @ref Main_Distributors_group
/// @dontinclude RandomQueryDistributor.java
/// @skipline  public class RandomQueryDistributor
/// @n
///
/// 2. Create bean in XML file in the directory "suite/distributor/" with this class
/// @dontinclude  distributor.conf.xml
/// @skip  begin: following section is used for docu generation - distributor bean
/// @until end: following section is used for docu generation - distributor bean
/// @n
///
/// 3. Refer this class in your @xlink{scenario-query-pool} with element @xlink{query-distributor}
/// @dontinclude  test.description.conf.xml
/// @skip  begin: following section is used for docu generation - distributor usage
/// @until end: following section is used for docu generation - distributor usage
///
/// @b Note:
/// @li full examples of the code are available in maven archetype-examples
/// @li instead of ${package} write the name of your package



