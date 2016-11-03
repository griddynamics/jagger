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

package com.griddynamics.jagger.invoker.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;

import java.net.MalformedURLException;
import java.util.concurrent.ConcurrentHashMap;

/** Abstract class for invokers which create request to Hessian services
 * @author Mairbek Khadikov
 * @n
 *
 * @param <Q> - type of input query
 * @param <R> - type of invocation result
 * @param <S> - type of Hessian service
 *
 * @ingroup Main_Invokers_group */
@Deprecated
public abstract class HessianInvoker<S, Q, R> implements Invoker<Q, R, String> {
    private final ConcurrentHashMap<String, S> services = new ConcurrentHashMap<String, S>();

    @Override
    public final R invoke(Q query, String endpoint) throws InvocationException {
        Preconditions.checkNotNull(query);
        Preconditions.checkNotNull(endpoint);

        S service = getService(endpoint);
        return invokeService(service, query);
    }

    private S getService(String url) {
        S service = services.get(url);
        if (service == null) {
            service = initService(url);
            services.putIfAbsent(url, service);
            service = services.get(url);
        }

        return service;
    }

    @SuppressWarnings("unchecked")
    protected S initService(String url) {
        try {
            HessianProxyFactory factory = new HessianProxyFactory();
            factory.setOverloadEnabled(true);

            return (S) factory.create(getClazz(), url);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }
    }

    /** Returns class of Hessian service
     * @author Mairbek Khadikov
     * @n
     *
     * @return class of Hessian service */
    protected abstract Class<S> getClazz();

    /** Creates invocation to specified Hessian service
     * @author Mairbek Khadikov
     * @n
     * @param query   - query for invocation
     * @param service - Hessian service
     *
     * @return result of invocation */
    protected abstract R invokeService(S service, Q query);

}
