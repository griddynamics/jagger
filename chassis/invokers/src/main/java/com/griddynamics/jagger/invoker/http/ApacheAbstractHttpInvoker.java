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

/** This class creates http request to the services of SuT
 * @author Evelina Stepanova
 * @n
 * @par Details:
 * @details An abstract implementation of http invoker. It based on apache http-client. Create http-requests and params from your queries and endpoints.
 *
 * @param <Q> -query type
 *
 * @ingroup Main_Invokers_group */
@Deprecated
public abstract class ApacheAbstractHttpInvoker<Q> implements Invoker<Q, HttpResponse, String> {
    private static final Logger log = LoggerFactory.getLogger(ApacheAbstractHttpInvoker.class);

    private AbstractHttpClient httpClient;

    @Required
    public void setHttpClient(AbstractHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /** Executes http request via apache http client
     * @author Mairbek Khadikov
     * @n
     *
     * @param query    - input data for the invocation
     * @param endpoint - url of target service
     *
     * @return apache http response
     * @throws InvocationException when invocation failed */
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

    /** Creates request(GET, POST, etc) from query and endpoint
     * @author Evelina Stepanova
     * @n
     *
     * @param query - a query of Q type
     * @param endpoint - location of target
     * @return apache request base
     */
    protected abstract HttpRequestBase getHttpMethod(Q query, String endpoint);

    /** Extract client params from query
     * @author Evelina Stepanova
     * @n
     *
     * @param query - a query of Q type
     * @return params for apache http client
     */
    protected abstract HttpParams getHttpClientParams(Q query);
}
