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

/** ??? Some short description
 * @author ???
 * @n
 * @par Details:
 * @details ???
 *
 * @param <Q> - Query type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Distributors_Base_group */
public interface LoadBalancer<Q, E> extends Iterable<Pair<Q, E>>, Serializable {

    /** ??? Some short description
     * @author ???
     * @n
     * @par Details:
     * @details ???
     *
     *  @return ??? */
    Iterator<Pair<Q, E>> provide();

    /** ??? Some short description
     * @author ???
     * @n
     * @par Details:
     * @details ???
     *
     *  @return ??? */
    int querySize();

    /** ??? Some short description
     * @author ???
     * @n
     * @par Details:
     * @details ???
     *
     *  @return ??? */
    int endpointSize();

}

/* **************** How to customize distributor ************************* */
/// @defgroup Main_HowToCustomizeDistributors_group Custom distributors
///
/// @details
/// @b Note: full examples of the code are available in maven archetype-examples
///
/// To add custom distributor you need to do:
///
/// 1. Create class which implements @ref Main_Distributors_Base_group interface or extends one of classes @ref Main_Distributors_group
/// @dontinclude RandomQueryDistributor.java
/// @skipline  public class RandomQueryDistributor
///
/// 2. Create bean in XML file in the directory "suite/distributor/" with this class
/// @dontinclude  distributor.conf.xml
/// @skip  begin: following section is used for docu generation - distributor bean
/// @until end: following section is used for docu generation - distributor bean
///
/// 3. Refer this class in test description XML file
/// @dontinclude  test.suite.scenario.config.xml
/// @skip  begin: following section is used for docu generation - distributor usage
/// @until end: following section is used for docu generation - distributor usage



