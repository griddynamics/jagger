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

/**
 * Schedules queries across endpoints one by one. For input: endpoints [e1,
 * e2] and queries [q1, q2, q3] executes actions in following order: (e1, q1),
 * (e2, q1), (e1, q2), (e2, q2), (e1, q3), (e2, q3).
 *
 * @param <Q> Query type
 * @param <E> Endpoint type
 * @author Mairbek Khadikov
 */
public class OneByOneLoadBalancer<Q, E> extends QueryPoolLoadBalancer<Q, E> {

    private PairSupplier<Q, E> pairSupplier = null;

    public OneByOneLoadBalancer(){
        super();
    }

    public OneByOneLoadBalancer(Iterable<Q> queryProvider, Iterable<E> endpointProvider){
        super(queryProvider, endpointProvider);
    }

    public void setPairSupplier(PairSupplier<Q, E> pairSupplier) {
        this.pairSupplier = pairSupplier;
    }

    @Override
    public Iterator<Pair<Q, E>> provide() {

        return new Iterator<Pair<Q, E>>() {

            private int index = 0;
            private int size = getPairSupplier().size();

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Pair<Q, E> next() {
                if(index >= size) {
                    index = 0;
                }
                return getPairSupplier().get(index++);
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

    public PairSupplier<Q, E> getPairSupplier() {
        if(pairSupplier == null) {
            pairSupplier = OneByOnePairSupplier.create(queryProvider, endpointProvider);
        }
        return pairSupplier;
    }

    @Override
    public String toString() {
        return "OneByOneLoadBalancer";
    }
}