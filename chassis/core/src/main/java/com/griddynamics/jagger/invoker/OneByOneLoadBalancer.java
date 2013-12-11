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

import java.util.Iterator;
import java.util.List;

/** Schedules queries across endpoints one by one
 * @author Mairbek Khadikov
 * @n
 * @par Details:
 * @details . "One by one" algorithm - for input endpoints [e1, e2] and queries [q1, q2, q3]
 * executes actions in following order: @n
 * (e1, q1), (e2, q1), (e1, q2), (e2, q2), (e1, q3), (e2, q3). @n
 *  @n
 * To use this distributor add @xlink{query-distributor} element with type @xlink_complex{query-distributor-one-by-one} in @xlink{scenario} block.
 *
 * @param <Q> Query type
 * @param <E> Endpoint type
 *
 * @ingroup Main_Distributors_group */
public class OneByOneLoadBalancer<Q, E> extends QueryPoolLoadBalancer<Q, E> {

    public OneByOneLoadBalancer(){
        super();
    }

    public OneByOneLoadBalancer(Iterable<Q> queryProvider, Iterable<E> endpointProvider){
        super(queryProvider, endpointProvider);
    }

    /** Returns an iterator over pairs
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details Returns an iterator over pairs, which were created by "One by one" algorithm
     *
     *  @return iterator over pairs */
    @Override
    public Iterator<Pair<Q, E>> provide() {
        final CircularSupplier<Q> querySupplier = CircularSupplier.create(queryProvider);
        final CircularSupplier<E> endpointSupplier = CircularSupplier.create(endpointProvider);

        return new Iterator<Pair<Q, E>>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Pair<Q, E> next() {
                boolean exceeded = endpointSupplier.exceeded();

                E endpoint = endpointSupplier.pop();
                Q query = exceeded ? querySupplier.pop() : querySupplier.peek();

                return Pair.of(query, endpoint);

            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Read only iterator");
            }

            @Override
            public String toString() {
                return "OneByOneLoadBalancer iterator";
            }

        };
    }

    @Override
    public String toString() {
        return "OneByOneLoadBalancer";
    }
}