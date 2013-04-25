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

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.util.Pair;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Structure that RoundRobinLoadBalancerShared uses
 */
public class RoundRobinPairSupplier<Q, E> implements PairSupplier<Q, E>, Serializable {

    private final ImmutableList<Pair<Q, E>> list;

    public static <Q, E> RoundRobinPairSupplier<Q, E> create(Iterable<Q> querys, Iterable<E> endpoints) {

        LinkedList<Pair<Q,E>> tempList = new LinkedList<Pair<Q, E>>();
        Iterator<E> endpointIt = endpoints.iterator();
        Iterator<Q> queryIt = querys.iterator();

        E currentEndpoint;
        Q currentQuery;
        while(endpointIt.hasNext() || queryIt.hasNext()) {
                if(!endpointIt.hasNext()) {
                    endpointIt = endpoints.iterator();
                }
                if(!queryIt.hasNext()) {
                    queryIt = querys.iterator();
                }
                currentEndpoint = endpointIt.next();
                currentQuery = queryIt.next();

                tempList.add(Pair.of(currentQuery, currentEndpoint));
        }

        return new RoundRobinPairSupplier<Q, E>(ImmutableList.copyOf(tempList));
    }

    private RoundRobinPairSupplier(ImmutableList<Pair<Q, E>> iterable) {
        this.list = iterable;
    }

    public int size() {
        return list.size();
    }

    public Pair<Q, E> pop(int index) {
        return list.get(index);
    }
}