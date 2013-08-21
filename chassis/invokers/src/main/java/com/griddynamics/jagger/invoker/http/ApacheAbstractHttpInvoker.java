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
package com.griddynamics.jagger.invoker.http;

import com.google.common.base.Preconditions;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/** ??? Some short description
 * @author ???
 * @n
 * @par Details:
 * @details ???
 *
 * @param <Q> -???
 *
 * @ingroup Main_Invokers_group */
public abstract class ApacheAbstractHttpInvoker<Q> implements Invoker<Q, HttpResponse, String> {
    private static final Logger log = LoggerFactory.getLogger(ApacheAbstractHttpInvoker.class);

    private AbstractHttpClient httpClient;

    @Required
    public void setHttpClient(AbstractHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public final HttpResponse invoke(Q query, String endpoint) throws InvocationException {
        Preconditions.checkNotNull(query);
        Preconditions.checkNotNull(endpoint);

        HttpRequestBase method = null;
        HttpEntity response = null;
        try {
            method = getHttpMethod(query, endpoint);
            method.setParams(getHttpClientParams(query));

            org.apache.http.HttpResponse httpResponse = httpClient.execute(method);
            response = httpResponse.getEntity();
            return HttpResponse.create(httpResponse.getStatusLine().getStatusCode(), EntityUtils.toString(response));
        } catch (Exception e) {
            if (method != null) {
                log.debug("Error during invocation with URL: " + method.getURI() +
                        ", endpoint: " + endpoint + ", query: " + query, e);
            } else {
                log.debug("Error during invocation with: endpoint: " + endpoint + ", query: " + query, e);
            }
            throw new InvocationException("InvocationException : ", e);
        } finally {
            EntityUtils.consumeQuietly(response);
        }
    }

    protected abstract HttpRequestBase getHttpMethod(Q query, String endpoint);

    protected abstract HttpParams getHttpClientParams(Q query);
}
