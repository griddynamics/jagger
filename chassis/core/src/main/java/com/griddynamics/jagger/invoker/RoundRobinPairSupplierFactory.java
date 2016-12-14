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

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkState;

public class RoundRobinPairSupplierFactory<Q, E> implements PairSupplierFactory<Q, E> {
    @Override
    public PairSupplier<Q, E> create(Iterable<Q> queries, Iterable<E> endpoints) {
        ArrayList<Pair<Q, E>> tempList = new ArrayList<>();
        Iterator<E> endpointIt = endpoints.iterator();
        checkState(endpointIt.hasNext(), "Empty EndpointProvider");

        if (queries == null) {
            while (endpointIt.hasNext()) {
                tempList.add(Pair.of(null, endpointIt.next()));
            }
        } else {
            Iterator<Q> queryIt = queries.iterator();
            E currentEndpoint;
            Q currentQuery;
            while (endpointIt.hasNext() || queryIt.hasNext()) {
                if (!endpointIt.hasNext()) {
                    endpointIt = endpoints.iterator();
                }
                if (!queryIt.hasNext()) {
                    queryIt = queries.iterator();
                }

                currentEndpoint = endpointIt.next();
                currentQuery = queryIt.next();
                tempList.add(Pair.of(currentQuery, currentEndpoint));
            }
        }

        return new PairSupplierImpl<>(ImmutableList.copyOf(tempList));
    }
}
