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
 * Each thread will take Pair<Q, E> from one place.
 * Each thread has it own indexing.
 */
public class RoundRobinLoadBalancerShared<Q, E> extends QueryPoolLoadBalancer<Q, E> {

    public RoundRobinLoadBalancerShared(Iterable<Q> queryProvider, Iterable<E> endpointProvider){
        super(queryProvider, endpointProvider);

        pairSupplier = RoundRobinPairSupplier.create(queryProvider, endpointProvider);
    }

    private PairSupplier<Q, E> pairSupplier;

    @Override
    public Iterator<Pair<Q, E>> provide() {

        return new Iterator<Pair<Q, E>>() {

            private int index = 0;
            private int size = pairSupplier.size();

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Pair<Q, E> next() {
                if(index >= size) {
                    index = 0;
                }
                return pairSupplier.pop(index ++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Read only iterator");
            }

            @Override
            public String toString() {
                return "RoundRobinLoadBalancerShare iterator";
            }
        };
    }

    @Override
    public String toString() {
        return "RoundRobinLoadBalancerShare";
    }
}
