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

/** LoadBalancer which uses query and endpoint provider
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details Abstract implementation of LoadBalancer. Contains query and endpoint providers.
 * Create pairs of queries and endpoints, which you can take from providers, in your implementation.
 *
 * @ingroup Main_Distributors_group */
public abstract class QueryPoolLoadBalancer<Q, E> implements LoadBalancer<Q, E> {

    protected Iterable<Q> queryProvider;
    protected Iterable<E> endpointProvider;

    public QueryPoolLoadBalancer(){
    }

    public QueryPoolLoadBalancer(Iterable<Q> queryProvider, Iterable<E> endpointProvider){
        this.queryProvider = queryProvider;
        this.endpointProvider = endpointProvider;
    }

    public void setQueryProvider(Iterable<Q> queryProvider){
        this.queryProvider = queryProvider;
    }

    public void setEndpointProvider(Iterable<E> endpointProvider){
        this.endpointProvider = endpointProvider;
    }

    @Override
    public final Iterator<Pair<Q, E>> iterator() {
        return provide();
    }

    @Override
    public int endpointSize() {
        return getIterableSize(endpointProvider);
    }

    @Override
    public int querySize() {
        return getIterableSize(queryProvider);
    }

    public int getIterableSize(Iterable iterable){
        if (iterable == null)
            return 0;

        Iterator<Q> iterator = iterable.iterator();
        int size = 0;
        while (iterator.hasNext()){
            iterator.next();
            size++;
        }
        return size;
    }
}