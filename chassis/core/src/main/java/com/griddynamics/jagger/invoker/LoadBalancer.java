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
 * @ingroup Main_Distributors_group */
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
     *
     *  @return number of queries */
    int querySize();

    /** Returns number of endpoints
     * @author Grid Dynamics
     * @n
     *
     *  @return number of endpoints*/
    int endpointSize();

}
