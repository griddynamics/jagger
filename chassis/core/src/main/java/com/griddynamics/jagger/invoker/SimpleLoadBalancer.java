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

import java.util.Iterator;

/** Contains only one query and endpoint
 * @author Dmitry Kotlyarov
 * @n
 * @par Details:
 * @details Creates an iterator over one pair of query and endpoint
 *
 * @param <Q> - Query type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Distributors_group */
public class SimpleLoadBalancer<Q, E> implements LoadBalancer<Q, E> {
    private final Q query;
    private final E endpoint;

    public SimpleLoadBalancer(Q query, E endpoint) {
        this.query = query;
        this.endpoint = endpoint;
    }

    /** Always returns an exact pair of endpoint and query
     * @author Grid Dynamics
     * @n
     *
     *  @return iterator over pair */
    @Override
    public Iterator<Pair<Q, E>> provide() {
        return new Iterator<Pair<Q, E>>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Pair<Q, E> next() {
                return Pair.of(query, endpoint);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Read only iterator");
            }
        };
    }

    @Override
    public int querySize() {
        return 1;
    }

    @Override
    public int endpointSize() {
        return 1;
    }

    @Override
    public Iterator<Pair<Q, E>> iterator() {
        return provide();
    }
}
