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

import java.util.Iterator;
import java.util.LinkedList;

import static com.google.common.base.Preconditions.checkState;

public class OneByOnePairSupplierFactory<Q, E> implements PairSupplierFactory<Q, E> {

    @Override
    public PairSupplier<Q, E> create(Iterable<Q> queries, Iterable<E> endpoints) {

        LinkedList<Pair<Q, E>> tempList = new LinkedList<Pair<Q, E>>();
        Iterator<E> endpointIt = endpoints.iterator();
        Iterator<Q> queryIt = queries.iterator();

        checkState(endpointIt.hasNext(), "Empty EndpointProvider");
        checkState(queryIt.hasNext(), "Empty QueryProvider");

        while(queryIt.hasNext()) {
            Q currentQuery = queryIt.next();
            while(endpointIt.hasNext()) {
                E currentEndpoint = endpointIt.next();
                tempList.add(Pair.of(currentQuery, currentEndpoint));
            }
            endpointIt = endpoints.iterator();
        }

        return new PairSupplierImpl<Q, E>(ImmutableList.copyOf(tempList));
    }
}
